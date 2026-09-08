package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.BioForgeScreen;
import com.github.elenterius.biomancy.client.gui.BioLabScreen;
import com.github.elenterius.biomancy.client.gui.DecomposerScreen;
import com.github.elenterius.biomancy.client.gui.DigesterScreen;
import com.github.elenterius.biomancy.crafting.recipe.BioBrewingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.DecomposingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.PotionSerumRecipes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.menu.DecomposerMenu;
import com.github.elenterius.biomancy.menu.DigesterMenu;
import com.github.elenterius.biomancy.menu.ISlotZone;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@JeiPlugin
public class BiomancyJeiPlugin implements IModPlugin {

	private static final ResourceLocation ID = BiomancyMod.rl("plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ModItems.POTION_SERUM.get(), PotionSerumSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(
				new DecomposingCategory(registration.getJeiHelpers().getGuiHelper()),
				new BioBrewingCategory(registration.getJeiHelpers().getGuiHelper()),
				new DigestingCategory(registration.getJeiHelpers().getGuiHelper()),
				new BioForgingCategory(registration.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
		RecipeManager recipeManager = level.getRecipeManager();

		registration.addRecipes(DecomposingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(mojangRecipeType(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), DecomposingRecipe.class)));
		registration.addRecipes(BioBrewingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(mojangRecipeType(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), BioBrewingRecipe.class)));
		registration.addRecipes(BioBrewingCategory.RECIPE_TYPE, wrapInRecipeHolders(PotionSerumRecipes.RECIPES, BioBrewingRecipe::getId));

		registration.addRecipes(DigestingCategory.RECIPE_TYPE, DigestingRecipes.getRecipes(level));
		registration.addRecipes(BioForgingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(mojangRecipeType(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), BioForgingRecipe.class)));

		registration.addRecipes(RecipeTypes.CRAFTING, SpecialCraftingRecipeMaker.createBiometricMembraneRecipes());
		registration.addRecipes(RecipeTypes.CRAFTING, SpecialCraftingRecipeMaker.createCradleCleansingRecipes());
		registration.addRecipes(RecipeTypes.CRAFTING, SpecialCraftingRecipeMaker.createPlayerHeadRecipes());
		registration.addRecipes(RecipeTypes.CRAFTING, SpecialCraftingRecipeMaker.createHelmetUpgradeRecipes());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModItems.BIO_FORGE.get()), BioForgingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.BIO_LAB.get()), BioBrewingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.DIGESTER.get()), DigestingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.DECOMPOSER.get()), DecomposingCategory.RECIPE_TYPE);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(DecomposerScreen.class, 176 - 5 - 10, 4, 10, 10, DecomposingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(BioLabScreen.class, 176 - 5 - 10, 4, 10, 10, BioBrewingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(DigesterScreen.class, 176 - 5 - 10, 4, 10, 10, DigestingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(BioForgeScreen.class, 292 - 5 - 10, 4, 10, 10, BioForgingCategory.RECIPE_TYPE);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registerInputSlots(registration, ModMenuTypes.DECOMPOSER.get(), DecomposerMenu.class, DecomposingCategory.RECIPE_TYPE, DecomposerMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, ModMenuTypes.BIO_LAB.get(), BioLabMenu.class, BioBrewingCategory.RECIPE_TYPE, BioLabMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, ModMenuTypes.DIGESTER.get(), DigesterMenu.class, DigestingCategory.RECIPE_TYPE, DigesterMenu.SlotZone.INPUT_ZONE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends net.minecraft.world.item.crafting.Recipe<?>> net.minecraft.world.item.crafting.RecipeType<T> mojangRecipeType(net.minecraft.world.item.crafting.RecipeType<?> recipeType, Class<T> recipeClass) {
		return (net.minecraft.world.item.crafting.RecipeType<T>) recipeType;
	}

	private static <R extends net.minecraft.world.item.crafting.Recipe<?>> List<RecipeHolder<R>> wrapInRecipeHolders(List<R> recipes, Function<R, ResourceLocation> idProvider) {
		return recipes.stream()
				.map(recipe -> new RecipeHolder<>(idProvider.apply(recipe), recipe))
				.toList();
	}

	private <C extends AbstractContainerMenu, R> void registerInputSlots(IRecipeTransferRegistration registration, @Nullable MenuType<C> menuType, Class<? extends C> containerClass, RecipeType<R> recipeType, ISlotZone slotZone) {
		registration.addRecipeTransferHandler(containerClass, menuType, recipeType, slotZone.getFirstIndex(), slotZone.getSlotCount(), 0, 36);
	}
}