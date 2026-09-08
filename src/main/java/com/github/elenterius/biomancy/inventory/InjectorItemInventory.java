package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.api.serum.SerumContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandler;

public class InjectorItemInventory {

	private final ItemStack cachedInventoryHost;
	private final LargeSingleItemStackHandler itemHandler;

	private InjectorItemInventory(short maxSlotSize, ItemStack inventoryHost) {
		itemHandler = new LargeSingleItemStackHandler(maxSlotSize) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof SerumContainer;
			}

			@Override
			protected void onContentsChanged() {
				serializeToHost();
			}
		};
		cachedInventoryHost = inventoryHost;
	}

	public static InjectorItemInventory create(short maxSlotSize, ItemStack inventoryHost) {
		InjectorItemInventory inventory = new InjectorItemInventory(maxSlotSize, inventoryHost);
		inventory.deserializeFromHost();
		return inventory;
	}

	private void serializeToHost() {
		CompoundTag tag = cachedInventoryHost.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.put("inventory", itemHandler.serializeNBT(RegistryAccess.EMPTY));
		cachedInventoryHost.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	private void deserializeFromHost() {
		CompoundTag tag = cachedInventoryHost.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		itemHandler.deserializeNBT(RegistryAccess.EMPTY, tag.getCompound("inventory"));
	}

	public boolean stillValid() {
		return !cachedInventoryHost.isEmpty();
	}

	public LargeSingleItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return itemHandler;
	}

}
