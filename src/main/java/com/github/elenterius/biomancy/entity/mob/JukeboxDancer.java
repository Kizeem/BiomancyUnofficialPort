package com.github.elenterius.biomancy.entity.mob;

import net.minecraft.core.BlockPos;
import javax.annotation.Nullable;

public interface JukeboxDancer {

	boolean isDancing();

	void setDancing(boolean flag);

	@Nullable BlockPos getJukeboxPos();

}
