package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleEvents;
import com.github.elenterius.biomancy.crafting.EssenceIngredient;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.github.elenterius.biomancy.item.EssenceItem;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class BiomancyKubeJSPlugin implements KubeJSPlugin {

	public static final Logger LOGGER = LogManager.getLogger("Biomancy KubeJS Plugin");

	@Override
	public void init() {
		NeoForge.EVENT_BUS.addListener(BiomancyKJSEvents::canCradleSpawnMob);
		NeoForge.EVENT_BUS.addListener(BiomancyKJSEvents::onCradleSpawnMob);
	}

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
		registry.of((ResourceKey<Registry<Serum>>) (ResourceKey<?>) ModSerums.SERUMS.getRegistryKey(), callback -> callback.add("basic", SerumBuilder.class, SerumBuilder::new));
		registry.of(Registries.ITEM, callback -> callback.add("biomancy:basic_serum", SerumItemBuilder.class, SerumItemBuilder::new));
		registry.of((ResourceKey<Registry<BioForgeTab>>) (ResourceKey<?>) ModBioForgeTabs.BIO_FORGE_TABS.getRegistryKey(), callback -> callback.add("basic", BioForgeTabBuilder.class, BioForgeTabBuilder::new));
	}

	@Override
	public void registerEvents(EventGroupRegistry event) {
		event.register(BiomancyKJSEvents.GROUP);
	}

	@Override
	public void registerClasses(ClassFilter filter) {
		filter.allow("com.github.elenterius.biomancy");

		filter.deny("com.github.elenterius.biomancy.integration");
		filter.deny("com.github.elenterius.biomancy.mixin");
		filter.deny("com.github.elenterius.biomancy.network");
		filter.deny("com.github.elenterius.geckolibextras");
	}

	@Override
	public void registerBindings(BindingRegistry event) {
		event.add("Biomancy$EssenceIngredient", EssenceIngredientUtil.class);
		event.add("Biomancy$EssenceItem", EssenceItemUtil.class);
		event.add("Biomancy$Nutrients", Nutrients.class);
		event.add("Biomancy$Tributes", Tributes.class);
		event.add("Biomancy$SimpleTribute", SimpleTribute.class);
		event.add("Biomancy$FleshBlob", FleshBlob.class);
		event.add("Biomancy$CradleEvent$CanSpawnMob", PrimordialCradleEvents.CanSpawnMob.class);
		event.add("Biomancy$CradleEvent$OnSpawnMob", PrimordialCradleEvents.OnSpawnMob.class);
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry event) {
		LOGGER.info("Registering Recipe Schemas...");

		event.register(ModRecipes.DIGESTING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.DIGESTING_SCHEMA);
		event.register(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.BIO_BREWING_SCHEMA);
		event.register(ModRecipes.BIO_FORGING_RECIPE_TYPE.getId(), BioForgingRecipeSchema.SCHEMA);
		event.register(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposingRecipeSchema.SCHEMA);
	}

	interface RecipeKeys {
		RecipeKey<Ingredient> INGREDIENT = IngredientComponent.INGREDIENT.instance().inputKey(RecipeUtil.JsonKeys.INGREDIENT);
		RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.instance().asList().inputKey(RecipeUtil.JsonKeys.INGREDIENTS);

		RecipeKey<IngredientStack> INGREDIENT_STACK = IngredientStackComponent.INGREDIENT_STACK.instance().inputKey(RecipeUtil.JsonKeys.INGREDIENT);
		RecipeKey<List<IngredientStack>> INGREDIENT_STACKS = IngredientStackComponent.INGREDIENT_STACK.instance().asList().inputKey(RecipeUtil.JsonKeys.INGREDIENTS);

		RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.instance().outputKey(RecipeUtil.JsonKeys.RESULT);
		RecipeKey<List<VariableOutput>> VARIABLE_OUTPUTS = VariableOutputComponent.VARIABLE_OUTPUT.instance().asList().outputKey(RecipeUtil.JsonKeys.RESULTS);

		RecipeKey<Integer> PROCESSING_TIME = NumberComponent.INT.otherKey(RecipeUtil.JsonKeys.PROCESSING_TIME).defaultOptional();
		RecipeKey<Integer> NUTRIENTS_COST = NumberComponent.INT.otherKey(RecipeUtil.JsonKeys.NUTRIENTS_COST).defaultOptional();
	}

	interface SimpleRecipeSchemas {
		RecipeSchema DIGESTING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);

		RecipeKey<Ingredient> REACTANT = IngredientComponent.INGREDIENT.instance().inputKey(RecipeUtil.JsonKeys.REACTANT);
		RecipeSchema BIO_BREWING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT_STACKS, REACTANT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);
	}

	interface BioForgingRecipeSchema {
		RecipeKey<List<SizedIngredient>> INGREDIENTS_WITH_COUNT = SizedIngredientComponent.SIZED_INGREDIENT.instance().asList().inputKey(RecipeUtil.JsonKeys.INGREDIENTS);
		RecipeKey<String> BIO_FORGE_TAB = StringComponent.ID.otherKey(RecipeUtil.JsonKeys.BIO_FORGE_TAB);

		RecipeSchema SCHEMA = new RecipeSchema(INGREDIENTS_WITH_COUNT, RecipeKeys.RESULT, BIO_FORGE_TAB, RecipeKeys.NUTRIENTS_COST);
	}

	interface DecomposingRecipeSchema {
		RecipeSchema SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT_STACK, RecipeKeys.VARIABLE_OUTPUTS, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);
	}

	interface EssenceIngredientUtil {

		@Info(
				value = "Creates a essence ingredient that matches a specific tier",
				params = {
						@Param(name = "entityType"),
						@Param(name = "tier", value = "The tier that this essence ingredient requires. Valid tiers are: 0, 1, 2 or 3")
				}
		)
		static EssenceIngredient fromTier(EntityType<?> entityType, int tier) {
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier {}", entityType.getDescriptionId(), tier);
			return EssenceIngredient.of(entityType, tier);
		}

		@Info(
				value = "Creates a essence ingredient that matches any tier",
				params = {@Param(name = "entityType")}
		)
		static EssenceIngredient from(EntityType<?> entityType) {
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier -1", entityType.getDescriptionId());
			return EssenceIngredient.of(entityType);
		}

	}

	interface EssenceItemUtil {

		@Info(
				value = "Creates a tier 1 essence from the EntityType of a LivingEntity",
				params = {@Param(name = "entityType")}
		)
		static ItemStack from(EntityType<?> entityType) {
			return EssenceItem.fromEntityType(entityType, 1);
		}

		@Info(
				value = "Creates a tier x essence from the EntityType of a LivingEntity",
				params = {
						@Param(name = "entityType"),
						@Param(name = "tier", value = "Quality tier of the essence. Valid tiers are: 1, 2 or 3")
				}
		)
		static ItemStack fromTier(EntityType<?> entityType, int tier) {
			return EssenceItem.fromEntityType(entityType, tier);
		}

		@Info(
				value = "Creates a unique essence from the EntityType of a LivingEntity",
				params = {
						@Param(name = "entityType"),
						@Param(name = "uuid", value = "UUID of the LivingEntity")
				}
		)
		static ItemStack fromUUID(EntityType<?> entityType, UUID uuid) {
			return EssenceItem.fromEntityType(entityType, uuid);
		}

		@Info(
				value = "Creates a tier 1 or 2 essence from a LivingEntity",
				params = {@Param(name = "livingEntity")}
		)
		static ItemStack fromLiving(LivingEntity livingEntity) {
			return EssenceItem.fromEntity(livingEntity, 0, 0);
		}

		@Info(
				value = "Creates a tier x essence from a LivingEntity. The tier depends on the enchantment level of surgical precision.",
				params = {
						@Param(name = "livingEntity"),
						@Param(name = "surgicalPrecisionLevel", value = "Level of surgical precision enchantment"),
						@Param(name = "lootingLevel", value = "Level of looting enchantment")
				}
		)
		static ItemStack fromLivingWith(LivingEntity livingEntity, int surgicalPrecisionLevel, int lootingLevel) {
			return EssenceItem.fromEntity(livingEntity, surgicalPrecisionLevel, lootingLevel);
		}

	}

}
