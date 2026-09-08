package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

import java.util.stream.Stream;

public final class ModTiers {

	public static final SimpleTier PRIMAL_FLESH = new SimpleTier(BlockTags.NEEDS_IRON_TOOL, 500, 4.5f, 1f, 14, () -> Ingredient.of(ModItems.LIVING_FLESH.get()));
	public static final SimpleTier BONE = new SimpleTier(BlockTags.NEEDS_STONE_TOOL, 142, 4.5f, 1f, 7, ModTiers::buildBoneIngredients);
	public static final SimpleTier BIOFLESH = new SimpleTier(Tags.Blocks.NEEDS_NETHERITE_TOOL, 2031, 9f, 4f, 15, () -> Ingredient.of(ModItemTags.FRESH_RAW_MEATS));

	private ModTiers() {}

	private static Ingredient.Value tagIngredient(TagKey<Item> tagKey) {
		return new Ingredient.TagValue(tagKey);
	}

	private static Ingredient.Value itemIngredient(Item item, int amount) {
		return new Ingredient.ItemValue(new ItemStack(item, amount));
	}

	private static Ingredient buildBoneIngredients() {
		return Ingredient.fromValues(Stream.of(
				tagIngredient(Tags.Items.BONES),
				itemIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
		));
	}

}
