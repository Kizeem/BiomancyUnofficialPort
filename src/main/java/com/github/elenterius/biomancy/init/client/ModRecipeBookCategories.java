package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRecipeBookCategories {

	private ModRecipeBookCategories() {}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeTab category) {
		return TAB_TO_CATEGORY.get(category.enumId());
	}

	@SubscribeEvent
	public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
		registerCategories(event);

		event.registerRecipeCategoryFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
	}

	// the enum constants BIOMANCY_* are added to RecipeBookCategories at runtime via META-INF/enumextensions.json
	// NOTE: this class must ONLY hold EnumProxy fields!
	// The runtime enum extender accesses these fields from RecipeBookCategories.<clinit>; any other static state
	// (especially EnumProxy.getValue() calls) here would re-enter class initialization before the enum is loaded.
	public static final EnumProxy<RecipeBookCategories> SEARCH_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.SEARCH.get().getIcon()));
	public static final EnumProxy<RecipeBookCategories> BUILDING_BLOCKS_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.BUILDING_BLOCKS.get().getIcon()));
	public static final EnumProxy<RecipeBookCategories> MACHINES_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.MACHINES.get().getIcon()));
	public static final EnumProxy<RecipeBookCategories> TOOLS_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.TOOLS.get().getIcon()));
	public static final EnumProxy<RecipeBookCategories> COMPONENTS_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.COMPONENTS.get().getIcon()));
	public static final EnumProxy<RecipeBookCategories> MISC_CATEGORY_PROXY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(ModBioForgeTabs.MISC.get().getIcon()));

	private static final Map<String, RecipeBookCategories> TAB_TO_CATEGORY = new HashMap<>();

	private static RecipeBookCategories categoryFor(RecipeBookCategories searchCategory, RecipeBookCategories blocksCategory, RecipeBookCategories machinesCategory, RecipeBookCategories toolsCategory, RecipeBookCategories componentsCategory, RecipeBookCategories miscCategory, BioForgeTab tab) {
		String path = ModBioForgeTabs.REGISTRY.get().getKey(tab).getPath();
		return switch (path) {
			case "search" -> searchCategory;
			case "blocks" -> blocksCategory;
			case "machines" -> machinesCategory;
			case "tools" -> toolsCategory;
			case "components" -> componentsCategory;
			case "misc" -> miscCategory;
			default -> miscCategory;
		};
	}

	private static void registerCategories(RegisterRecipeBookCategoriesEvent event) {
		RecipeBookCategories searchCategory = SEARCH_CATEGORY_PROXY.getValue();
		RecipeBookCategories buildingBlocksCategory = BUILDING_BLOCKS_CATEGORY_PROXY.getValue();
		RecipeBookCategories machinesCategory = MACHINES_CATEGORY_PROXY.getValue();
		RecipeBookCategories toolsCategory = TOOLS_CATEGORY_PROXY.getValue();
		RecipeBookCategories componentsCategory = COMPONENTS_CATEGORY_PROXY.getValue();
		RecipeBookCategories miscCategory = MISC_CATEGORY_PROXY.getValue();

		for (Map.Entry<ResourceKey<BioForgeTab>, BioForgeTab> entry : ModBioForgeTabs.REGISTRY.get().entrySet()) {
			BioForgeTab tab = entry.getValue();
			String name = tab.enumId();
			RecipeBookCategories category = categoryFor(searchCategory, buildingBlocksCategory, machinesCategory, toolsCategory, componentsCategory, miscCategory, tab);
			TAB_TO_CATEGORY.put(name, category);
		}

		List<RecipeBookCategories> categories = TAB_TO_CATEGORY.values().stream().toList();

		event.registerBookCategories(ModRecipeBookTypes.bioForge(), categories);
		event.registerAggregateCategory(searchCategory, categories);
		event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), recipeHolder -> {
			if (recipeHolder.value() instanceof BioForgingRecipe bioForgingRecipe) {
				return TAB_TO_CATEGORY.get(bioForgingRecipe.getTab().enumId());
			}
			return null;
		});
	}

}
