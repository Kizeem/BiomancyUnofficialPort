package com.github.elenterius.biomancy.client.render;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import javax.annotation.Nullable;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class PartyTimeShaderHandler {

	private static int ticks = 0;
	@Nullable
	private static Uniform time;

	private PartyTimeShaderHandler() {}

	/// Called when shaders are (re)registered so a stale Uniform from a previous ShaderInstance isn't reused.
	public static void resetTimeUniform() {
		time = null;
	}

	@Nullable
	private static Uniform getTimeUniform() {
		ShaderInstance shader = ModRenderTypes.getEntityCutoutPartyTimeShaderOrNull();
		if (shader == null) {
			return null;
		}
		if (time == null) {
			time = shader.getUniform("Time");
		}
		return time;
	}

	@SubscribeEvent
	static void onClientTick(final ClientTickEvent.Pre event) {
		if (true) { // ClientTickEvent.Pre is always the pre-phase
			ticks++;
		}
	}

	@SubscribeEvent
	static void onRenderFramePre(final RenderFrameEvent.Pre event) {
		Uniform timeUniform = getTimeUniform();
		if (timeUniform == null) {
			return;
		}

		float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		float totalTicks = ticks + partialTick;
		float t = totalTicks * 0.05f; //convert to seconds, ticks/20.0 ~= 1 sec
		timeUniform.set(t);
	}

}
