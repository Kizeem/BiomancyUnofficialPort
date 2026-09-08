package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
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

public class DecomposingRecipe extends StaticProcessingRecipe {
	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 1;
	public static final int MAX_INGREDIENTS = 1;
	public static final int MAX_OUTPUTS = 6;

	private final IngredientStack ingredientStack;
	private final List<VariableOutput> outputs;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	public DecomposingRecipe(ResourceLocation id, List<VariableOutput> outputs, IngredientStack ingredientStack, int craftingTimeTicks, int craftingCostNutrients) {
		super(id, craftingTimeTicks, craftingCostNutrients);
		this.ingredientStack = ingredientStack;
		this.outputs = outputs;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(List.of(ingredientStack));
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);

		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(ContainerRecipeInput inputInventory, Level level) {
		ItemStack stack = inputInventory.getItem(0);
		return ingredientStack.ingredient().test(stack) && stack.getCount() >= ingredientStack.count();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
		return outputs.get(0).getItemStack();
	}

	@Override
	public ItemStack assemble(ContainerRecipeInput inputInventory, HolderLookup.Provider registryAccess) {
		return outputs.get(0).getItemStack().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public IngredientStack getIngredientQuantity() {
		return ingredientStack;
	}

	public List<VariableOutput> getOutputs() {
		return outputs;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DECOMPOSING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DECOMPOSING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DECOMPOSER.get());
	}

	public static class Serializer implements RecipeSerializer<DecomposingRecipe> {
		private static final ResourceLocation DEFAULT_ID = BiomancyMod.rl("decomposing");

		private static final MapCodec<DecomposingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				IngredientStack.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredientStack),
				VariableOutput.CODEC.listOf().fieldOf("results").forGetter(recipe -> recipe.outputs),
				Codec.INT.optionalFieldOf("processingTime", 100).forGetter(recipe -> recipe.craftingTimeTicks),
				Codec.INT.optionalFieldOf("nutrientsCost", (int) DEFAULT_CRAFTING_COST_NUTRIENTS).forGetter(recipe -> recipe.craftingCostNutrients)
		).apply(instance, (ingredientStack, results, time, cost) -> new DecomposingRecipe(DEFAULT_ID, results, ingredientStack, time, cost)));

		private static final StreamCodec<RegistryFriendlyByteBuf, DecomposingRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					IngredientStack.STREAM_CODEC.encode(buffer, recipe.ingredientStack);
					buffer.writeVarInt(recipe.craftingTimeTicks);
					buffer.writeVarInt(recipe.craftingCostNutrients);
					buffer.writeVarInt(recipe.outputs.size());
					for (VariableOutput output : recipe.outputs) {
						VariableOutput.STREAM_CODEC.encode(buffer, output);
					}
				},
				buffer -> {
					IngredientStack ingredientStack = IngredientStack.STREAM_CODEC.decode(buffer);
					int craftingTime = buffer.readVarInt();
					int craftingCost = buffer.readVarInt();

					int outputCount = buffer.readVarInt();
					List<VariableOutput> outputs = new ArrayList<>();
					for (int j = 0; j < outputCount; ++j) {
						outputs.add(VariableOutput.STREAM_CODEC.decode(buffer));
					}

					return new DecomposingRecipe(DEFAULT_ID, outputs, ingredientStack, craftingTime, craftingCost);
				}
		);

		@Override
		public MapCodec<DecomposingRecipe> codec() {
			return MAP_CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DecomposingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

}