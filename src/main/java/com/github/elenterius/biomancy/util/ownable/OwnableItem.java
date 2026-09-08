package com.github.elenterius.biomancy.util.ownable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Optional;
import java.util.UUID;

public interface OwnableItem {
	String NBT_KEY = "OwnerUUID";

	default Optional<UUID> getOwner(ItemStack stack) {
		CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (nbt.hasUUID(NBT_KEY)) {
			return Optional.of(nbt.getUUID(NBT_KEY));
		}
		return Optional.empty();
	}

	default void setOwner(ItemStack stack, UUID uuid) {
		CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		nbt.putUUID(NBT_KEY, uuid);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
	}

	default void removeOwner(ItemStack stack) {
		CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		nbt.remove(NBT_KEY);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
	}

	default boolean hasOwner(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().hasUUID(NBT_KEY);
	}

	default boolean isOwner(ItemStack stack, UUID uuid) {
		return getOwner(stack).map(value -> value.equals(uuid)).orElse(false);
	}

}
