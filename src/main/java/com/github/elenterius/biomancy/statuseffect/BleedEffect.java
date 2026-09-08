package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.mixin.accessor.MobEffectInstanceAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends StatusEffect implements StackingStatusEffect {

	private int maxEffectStack;

	public BleedEffect(MobEffectCategory category, int color, int maxEffectStack) {
		super(category, color, false);
		this.maxEffectStack = maxEffectStack;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return (duration + 1) % 40 == 0;
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (livingEntity.hasEffect(MobEffects.REGENERATION) || livingEntity.hasEffect(MobEffects.HEAL)) {
			MobEffectInstance effectInstance = getActiveBleedEffectInstance(livingEntity);
			if (effectInstance != null) livingEntity.removeEffect(effectInstance.getEffect());
			return true;
		}

		if (livingEntity.isSprinting()) {
			increaseEffectDurationBy(livingEntity, 200);
		}
		else if (livingEntity.isCrouching()) {
			reduceEffectDurationBy(livingEntity, 200);
		}

		int effectLevel = amplifier + 1;
		livingEntity.hurt(ModDamageSources.bleed(livingEntity.level(), null), effectLevel);

		if (livingEntity.level() instanceof ServerLevel serverLevel) {
			float xz = livingEntity.getBbWidth() * 0.25f;
			float y = livingEntity.getBbHeight() * 0.25f;
			serverLevel.sendParticles(ModParticleTypes.FALLING_BLOOD.get(), livingEntity.getX(), livingEntity.getY(0.5f), livingEntity.getZ(), 4, xz, y, xz, 0);
		}
		return true;
	}

	private MobEffectInstance getActiveBleedEffectInstance(LivingEntity livingEntity) {
		for (MobEffectInstance instance : livingEntity.getActiveEffectsMap().values()) {
			if (instance.getEffect().value() == this) return instance;
		}
		return null;
	}

	private void reduceEffectDurationBy(LivingEntity livingEntity, int ticks) {
		MobEffectInstance effectInstance = getActiveBleedEffectInstance(livingEntity);
		if (effectInstance == null) return;

		int reducedDuration = effectInstance.getDuration() - ticks;
		if (reducedDuration > 0) {
			((MobEffectInstanceAccessor) effectInstance).biomancy$setDuration(reducedDuration);
		}
		else {
			livingEntity.removeEffect(effectInstance.getEffect());
		}
	}

	private void increaseEffectDurationBy(LivingEntity livingEntity, int ticks) {
		MobEffectInstance effectInstance = getActiveBleedEffectInstance(livingEntity);
		if (effectInstance == null) return;

		int increasedDuration = effectInstance.getDuration() + ticks;
		((MobEffectInstanceAccessor) effectInstance).biomancy$setDuration(increasedDuration);
	}

	@Override
	public int getMaxEffectStack() {
		return maxEffectStack;
	}

}
