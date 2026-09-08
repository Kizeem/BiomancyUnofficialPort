package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorUpgrades;
import com.github.elenterius.biomancy.item.armor.LivingArmorItem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.registries.DeferredHolder;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public final class SpecialCraftingRecipeMaker {

	private SpecialCraftingRecipeMaker() {}

	public static List<RecipeHolder<CraftingRecipe>> createCradleCleansingRecipes() {
		ItemStack cradle = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();
		CompoundTag tag = new CompoundTag();
		CompoundTag tagProcGen = new CompoundTag();
		MoundShape.ProcGenValues procGenValues = new MoundShape.ProcGenValues(1234L, (byte) 0, (byte) 0, (byte) 1, 250, 66, 0.7f, 0.5f);
		procGenValues.writeTo(tagProcGen);
		tag.put(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY, tagProcGen);
		BlockItem.setBlockEntityData(cradle, ModBlockEntities.PRIMORDIAL_CRADLE.get(), tag);

		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(Ingredient.of(cradle));
		ingredients.add(Ingredient.of(ModItems.CLEANSING_SERUM.get()));
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ResourceLocation recipeId = ModItems.PRIMORDIAL_CRADLE.getId().withSuffix("_cleansing" + ".cleansed");

		ItemStack result = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();

		return List.of(
				new RecipeHolder<>(recipeId, new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs))
		);
	}

	public static List<RecipeHolder<CraftingRecipe>> createHelmetUpgradeRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		ModItems.findEntries(LivingArmorItem.class).forEach(armorItem -> {
			try {
				recipes.add(createHelmetUpgradeRecipe(armorItem));
			}
			catch (Exception e) {
				BiomancyMod.LOGGER.error("Failed to create helmet upgrade recipe for {}", armorItem.getId(), e);
			}
		});
		return recipes;
	}

	private static RecipeHolder<CraftingRecipe> createHelmetUpgradeRecipe(DeferredHolder<Item, ?> armorItem) {
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(ModItems.PRIMORDIAL_CORE.get()), Ingredient.of(armorItem.get()));
		ItemStack result = AcolyteArmorUpgrades.addUpgrade(armorItem.get().getDefaultInstance(), AcolyteArmorUpgrades.PRIMORDIAL_SIGHT);
		ResourceLocation recipeId = BiomancyMod.rl("special_crafting/" + armorItem.getId().getPath() + "_upgrade");
		return new RecipeHolder<>(recipeId, new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs));
	}

	public static List<RecipeHolder<CraftingRecipe>> createPlayerHeadRecipes() {
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
				Ingredient.of(Items.PLAYER_HEAD),
				Ingredient.of(ModItems.EXOTIC_DUST.get()),
				Ingredient.of(createUniquePlayerEssence(UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6"), "jeb_"))
		);
		ItemStack result = createPlayerHead("jeb_");

		return List.of(
				new RecipeHolder<>(BiomancyMod.rl("special_crafting/player_head"), new ShapelessRecipe("", CraftingBookCategory.MISC, result, inputs))
		);
	}

	private static ItemStack createPlayerHead(String name) {
		ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
		stack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap()));

		return stack;
	}

	private static ItemStack createUniquePlayerEssence(UUID entityUUID, String playerName) {
		EssenceItem essenceItem = (EssenceItem) ModItems.ESSENCE.get();
		ItemStack stack = essenceItem.getDefaultInstance();

		EntityType<?> entityType = EntityType.PLAYER;
		int[] colors = EssenceItem.getEssenceColors(entityUUID);

		essenceItem.setEssenceData(stack, 3, entityType, entityUUID, colors, null);

		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putString(EssenceItem.PLAYER_NAME_KEY, playerName);
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

		return stack;
	}

	public static List<RecipeHolder<CraftingRecipe>> createBiometricMembraneRecipes() {
		UUID pigUUID = UUID.fromString("420faf42-bf42-4b20-af42-c42420e42d42");

		UUID playerUUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
		String playerName = "jeb_";

		Stream<List<RecipeHolder<CraftingRecipe>>> recipePairs = Stream.of(
				createRecipePair(EntityType.PIG, false, null),
				createRecipePair(EntityType.PIG, true, null),
				createRecipePair(EntityType.PIG, false, pigUUID),
				createRecipePair(EntityType.PIG, true, pigUUID),
				createRecipePair(EntityType.PLAYER, false, null),
				createRecipePair(EntityType.PLAYER, true, null),
				createUniquePlayerRecipePair(false, playerUUID, playerName),
				createUniquePlayerRecipePair(true, playerUUID, playerName)
		);

		return recipePairs.flatMap(List::stream).toList();
	}

	private static List<RecipeHolder<CraftingRecipe>> createUniquePlayerRecipePair(boolean isInverted, UUID playerUUID, String playerName) {
		Ingredient ingredient = Ingredient.of(createUniquePlayerEssence(playerUUID, playerName));
		return createRecipePair(EntityType.PLAYER, isInverted, playerUUID, 3, ingredient);
	}

	private static List<RecipeHolder<CraftingRecipe>> createRecipePair(EntityType<?> entityType, boolean isInverted, @Nullable UUID entityUUID) {
		int tier = entityUUID != null ? 3 : 1;
		Ingredient ingredient = Ingredient.of(EssenceItem.fromEntityType(entityType, tier));
		return createRecipePair(entityType, isInverted, entityUUID, tier, ingredient);
	}

	private static List<RecipeHolder<CraftingRecipe>> createRecipePair(EntityType<?> entityType, boolean isInverted, @Nullable UUID entityUUID, int tier, Ingredient essence) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(essence);
		ingredients.add(Ingredient.of(ModItems.BIOMETRIC_MEMBRANE.get()));

		if (isInverted) {
			ingredients.add(Ingredient.of(Items.REDSTONE_TORCH));
		}

		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ItemStack membraneStack = BiometricMembraneBlock.createItem(entityType, entityUUID, EssenceItem.getEssenceColors(entityType, entityUUID, tier), isInverted);

		String name = ModItems.BIOMETRIC_MEMBRANE.getId().toLanguageKey();
		String inverted = isInverted ? "inverted." : "";
		String unique = entityUUID != null ? "unique." : "";

		ResourceLocation createRecipeId = BiomancyMod.rl(name + "." + inverted + unique + entityType.getDescriptionId());
		ResourceLocation resetRecipeId = BiomancyMod.rl(name + ".reset." + inverted + unique + entityType.getDescriptionId());

		return List.of(
				new RecipeHolder<>(createRecipeId, new ShapelessRecipe(name, CraftingBookCategory.MISC, membraneStack, inputs)),
				new RecipeHolder<>(resetRecipeId, new ShapelessRecipe(name, CraftingBookCategory.MISC, new ItemStack(ModItems.BIOMETRIC_MEMBRANE.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(membraneStack))))
		);
	}
}