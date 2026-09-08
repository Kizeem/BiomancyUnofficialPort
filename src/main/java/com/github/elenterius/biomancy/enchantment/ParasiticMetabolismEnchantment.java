package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.EnchantmentUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public final class ParasiticMetabolismEnchantment {

	private ParasiticMetabolismEnchantment() {}

	public static void repairLivingItems(Player player) {
		if (player.getHealth() <= 10f) return;

		FoodData foodData = player.getFoodData();
		if (foodData.getFoodLevel() <= 2) return;

		List<Map.Entry<EquipmentSlot, ItemStack>> enchantedItems = EnchantmentUtil.getItemsWithEnchantment(ModEnchantments.PARASITIC_METABOLISM, player, LivingTool.NEED_NUTRIENTS_PREDICATE);

		if (!enchantedItems.isEmpty()) {
			Map.Entry<EquipmentSlot, ItemStack> slotItem = enchantedItems.get(player.getRandom().nextInt(enchantedItems.size()));
			ItemStack stack = slotItem.getValue();
			NutrientsContainerItem item = (NutrientsContainerItem) stack.getItem();

			int bonusRepairValue = 2;
			item.increaseNutrients(stack, Nutrients.getRepairValue(ModItems.NUTRIENT_PASTE.get().getDefaultInstance()) + bonusRepairValue);

			if (!player.getAbilities().invulnerable) {
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}
		}
	}

}