package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class UnbreakingEnchantmentMixin {

	@Inject(method = "getAvailableEnchantmentResults", at = @At(value = "RETURN"))
	private static void onGetAvailableEnchantmentResults(int enchantmentLevel, ItemStack stack, Stream<Holder<Enchantment>> enchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
		if (stack.getItem() instanceof LivingTool) {
			cir.getReturnValue().removeIf(enchantmentInstance -> enchantmentInstance.enchantment.is(Enchantments.UNBREAKING));
		}
	}

}