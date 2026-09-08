package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackDamageEffect extends StatusEffect {

	protected double damageMultiplier;
	protected ResourceLocation attackDamageId;

	public AttackDamageEffect(MobEffectCategory category, int color) {
		this(category, color, true);
	}

	public AttackDamageEffect(MobEffectCategory category, int color, boolean isCurable) {
		super(category, color, isCurable);
	}

	public AttackDamageEffect addAttackDamageModifier(String id, double damageMultiplier, double amount, AttributeModifier.Operation operation) {
		this.damageMultiplier = damageMultiplier;
		this.attackDamageId = ResourceLocation.parse(id);
		addAttributeModifier(Attributes.ATTACK_DAMAGE, this.attackDamageId, amount, operation);
		return this;
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
		return true;
	}

}
