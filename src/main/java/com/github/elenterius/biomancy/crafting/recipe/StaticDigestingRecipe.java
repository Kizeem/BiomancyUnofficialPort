package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class StaticDigestingRecipe extends StaticProcessingRecipe implements DigestingRecipe {

	private final Ingredient recipeIngredient;
	private final ItemStack recipeResult;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	public StaticDigestingRecipe(ResourceLocation id, ItemStack result, int craftingTimeTicks, int craftingCostNutrients, Ingredient ingredient) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		recipeIngredient = ingredient;
		recipeResult = result;

		vanillaIngredients = NonNullList.of(Ingredient.EMPTY, recipeIngredient);
		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(ContainerRecipeInput inputInventory, Level level) {
		return recipeIngredient.test(inputInventory.getItem(0));
	}

	@Override
	public ItemStack assemble(ContainerRecipeInput inputInventory, HolderLookup.Provider registryAccess) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
		return recipeResult;
	}

	@Override
	public Ingredient getIngredient() {
		return recipeIngredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DIGESTING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DIGESTING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<StaticDigestingRecipe> {
		private static final ResourceLocation DEFAULT_ID = BiomancyMod.rl("digesting");

		private static final MapCodec<StaticDigestingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.recipeIngredient),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.recipeResult),
				Codec.INT.optionalFieldOf("processingTime", 100).forGetter(recipe -> recipe.craftingTimeTicks),
				Codec.INT.optionalFieldOf("nutrientsCost", 1).forGetter(recipe -> recipe.craftingCostNutrients)
		).apply(instance, (ingredient, result, time, cost) -> new StaticDigestingRecipe(DEFAULT_ID, result, time, cost, ingredient)));

		private static final StreamCodec<RegistryFriendlyByteBuf, StaticDigestingRecipe> STREAM_CODEC = StreamCodec.composite(
				ItemStack.OPTIONAL_STREAM_CODEC, recipe -> recipe.recipeResult,
				ByteBufCodecs.VAR_INT, recipe -> recipe.craftingTimeTicks,
				ByteBufCodecs.VAR_INT, recipe -> recipe.craftingCostNutrients,
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.recipeIngredient,
				(result, time, cost, ingredient) -> new StaticDigestingRecipe(DEFAULT_ID, result, time, cost, ingredient)
		);

		@Override
		public MapCodec<StaticDigestingRecipe> codec() {
			return MAP_CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, StaticDigestingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}