package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.FoodDigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.StaticDigestingRecipe;
import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.ArrayList;
import java.util.List;

public final class DigestingRecipes {

	private DigestingRecipes() {}

	public static List<RecipeHolder<DigestingRecipe>> getRecipes(ClientLevel level) {
		List<RecipeHolder<DigestingRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(mojangRecipeType(ModRecipes.DIGESTING_RECIPE_TYPE.get(), DigestingRecipe.class));

		List<RecipeHolder<DigestingRecipe>> resolvedRecipes = new ArrayList<>();

		for (RecipeHolder<DigestingRecipe> holder : allRecipes) {
			if (holder.value() instanceof FoodDigestingRecipe dynamicRecipe) {
				resolvedRecipes.addAll(convertToStaticRecipes(level, holder.id(), dynamicRecipe));
			}
			else {
				resolvedRecipes.add(holder);
			}
		}

		return resolvedRecipes;
	}

	private static List<RecipeHolder<DigestingRecipe>> convertToStaticRecipes(ClientLevel level, ResourceLocation baseId, FoodDigestingRecipe dynamicRecipe) {
		List<RecipeHolder<DigestingRecipe>> staticRecipes = new ArrayList<>();

		for (ItemStack ingredientItem : dynamicRecipe.getIngredient().getItems()) {
			ContainerRecipeInput inputInventory = new ContainerRecipeInput(new SingleRecipeInput(ingredientItem));

			ItemStack result = dynamicRecipe.assemble(inputInventory, level.registryAccess());
			int craftingTimeTicks = dynamicRecipe.getCraftingTimeTicks(inputInventory);
			int craftingCostNutrients = dynamicRecipe.getCraftingCostNutrients(inputInventory);
			Ingredient ingredient = Ingredient.of(ingredientItem);

			String suffix = BuiltInRegistries.ITEM.getKey(ingredientItem.getItem()).toLanguageKey();
			ResourceLocation recipeId = baseId.withSuffix("_jei_" + suffix);
			StaticDigestingRecipe recipe = new StaticDigestingRecipe(recipeId, result, craftingTimeTicks, craftingCostNutrients, ingredient);

			staticRecipes.add(new RecipeHolder<>(recipeId, recipe));
		}

		return staticRecipes;
	}

	@SuppressWarnings("unchecked")
	private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeType<T> mojangRecipeType(RecipeType<?> recipeType, Class<T> recipeClass) {
		return (RecipeType<T>) recipeType;
	}

}