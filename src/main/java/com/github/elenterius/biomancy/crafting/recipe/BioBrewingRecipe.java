package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BioBrewingRecipe extends StaticProcessingRecipe {

	public static final short DEFAULT_CRAFTING_TIME_TICKS = 4 * 20;
	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 2;
	public static final int MAX_INGREDIENTS = 4;
	public static final int MAX_REACTANT = 1;

	private final List<IngredientStack> ingredients;
	private final Ingredient recipeReactant;
	private final int reactantCount;
	private final ItemStack result;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	public BioBrewingRecipe(ResourceLocation id, ItemStack result, int craftingTimeTicks, int craftingCostNutrients, List<IngredientStack> ingredients, Ingredient reactant) {
		this(id, result, craftingTimeTicks, craftingCostNutrients, ingredients, reactant, 1);
	}

	public BioBrewingRecipe(ResourceLocation id, ItemStack result, int craftingTimeTicks, int craftingCostNutrients, List<IngredientStack> ingredients, Ingredient reactant, int reactantCount) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		this.ingredients = ingredients;
		recipeReactant = reactant;
		this.reactantCount = Math.max(1, reactantCount);
		this.result = result;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		flatIngredients.add(recipeReactant);

		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);

		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(ContainerRecipeInput inv, Level level) {
		int lastIndex = inv.size() - 1;
		ItemStack reactantStack = inv.getItem(lastIndex);
		if (reactantStack.getCount() < reactantCount || !recipeReactant.test(reactantStack)) return false;

		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < lastIndex; idx++) {
			ItemStack stack = inv.getItem(idx);
			if (stack.isEmpty()) continue;

			for (int i = 0; i < ingredients.size(); i++) {
				IngredientStack requiredIngredient = ingredients.get(i);
				if (requiredIngredient.testItem(stack) && countedIngredients[i] < requiredIngredient.count()) {
					countedIngredients[i] += stack.getCount();
					break;
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(ContainerRecipeInput inv, HolderLookup.Provider registryAccess) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= ingredients.size();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientStack> getIngredientQuantities() {
		return ingredients;
	}

	public Ingredient getReactant() {
		return recipeReactant;
	}

	public int getReactantCount() {
		return reactantCount;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_BREWING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_BREWING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.BIO_LAB.get());
	}

	public static class Serializer implements RecipeSerializer<BioBrewingRecipe> {
		private static final ResourceLocation DEFAULT_ID = BiomancyMod.rl("bio_brewing");

		private static final MapCodec<BioBrewingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				IngredientStack.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
				Ingredient.CODEC.optionalFieldOf("reactant", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeReactant),
				Codec.INT.optionalFieldOf("reactantCount", 1).forGetter(recipe -> recipe.reactantCount),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
				Codec.INT.optionalFieldOf("processingTime", 100).forGetter(recipe -> recipe.craftingTimeTicks),
				Codec.INT.optionalFieldOf("nutrientsCost", (int) DEFAULT_CRAFTING_COST_NUTRIENTS).forGetter(recipe -> recipe.craftingCostNutrients)
		).apply(instance, (ingredients, reactant, reactantCount, result, time, cost) -> new BioBrewingRecipe(DEFAULT_ID, result, time, cost, ingredients, reactant, reactantCount)));

		private static final StreamCodec<RegistryFriendlyByteBuf, BioBrewingRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.result);
					Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.recipeReactant);
					buffer.writeVarInt(recipe.reactantCount);
					buffer.writeVarInt(recipe.craftingTimeTicks);
					buffer.writeVarInt(recipe.craftingCostNutrients);

					buffer.writeVarInt(recipe.ingredients.size());
					for (IngredientStack ingredientStack : recipe.ingredients) {
						IngredientStack.STREAM_CODEC.encode(buffer, ingredientStack);
					}
				},
				buffer -> {
					ItemStack resultStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
					Ingredient reactant = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
					int reactantCount = buffer.readVarInt();
					int craftingTime = buffer.readVarInt();
					int craftingCost = buffer.readVarInt();

					int ingredientCount = buffer.readVarInt();
					List<IngredientStack> ingredients = new ArrayList<>();
					for (int i = 0; i < ingredientCount; i++) {
						ingredients.add(IngredientStack.STREAM_CODEC.decode(buffer));
					}

					return new BioBrewingRecipe(DEFAULT_ID, resultStack, craftingTime, craftingCost, ingredients, reactant, reactantCount);
				}
		);

		@Override
		public MapCodec<BioBrewingRecipe> codec() {
			return MAP_CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BioBrewingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}