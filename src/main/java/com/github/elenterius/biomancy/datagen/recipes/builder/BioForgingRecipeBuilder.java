package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class BioForgingRecipeBuilder implements RecipeBuilder<BioForgingRecipeBuilder> {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.BIO_FORGING_RECIPE_TYPE.getId().getPath();

	private final ResourceLocation recipeId;

	private final List<ICondition> conditions = new ArrayList<>();
	private final ItemData result;
	private final List<IngredientStack> ingredients = new ArrayList<>();
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
	private BioForgeTab category = ModBioForgeTabs.MISC.get();

	private int craftingCostNutrients = -1;
	private boolean criteriaAdded = false;

	private BioForgingRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		this.result = result;
	}

	public static BioForgingRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new BioForgingRecipeBuilder(recipeId, result);
	}

	public static BioForgingRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(modId, outputName);
		return new BioForgingRecipeBuilder(rl, result);
	}

	public static BioForgingRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(outputName);
		return new BioForgingRecipeBuilder(rl, result);
	}

	public static BioForgingRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(result.getItemPath());
		return new BioForgingRecipeBuilder(rl, result);
	}

	public static BioForgingRecipeBuilder create(ItemStack stack) {
		return create(new ItemData(stack));
	}

	public static BioForgingRecipeBuilder create(ItemLike item) {
		return create(new ItemData(item));
	}

	public static BioForgingRecipeBuilder create(ItemLike item, int count) {
		return create(new ItemData(item, count));
	}

	public BioForgingRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
		return this;
	}

	public BioForgingRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public BioForgingRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public BioForgingRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public BioForgingRecipeBuilder setCategory(BioForgeTab category) {
		this.category = category;
		return this;
	}

	public BioForgingRecipeBuilder setCategory(Supplier<BioForgeTab> category) {
		this.category = category.get();
		return this;
	}

	public BioForgingRecipeBuilder addIngredient(TagKey<Item> tag) {
		return addIngredient(Ingredient.of(tag));
	}

	public BioForgingRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
		return addIngredient(Ingredient.of(tag), quantity);
	}

	public BioForgingRecipeBuilder addIngredient(ItemLike item) {
		return addIngredient(item, 1);
	}

	public BioForgingRecipeBuilder addIngredient(Ingredient ingredient) {
		return addIngredient(ingredient, 1);
	}

	public BioForgingRecipeBuilder addIngredient(ItemLike item, int quantity) {
		addIngredient(Ingredient.of(item), quantity);
		return this;
	}

	public BioForgingRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
		ingredients.add(new IngredientStack(ingredient, quantity));
		return this;
	}

	@Override
	public BioForgingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		advancement.addCriterion(name, criterion);
		criteriaAdded = true;
		return this;
	}

	public void save(RecipeOutput output) {
		validateCriteria();

		if (craftingCostNutrients < 0) {
			craftingCostNutrients = BioForgingRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS;
		}

		advancement.parent(ResourceLocation.parse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);

		String folderName = RecipeBuilder.getRecipeFolderName(null, BiomancyMod.MOD_ID);
		ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), "recipes/%s/%s".formatted(folderName, recipeId.getPath()));

		AdvancementHolder advancementHolder = advancement.build(advancementId);
		BioForgingRecipe recipe = new BioForgingRecipe(recipeId, category, result.toStack(), ingredients, craftingCostNutrients);
		output.accept(recipeId, recipe, advancementHolder, conditions.toArray(ICondition[]::new));
	}

	private void validateCriteria() {
		if (!criteriaAdded) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}
}
