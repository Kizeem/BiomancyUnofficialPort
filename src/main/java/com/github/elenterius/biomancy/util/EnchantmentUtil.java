package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class EnchantmentUtil {

	private EnchantmentUtil() {}

	public static int getLevel(ItemStack stack, ResourceKey<Enchantment> enchantmentKey) {
		ItemEnchantments enchantments = stack.getEnchantments();
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(enchantmentKey)) return enchantments.getLevel(holder);
		}
		return 0;
	}

	public static int getLevel(LivingEntity livingEntity, ResourceKey<Enchantment> enchantmentKey) {
		Holder<Enchantment> holder = ModEnchantments.getHolder(enchantmentKey, livingEntity.level());
		int maxLevel = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			maxLevel = Math.max(maxLevel, livingEntity.getItemBySlot(slot).getEnchantmentLevel(holder));
		}
		return maxLevel;
	}

	public static List<Map.Entry<EquipmentSlot, ItemStack>> getItemsWithEnchantment(ResourceKey<Enchantment> enchantmentKey, LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		Holder<Enchantment> holder = ModEnchantments.getHolder(enchantmentKey, livingEntity.level());
		Map<EquipmentSlot, ItemStack> map = holder.value().getSlotItems(livingEntity);
		List<Map.Entry<EquipmentSlot, ItemStack>> list = new ArrayList<>();

		for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
			ItemStack stack = entry.getValue();
			if (!stack.isEmpty() && stack.getEnchantmentLevel(holder) > 0 && predicate.test(stack)) {
				list.add(entry);
			}
		}

		return list;
	}

}