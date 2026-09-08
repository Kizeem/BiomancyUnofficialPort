package com.github.elenterius.biomancy.entity.mob.fleshblob;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

class FleshBlobAttackGoal extends MeleeAttackGoal {

	public FleshBlobAttackGoal(FleshBlob mob, double speed) {
		super(mob, speed, true);
	}

	private double getSizePct() {
		return (double) (((FleshBlob) mob).getBlobSize() - FleshBlob.MIN_SIZE) / (double) (FleshBlob.MAX_SIZE - FleshBlob.MIN_SIZE);
	}

}
