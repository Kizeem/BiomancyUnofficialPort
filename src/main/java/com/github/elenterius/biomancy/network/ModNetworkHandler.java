package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.menu.BioForgeMenu;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.util.ItemStackFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetworkHandler {

	private ModNetworkHandler() {}

	@SubscribeEvent
	public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");

		registrar.playToServer(
				BioForgeRecipePayload.TYPE,
				BioForgeRecipePayload.STREAM_CODEC,
				(payload, context) -> context.enqueueWork(() -> {
					if (context.player() instanceof ServerPlayer sender && !sender.isSpectator()
							&& sender.containerMenu instanceof BioForgeMenu menu && menu.containerId == payload.containerId()) {
						RecipeHolder<?> holder = sender.level().getRecipeManager().byKey(payload.recipeId()).orElse(null);
						if (holder != null && holder.value() instanceof BioForgingRecipe) {
							//noinspection unchecked
							menu.setSelectedRecipe((RecipeHolder<BioForgingRecipe>) holder, sender);
						}
					}
				})
		);

		registrar.playToServer(
				KeyPressPayload.TYPE,
				KeyPressPayload.STREAM_CODEC,
				(payload, context) -> context.enqueueWork(() -> {
					if (context.player() instanceof ServerPlayer player) {
						ServerLevel level = player.serverLevel();
						KeyPressListener.onReceiveKeybindingPacket(level, player, payload.slotIndex(), payload.flag());
					}
				})
		);

		registrar.playToClient(
				BioLabFilterPayload.TYPE,
				BioLabFilterPayload.STREAM_CODEC,
				(payload, context) -> context.enqueueWork(() -> {
					if (FMLEnvironment.dist.isClient()) {
						handleBioLabFilterClient(payload);
					}
				})
		);

		registrar.playToClient(
				CustomExplosionPayload.TYPE,
				CustomExplosionPayload.STREAM_CODEC,
				(payload, context) -> context.enqueueWork(() -> {
					if (FMLEnvironment.dist.isClient()) {
						CustomExplosionPayload.handleClient(payload);
					}
				})
		);
	}

	private static void handleBioLabFilterClient(BioLabFilterPayload payload) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && minecraft.player.containerMenu instanceof BioLabMenu menu && menu.containerId == payload.containerId()) {
			menu.setFilters(payload.filters());
		}
	}

	public static void sendKeyBindPressToServer(net.minecraft.world.InteractionHand hand, byte flag) {
		net.minecraft.world.entity.EquipmentSlot slot = hand == net.minecraft.world.InteractionHand.MAIN_HAND
				? net.minecraft.world.entity.EquipmentSlot.MAINHAND
				: net.minecraft.world.entity.EquipmentSlot.OFFHAND;
		sendKeyBindPressToServer(slot, flag);
	}

	public static void sendKeyBindPressToServer(net.minecraft.world.entity.EquipmentSlot slot, byte flag) {
		sendKeyBindPressToServer(slot.getFilterFlag(), flag);
	}

	public static void sendKeyBindPressToServer(int slotIndex, byte flag) {
		net.neoforged.neoforge.network.PacketDistributor.sendToServer(new KeyPressPayload(slotIndex, flag));
	}

	public static void sendBioForgeRecipeToServer(int containerId, RecipeHolder<BioForgingRecipe> holder) {
		if (holder == null) return;
		net.neoforged.neoforge.network.PacketDistributor.sendToServer(new BioForgeRecipePayload(containerId, holder.id()));
	}

	public static void sendBioLabFilterToClient(ServerPlayer player, int containerId, com.github.elenterius.biomancy.util.ItemStackFilterList filters) {
		List<ItemStack> stacks = filters.stream().map(ItemStackFilter::getItemStack).map(s -> s == null ? ItemStack.EMPTY : s).toList();
		net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new BioLabFilterPayload(containerId, stacks));
	}

	public static void sendCustomExplosionToClients(ServerLevel level, com.github.elenterius.biomancy.util.ExplosionUtil.ExplosionType explosionType, net.minecraft.world.level.Explosion explosion) {
		if (!explosion.interactsWithBlocks()) {
			explosion.clearToBlow();
		}

		double radius = Math.min(((com.github.elenterius.biomancy.mixin.accessor.ExplosionAccessor) explosion).getRadius() + 64d, 96d);
		double radiusSqr = radius * radius;

		for (ServerPlayer player : level.players()) {
			if (player.distanceToSqr(explosion.center()) < radiusSqr) {
				net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, CustomExplosionPayload.of(explosionType, explosion, player));
			}
		}
	}

}
