package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.entity.mob.ai.goal.FrenzyAttackableTargetGoal;
import com.github.elenterius.biomancy.entity.mob.ai.goal.FrenzyMeleeAttackGoal;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

import java.util.List;

public class FrenzySerum extends BasicSerum {

	public static final int DEFAULT_DURATION_TICKS = 20 * (2 * 60 + 30); // 2 min : 30 sec
	public static final int MIN_DURATION_TICKS = 20 * 30; // 30 sec
	public static final double ATTACK_DAMAGE_FALLBACK = 1;

	public FrenzySerum(int colorIn) {
		super(colorIn);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Rabbit rabbit && rabbit.getVariant() != Rabbit.Variant.EVIL) {
			rabbit.setVariant(Rabbit.Variant.EVIL);
			return;
		}

		addStatusEffect(target);
	}

	@Override
	public void affectPlayerSelf(ServerLevel level, CompoundTag tag, ServerPlayer targetSelf) {
		addStatusEffect(targetSelf);
	}

	private void addStatusEffect(LivingEntity target) {
		target.addEffect(new MobEffectInstance(ModMobEffects.FRENZY, DEFAULT_DURATION_TICKS, 0));
	}

	@Override
	public void appendTooltip(CompoundTag tag, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey()).withStyle(TextStyles.LORE));
		tooltip.add(ComponentUtil.EMPTY_LINE);
		addEffectToClientTooltip(tooltip, ModMobEffects.FRENZY, 0, DEFAULT_DURATION_TICKS);
	}

	public void addEffectToClientTooltip(List<Component> tooltip, Holder<MobEffect> effect, int amplifier, int duration) {
		PotionContents.addPotionTooltip(List.of(new MobEffectInstance(effect, duration, amplifier)), tooltip::add, 1f, 1f);
	}

	public static void injectAIBehavior(Mob mob) {
		if (!(mob instanceof PathfinderMob) && !(mob instanceof RangedAttackMob)) return;

		if (!hasFrenzyTargetGoal(mob)) {
			mob.targetSelector.addGoal(1, new FrenzyAttackableTargetGoal<>(mob, LivingEntity.class));
		}

		if (!(mob instanceof RangedAttackMob) && !(mob instanceof Enemy) && mob instanceof PathfinderMob pathfinderMob) {
			if (!hasMeleeAttackGoal(mob)) {
				mob.goalSelector.addGoal(4, new FrenzyMeleeAttackGoal(pathfinderMob, 1d, false));
			}
		}
	}

	private static boolean hasMeleeAttackGoal(Mob mob) {
		for (WrappedGoal availableGoal : mob.goalSelector.getAvailableGoals()) {
			if (availableGoal.getGoal() instanceof MeleeAttackGoal) return true;
		}
		return false;
	}

	private static boolean hasFrenzyTargetGoal(Mob mob) {
		for (WrappedGoal availableGoal : mob.targetSelector.getAvailableGoals()) {
			if (availableGoal.getGoal() instanceof FrenzyAttackableTargetGoal) return true;
		}
		return false;
	}

}
