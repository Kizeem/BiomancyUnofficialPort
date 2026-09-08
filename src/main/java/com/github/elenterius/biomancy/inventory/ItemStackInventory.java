package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemStackInventory {

	private final ItemStack cachedInventoryHost;
	private final InventorySerializer serializer;

	private final ItemStackHandler itemHandler;

	ItemStackInventory(int slots, int maxSlotSize, ItemStack inventoryHost, InventorySerializer serializer) {
		this.serializer = serializer;
		itemHandler = new ItemStackHandler(slots) {
			@Override
			public int getSlotLimit(int slot) {
				return maxSlotSize;
			}

			@Override
			protected void onContentsChanged(int slot) {
				serializeToHost();
			}
		};
		cachedInventoryHost = inventoryHost;
	}

	public static ItemStackInventory create(int slots, int maxSlotSize, ItemStack inventoryHost, InventorySerializer inventorySerializer) {
		ItemStackInventory inventory = new ItemStackInventory(slots, maxSlotSize, inventoryHost, inventorySerializer);
		inventory.deserializeFromHost();
		return inventory;
	}

	public static ItemStackInventory create(int slots, int maxSlotSize, ItemStack inventoryHost) {
		ItemStackInventory inventory = new ItemStackInventory(slots, maxSlotSize, inventoryHost, InventorySerializer.DEFAULT);
		inventory.deserializeFromHost();
		return inventory;
	}

	private void serializeToHost() {
		CompoundTag tag = serializer.getStoreTag(cachedInventoryHost);
		serializer.serialize(tag, cachedInventoryHost, itemHandler, RegistryAccess.EMPTY);
		serializer.setStoreTag(cachedInventoryHost, tag);
	}

	private void deserializeFromHost() {
		CompoundTag tag = serializer.getStoreTag(cachedInventoryHost);
		serializer.deserialize(tag, cachedInventoryHost, itemHandler, RegistryAccess.EMPTY);
	}

	public boolean stillValid(Player player) {
		return !cachedInventoryHost.isEmpty();
	}

	private static NonNullList<ItemStack> toStackList(ItemStackHandler handler) {
		NonNullList<ItemStack> list = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
		for (int i = 0; i < handler.getSlots(); i++) {
			list.set(i, handler.getStackInSlot(i));
		}
		return list;
	}

	public IItemHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return itemHandler;
	}

	public interface InventorySerializer {
		String NBT_KEY = "Inventory";

		InventorySerializer DEFAULT = new InventorySerializer() {};

		InventorySerializer BLOCK_ENTITY_TAG = new InventorySerializer() {
			@Override
			public CompoundTag getStoreTag(ItemStack stack) {
				return stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
			}

			@Override
			public void setStoreTag(ItemStack stack, CompoundTag tag) {
				if (tag.isEmpty()) {
					stack.remove(DataComponents.BLOCK_ENTITY_DATA);
				}
				else {
					stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
				}
			}
		};

		InventorySerializer CONTAINER = new InventorySerializer() {
			@Override
			public CompoundTag getStoreTag(ItemStack stack) {
				return new CompoundTag();
			}

			@Override
			public void setStoreTag(ItemStack stack, CompoundTag tag) {
			}

			@Override
			public void serialize(CompoundTag store, ItemStack host, INBTSerializable<CompoundTag> serializable, HolderLookup.Provider provider) {
				ItemContainerContents contents = ItemContainerContents.fromItems(toStackList((ItemStackHandler) serializable));
				if (contents == ItemContainerContents.EMPTY) {
					host.remove(DataComponents.CONTAINER);
				}
				else {
					host.set(DataComponents.CONTAINER, contents);
				}
			}

			@Override
			public void deserialize(CompoundTag store, ItemStack host, INBTSerializable<CompoundTag> deserializable, HolderLookup.Provider provider) {
				ItemStackHandler handler = (ItemStackHandler) deserializable;
				NonNullList<ItemStack> list = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
				host.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);
				for (int i = 0; i < list.size(); i++) {
					handler.setStackInSlot(i, list.get(i));
				}
			}
		};

		default CompoundTag getStoreTag(ItemStack stack) {
			return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		}

		default void setStoreTag(ItemStack stack, CompoundTag tag) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}

		default void serialize(CompoundTag store, ItemStack host, INBTSerializable<CompoundTag> serializable, HolderLookup.Provider provider) {
			store.put(NBT_KEY, serializable.serializeNBT(provider));
		}

		default void deserialize(CompoundTag store, ItemStack host, INBTSerializable<CompoundTag> deserializable, HolderLookup.Provider provider) {
			deserializable.deserializeNBT(provider, store.getCompound(NBT_KEY));
		}

		default CompoundTag unwrap(CompoundTag store) {
			return store.getCompound(NBT_KEY);
		}
	}

}
