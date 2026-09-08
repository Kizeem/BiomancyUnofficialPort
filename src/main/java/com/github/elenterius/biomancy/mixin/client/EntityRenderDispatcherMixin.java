package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.entity.misc.HitboxDebugInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void render(Entity entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBuffer, int packedLight, CallbackInfo ci) {
		EntityRenderDispatcher dispatcher = (EntityRenderDispatcher) (Object) this;
		if (dispatcher.shouldRenderHitBoxes() && !entity.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
			if (entity instanceof HitboxDebugInfo customHitboxInfo) {
				customHitboxInfo.renderHitboxInfo(dispatcher, poseStack, multiBuffer, packedLight, partialTicks);
			}
		}
	}

}