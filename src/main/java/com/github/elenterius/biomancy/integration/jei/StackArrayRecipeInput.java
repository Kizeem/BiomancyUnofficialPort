package com.github.elenterius.biomancy.integration.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public final class StackArrayRecipeInput implements RecipeInput {

	private final NonNullList<ItemStack> stacks;

	public StackArrayRecipeInput(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public void setStack(int index, ItemStack stack) {
		stacks.set(index, stack);
	}

	@Override
	public ItemStack getItem(int index) {
		return index >= 0 && index < stacks.size() ? stacks.get(index) : ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return stacks.size();
	}
}
