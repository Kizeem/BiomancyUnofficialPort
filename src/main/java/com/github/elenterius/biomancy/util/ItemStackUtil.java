package com.github.elenterius.biomancy.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import javax.annotation.Nullable;

public final class ItemStackUtil {

	private ItemStackUtil() {}

	@Nullable
	public static CompoundTag getBlockEntityData(ItemStack stack) {
		CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		return customData == null ? null : customData.copyTag();
	}

}
