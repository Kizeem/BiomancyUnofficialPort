package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.mob.FleshPig;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FleshPigModel<T extends FleshPig> extends DefaultedEntityGeoModel<T> {

	public FleshPigModel() {
		super(BiomancyMod.rl("mob/flesh_pig"), true);
	}

	@Override
	public void applyMolangQueries(AnimationState<T> state, double animTime) {
		super.applyMolangQueries(state, animTime);

		T animatable = state.getAnimatable();
		float partialTick = state.getPartialTick();

		MathParser.setVariable("variable.limb_swing", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwing = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwing = animatable.walkAnimation.position(partialTick);
				if (animatable.isBaby()) limbSwing *= 3f;
			}

			return limbSwing;
		});

		MathParser.setVariable("variable.limb_swing_amount", () -> {
			boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());

			float limbSwingAmount = 0;

			if (!shouldSit && animatable.isAlive()) {
				limbSwingAmount = animatable.walkAnimation.speed(partialTick);
				if (limbSwingAmount > 1f) limbSwingAmount = 1f;
			}

			return limbSwingAmount;
		});
	}

}
