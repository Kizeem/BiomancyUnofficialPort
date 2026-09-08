package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.data.models.model.TextureSlot;
import javax.annotation.Nonnull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;



@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {

	@Invoker("create")
	static @Nonnull TextureSlot biomancy$create(String id) {
		//noinspection DataFlowIssue
		return null;
	}

}
