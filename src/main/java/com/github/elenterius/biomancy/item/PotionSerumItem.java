package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.serum.PotionSerum;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public class PotionSerumItem extends Item implements SerumContainer, ItemTooltipStyleProvider {

	public PotionSerumItem(Properties properties) {
		super(properties);
	}

	public ItemStack getInstanceFrom(Potion potion) {
		return setSerumData(getDefaultInstance(), potion, List.of());
	}

	public ItemStack getInstanceFrom(Potion potion, Collection<MobEffectInstance> statusEffects) {
		return setSerumData(getDefaultInstance(), potion, statusEffects);
	}

	public ItemStack getInstanceFrom(Collection<MobEffectInstance> statusEffects, int color) {
		return setSerumData(getDefaultInstance(), PotionSerum.EMPTY, statusEffects, color);
	}

	public ItemStack getInstanceFrom(Potion potion, Collection<MobEffectInstance> statusEffects, int color) {
		return setSerumData(getDefaultInstance(), potion, statusEffects, color);
	}

	@Override
	public PotionSerum getSerum(ItemStack stack) {
		return (PotionSerum) ModSerums.POTION_SERUM.get();
	}

	public int getTintColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return getSerumColor(stack);
		return 0xFF_FFFFFF;
	}

	//	@Override
	//	public Component getName(ItemStack stack) {
	//		MutableComponent serumName = getSerum(stack).getDisplayName(getSerumData(stack));
	//		return ComponentUtil.mutable().append(super.getName(stack)).append(" of ").append(serumName);
	//	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		MutableComponent serumName = getSerum(stack).getDisplayName(getSerumData(stack));
		return ComponentUtil.mutable().append(displayName).append(" (").append(serumName).append(")");
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		//tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		getSerum(stack).appendTooltip(getSerumData(stack), context.level(), tooltip, flag);
	}

	@Override
	public String getTooltipKey(ItemStack stack) {
		return getSerum(stack).getDescriptionTranslationKey();
	}

	public static ItemStack setSerumData(ItemStack stack, Potion potion, Collection<MobEffectInstance> effects) {
		CompoundTag tag = Serum.getDataTag(stack);
		PotionSerum.setData(tag, potion, effects);
		Serum.setDataTag(stack, tag);
		return stack;
	}

	public static ItemStack setSerumData(ItemStack stack, Potion potion, Collection<MobEffectInstance> effects, int color) {
		CompoundTag tag = Serum.getDataTag(stack);
		PotionSerum.setData(tag, potion, effects, color);
		Serum.setDataTag(stack, tag);
		return stack;
	}

}