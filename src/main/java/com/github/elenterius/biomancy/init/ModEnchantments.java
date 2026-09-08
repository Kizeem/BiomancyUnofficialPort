package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public final class ModEnchantments {

	public static final ResourceKey<Enchantment> DESPOIL = createKey("despoil");
	public static final ResourceKey<Enchantment> ANESTHETIC = createKey("anesthetic");
	public static final ResourceKey<Enchantment> SURGICAL_PRECISION = createKey("surgical_precision");
	public static final ResourceKey<Enchantment> PARASITIC_METABOLISM = createKey("parasitic_metabolism");
	public static final ResourceKey<Enchantment> SELF_FEEDING = createKey("self_feeding");

	public static final List<ResourceKey<Enchantment>> ENCHANTMENT_KEYS = List.of(DESPOIL, ANESTHETIC, SURGICAL_PRECISION, PARASITIC_METABOLISM, SELF_FEEDING);

	private ModEnchantments() {}

	private static ResourceKey<Enchantment> createKey(String path) {
		return ResourceKey.create(Registries.ENCHANTMENT, BiomancyMod.rl(path));
	}

	public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> key, RegistryAccess registryAccess) {
		return registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
	}

	public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> key, LevelReader level) {
		return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
	}

	public static int getMaxLevel(ResourceKey<Enchantment> key, Level level) {
		return getHolder(key, level).value().getMaxLevel();
	}

	public static void bootstrap(BootstrapContext<Enchantment> context) {
		HolderGetter<Item> itemLookup = context.lookup(Registries.ITEM);
		HolderGetter<Enchantment> enchantmentLookup = context.lookup(Registries.ENCHANTMENT);

		context.register(DESPOIL, Enchantment.enchantment(Enchantment.definition(
				itemLookup.getOrThrow(ModItemTags.ENCHANTABLE_DESPOIL), 3, 3,
				Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
				EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND))
				.exclusiveWith(enchantmentLookup.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE))
				.build(BiomancyMod.rl("despoil")));

		context.register(ANESTHETIC, Enchantment.enchantment(Enchantment.definition(
				itemLookup.getOrThrow(ModItemTags.ENCHANTABLE_SYRINGES), 3, 1,
				Enchantment.dynamicCost(11, 10), Enchantment.dynamicCost(21, 15), 4,
				EquipmentSlotGroup.MAINHAND))
				.build(BiomancyMod.rl("anesthetic")));

		context.register(SURGICAL_PRECISION, Enchantment.enchantment(Enchantment.definition(
				itemLookup.getOrThrow(ModItemTags.ENCHANTABLE_SURGERY), 3, 3,
				Enchantment.dynamicCost(11, 10), Enchantment.dynamicCost(21, 15), 4,
				EquipmentSlotGroup.MAINHAND))
				.build(BiomancyMod.rl("surgical_precision")));

		context.register(PARASITIC_METABOLISM, Enchantment.enchantment(Enchantment.definition(
				itemLookup.getOrThrow(ModItemTags.ENCHANTABLE_LIVING_TOOLS), 3, 1,
				Enchantment.dynamicCost(11, 10), Enchantment.dynamicCost(21, 15), 4,
				EquipmentSlotGroup.ANY))
				.build(BiomancyMod.rl("parasitic_metabolism")));

		context.register(SELF_FEEDING, Enchantment.enchantment(Enchantment.definition(
				itemLookup.getOrThrow(ModItemTags.ENCHANTABLE_LIVING_TOOLS), 3, 1,
				Enchantment.dynamicCost(11, 10), Enchantment.dynamicCost(21, 15), 4,
				EquipmentSlotGroup.ANY))
				.build(BiomancyMod.rl("self_feeding")));
	}

}