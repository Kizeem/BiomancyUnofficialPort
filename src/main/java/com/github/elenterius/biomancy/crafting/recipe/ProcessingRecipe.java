package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;

public sealed interface ProcessingRecipe extends RecipeWithMatchPriority permits DigestingRecipe, DynamicProcessingRecipe, StaticProcessingRecipe {

	ResourceLocation getId();

	int getCraftingTimeTicks(RecipeInput inputInventory);

	int getCraftingCostNutrients(RecipeInput inputInventory);

	default boolean isRecipeEqual(ProcessingRecipe other) {
		return getId().equals(other.getId());
	}

}
