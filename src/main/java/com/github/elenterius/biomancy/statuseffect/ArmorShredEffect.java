package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ArmorShredEffect extends StatusEffect implements StackingStatusEffect {

	private final int maxStackSize;

	public ArmorShredEffect(MobEffectCategory category, int maxStackSize, int color) {
		super(category, color, false);
		this.maxStackSize = maxStackSize;
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
		return true;
	}

	@Override
	public int getMaxEffectStack() {
		return maxStackSize;
	}

}
