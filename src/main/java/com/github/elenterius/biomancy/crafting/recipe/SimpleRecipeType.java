package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class SimpleRecipeType<T extends Recipe<ContainerRecipeInput>> implements RecipeType<T> {

	private final String identifier;

	public SimpleRecipeType(String identifier) {
		this.identifier = identifier;
	}

	public String getId() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier;
	}

	public static class AdvancedRecipeType<R extends Recipe<ContainerRecipeInput>> extends SimpleRecipeType<R> {

		public AdvancedRecipeType(String identifier) {
			super(identifier);
		}

		public Optional<R> getRecipeById(Level level, ResourceLocation id) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.byKey(id)
					.filter(recipeHolder -> recipeHolder.value().getType() == this)
					.map(RecipeHolder::value)
					.map(this::castRecipe);
		}

		public Optional<R> getFirstRecipeFor(Level level, RecipeInput inputInventory) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.getRecipeFor(this, new ContainerRecipeInput(inputInventory), level).map(RecipeHolder::value);
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<R> getBestRecipeFor(Level level, RecipeInput inputInventory) {
			ContainerRecipeInput recipeInput = new ContainerRecipeInput(inputInventory);

			R topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (R recipe : getRecipes(level)) {
				if (!recipe.matches(recipeInput, level)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipe = recipe;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		private @Nullable R castRecipe(@Nullable Recipe<?> recipe) {
			//noinspection unchecked
			return (R) recipe;
		}

		private boolean matches(R recipe, ItemStack stack) {
			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.test(stack)) return true;
			}
			return false;
		}

		private List<R> getRecipes(Level level) {
			return level.getRecipeManager().getAllRecipesFor(this).stream().map(RecipeHolder::value).toList();
		}

		private Map<ResourceLocation, R> getRecipesById(Level level) {
			return level.getRecipeManager().getAllRecipesFor(this).stream().collect(Collectors.toMap(RecipeHolder::id, RecipeHolder::value));
		}

		public Optional<R> getFirstRecipeForIngredient(Level level, ItemStack stack) {
			return getRecipes(level).stream().filter(recipe -> matches(recipe, stack)).findFirst();
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<R> getBestRecipeForIngredient(Level level, ItemStack stack) {
			R topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (R recipe : getRecipes(level)) {
				if (!matches(recipe, stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipe = recipe;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		public Optional<Pair<ResourceLocation, R>> getFirstRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			Map<ResourceLocation, R> map = getRecipesById(level);
			if (lastRecipeId != null) {
				R recipe = map.get(lastRecipeId);
				if (recipe != null && matches(recipe, stack)) {
					return Optional.of(Pair.of(lastRecipeId, recipe));
				}
			}

			return map.entrySet().stream()
					.filter(entry -> matches(entry.getValue(), stack))
					.findFirst()
					.map(entry -> Pair.of(entry.getKey(), entry.getValue()));
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<Pair<ResourceLocation, R>> getBestRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			Map<ResourceLocation, R> typedRecipes = getRecipesById(level);
			if (lastRecipeId != null) {
				R recipe = typedRecipes.get(lastRecipeId);
				if (recipe != null && matches(recipe, stack)) {
					return Optional.of(Pair.of(lastRecipeId, recipe));
				}
			}

			Map.Entry<ResourceLocation, R> topRecipeEntry = null;
			int topPriority = Integer.MIN_VALUE;

			for (Map.Entry<ResourceLocation, R> recipeEntry : typedRecipes.entrySet()) {
				R recipe = recipeEntry.getValue();
				if (!matches(recipe, stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipeEntry = recipeEntry;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipeEntry)
					.map(entry -> Pair.of(entry.getKey(), entry.getValue()));
		}

	}

}