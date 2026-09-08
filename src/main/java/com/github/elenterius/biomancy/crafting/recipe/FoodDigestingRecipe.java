package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.AnyFoodIngredient;
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
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FoodDigestingRecipe extends DynamicProcessingRecipe implements DigestingRecipe {

	private final int multiplier;
	private final ItemStack resultBaseItem;
	private final AnyFoodIngredient ingredient;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	protected FoodDigestingRecipe(ResourceLocation key, int multiplier, ItemStack resultBaseItem) {
		super(key, ModRecipes.DIGESTING_RECIPE_TYPE.get());
		this.multiplier = multiplier;
		this.resultBaseItem = resultBaseItem;

		ingredient = new AnyFoodIngredient();

		vanillaIngredients = NonNullList.of(Ingredient.EMPTY, ingredient.toVanilla());
		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	public static int getFoodNutrition(ItemStack stack) {
		if (stack.isEmpty()) return 0;
		if (!stack.has(net.minecraft.core.component.DataComponents.FOOD)) return 0;

		FoodProperties foodProperties = stack.getFoodProperties(null);
		return foodProperties != null ? foodProperties.nutrition() : 0;
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(ContainerRecipeInput inputInventory, Level pLevel) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0;
	}

	@Override
	public ItemStack assemble(ContainerRecipeInput inputInventory, HolderLookup.Provider registryAccess) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		if (nutrition <= 0) return ItemStack.EMPTY;

		int count = Mth.clamp(nutrition * multiplier, 1, 64 * 2);
		return resultBaseItem.copyWithCount(count);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public Ingredient getIngredient() {
		return ingredient.toVanilla();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	@Override
	public int getCraftingTimeTicks(RecipeInput inputInventory) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0 ? Mth.ceil(200 + 190 * Math.log(nutrition)) : 0;
	}

	@Override
	public int getCraftingCostNutrients(RecipeInput inputInventory) {
		float sixtySecondsInTicks = 1200;
		return 1 + Mth.floor(getCraftingTimeTicks(inputInventory) / sixtySecondsInTicks);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.FOOD_DIGESTING_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<FoodDigestingRecipe> {
		private static final ResourceLocation DEFAULT_ID = BiomancyMod.rl("food_digesting");

		private static final MapCodec<FoodDigestingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.INT.optionalFieldOf("multiplier", 1).forGetter(recipe -> recipe.multiplier),
				ItemStack.STRICT_CODEC.fieldOf("result_base").forGetter(recipe -> recipe.resultBaseItem)
		).apply(instance, (multiplier, resultBaseItem) -> new FoodDigestingRecipe(DEFAULT_ID, multiplier, resultBaseItem)));

		private static final StreamCodec<RegistryFriendlyByteBuf, FoodDigestingRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, recipe -> recipe.multiplier,
				ItemStack.OPTIONAL_STREAM_CODEC, recipe -> recipe.resultBaseItem,
				(multiplier, resultBaseItem) -> new FoodDigestingRecipe(DEFAULT_ID, multiplier, resultBaseItem)
		);

		@Override
		public MapCodec<FoodDigestingRecipe> codec() {
			return MAP_CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FoodDigestingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}