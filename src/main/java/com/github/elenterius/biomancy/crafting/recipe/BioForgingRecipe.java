package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.ContainerRecipeInput;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BioForgingRecipe implements Recipe<ContainerRecipeInput> {

	public static final byte DEFAULT_CRAFTING_COST_NUTRIENTS = 1;
	public static final int MAX_INGREDIENTS = 5;
	private final ResourceLocation registryKey;
	private final BioForgeTab tab;
	private final List<IngredientStack> ingredients;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	private final int cost;

	public BioForgingRecipe(ResourceLocation id, BioForgeTab tab, ItemStack result, List<IngredientStack> ingredients, int craftingCostNutrients) {
		registryKey = id;
		this.tab = tab;
		this.result = result;
		this.ingredients = ingredients;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);

		cost = craftingCostNutrients;
	}

	public static boolean areRecipesEqual(BioForgingRecipe recipeA, BioForgingRecipe recipeB) {
		return recipeA.isRecipeEqual(recipeB);
	}

	public ResourceLocation getId() {
		return registryKey;
	}

	public int getCraftingCostNutrients() {
		return cost;
	}

	public boolean isRecipeEqual(BioForgingRecipe other) {
		return registryKey.equals(other.getId());
	}

	public boolean isCraftable(StackedContents itemCounter) {
		for (IngredientStack ingredientStack : ingredients) {
			if (!ingredientStack.hasSufficientCount(itemCounter)) {
				return false;
			}
		}

		return true;
	}

	public boolean isCraftable(ItemStackCounter itemCounter) {
		int[] residuals = new int[ingredients.size()];
		int totalResidual = 0;
		for (int i = 0; i < ingredients.size(); i++) {
			int count = ingredients.get(i).count();
			residuals[i] = count;
			totalResidual += count;
		}

		for (ItemStackCounter.CountedItem countedItem : itemCounter.getItemCounts()) {
			if (totalResidual <= 0) return true;

			int available = countedItem.amount();

			for (int i = 0; i < ingredients.size(); i++) {
				if (available <= 0) break;

				final int residual = residuals[i];
				if (residual > 0 && ingredients.get(i).testItem(countedItem.stack())) {
					final int amount = Math.min(residual, available);
					residuals[i] -= amount;
					available -= amount;
					totalResidual -= amount;
				}
			}
		}

		return totalResidual <= 0;
	}

	@Override
	public boolean matches(ContainerRecipeInput inv, Level level) {
		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < inv.size(); idx++) {
			ItemStack stack = inv.getItem(idx);
			if (!stack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					if (ingredients.get(i).testItem(stack)) {
						countedIngredients[i] += stack.getCount();
						break;
					}
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(ContainerRecipeInput container, HolderLookup.Provider registryAccess) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 0;
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

	public BioForgeTab getTab() {
		return tab;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.BIO_FORGE.get());
	}

	public static class Serializer implements RecipeSerializer<BioForgingRecipe> {
		private static final ResourceLocation DEFAULT_ID = BiomancyMod.rl("bio_forging");

		private static final Codec<BioForgeTab> TAB_CODEC = ResourceLocation.CODEC.flatXmap(
				id -> {
					BioForgeTab tab = ModBioForgeTabs.REGISTRY.get().get(id);
					return tab != null ? DataResult.success(tab) : DataResult.error(() -> "Unknown Bio-Forge tab '%s'".formatted(id));
				},
				tab -> DataResult.success(ModBioForgeTabs.REGISTRY.get().getKey(tab))
		);

		private static final MapCodec<BioForgingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				IngredientStack.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
				RecipeUtil.ITEM_STACK_WITH_LEGACY_NBT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
				Codec.INT.optionalFieldOf("nutrientsCost", (int) DEFAULT_CRAFTING_COST_NUTRIENTS).forGetter(recipe -> recipe.cost),
				TAB_CODEC.fieldOf(BioForgeTab.JSON_KEY).forGetter(recipe -> recipe.tab)
		).apply(instance, (ingredients, result, cost, tab) -> new BioForgingRecipe(DEFAULT_ID, tab, result, ingredients, cost)));

		private static final StreamCodec<RegistryFriendlyByteBuf, BioForgingRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.result);

					buffer.writeVarInt(recipe.ingredients.size());
					for (IngredientStack ingredientStack : recipe.ingredients) {
						IngredientStack.STREAM_CODEC.encode(buffer, ingredientStack);
					}

					buffer.writeVarInt(recipe.cost);

					buffer.writeResourceLocation(ModBioForgeTabs.REGISTRY.get().getKey(recipe.tab));
				},
				buffer -> {
					ItemStack resultStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

					int ingredientCount = buffer.readVarInt();
					List<IngredientStack> ingredients = new ArrayList<>();
					for (int i = 0; i < ingredientCount; i++) {
						ingredients.add(IngredientStack.STREAM_CODEC.decode(buffer));
					}

					int craftingCost = buffer.readVarInt();

					BioForgeTab tab = BioForgeTab.fromNetwork(buffer);

					return new BioForgingRecipe(DEFAULT_ID, tab, resultStack, ingredients, craftingCost);
				}
		);

		@Override
		public MapCodec<BioForgingRecipe> codec() {
			return MAP_CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BioForgingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

}