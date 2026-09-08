package com.github.elenterius.biomancy.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ContainerRecipeInput(RecipeInput input) implements RecipeInput {

	@Override
	public ItemStack getItem(int slotIndex) {
		return input.getItem(slotIndex);
	}

	@Override
	public int size() {
		return input.size();
	}

}
