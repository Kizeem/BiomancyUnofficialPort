package com.github.elenterius.biomancy.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModKeyBindings;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import com.github.elenterius.biomancy.mixin.accessor.MinecraftAccessor;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ClientInputHandler {

	private static final EquipmentSlot[] armorSlotTypes = Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmor).toArray(EquipmentSlot[]::new);

	private ClientInputHandler() {}

	@SubscribeEvent
	public static void onKeyInput(final InputEvent.Key event) {
		if (event.getAction() != GLFW.GLFW_RELEASE) return; //we only want key releases

		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return;

		InputConstants.Key key = InputConstants.getKey(event.getKey(), event.getScanCode());

		if (ModKeyBindings.MAIN_HAND_ITEM_ACTION.isActiveAndMatches(key)) {
			handleEquipmentSlot(EquipmentSlot.MAINHAND, player);
		}

		if (ModKeyBindings.OFF_HAND_ITEM_ACTION.isActiveAndMatches(key)) {
			handleEquipmentSlot(EquipmentSlot.OFFHAND, player);
		}

		if (ModKeyBindings.EQUIPPED_ARMOR_ACTION.isActiveAndMatches(key)) {
			handleEquipmentSlots(armorSlotTypes, player);
		}
	}

	@SubscribeEvent
	public static void onClientTickPost(final ClientTickEvent.Post event) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null || !player.isAlive() || minecraft.screen != null) return;

		ItemStack mainHandItem = player.getMainHandItem();
		if (!(mainHandItem.getItem() instanceof RavenousClawsItem)) return;

		if (!minecraft.options.keyAttack.isDown() || player.isUsingItem()) return;
		if (minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.ENTITY) return;
		if (player.getAttackStrengthScale(0f) < 1f) return;

		((MinecraftAccessor) minecraft).biomancy$startAttack();
	}

	private static void handleEquipmentSlots(EquipmentSlot[] slots, LocalPlayer player) {
		for (EquipmentSlot slot : slots) {
			handleEquipmentSlot(slot, player);
		}
	}

	private static void handleEquipmentSlot(EquipmentSlot slot, LocalPlayer player) {
		ItemStack stack = player.getItemBySlot(slot);
		if (!stack.isEmpty() && stack.getItem() instanceof KeyPressListener keyListener) {
			KeyPressListener.KeyPressResult result = keyListener.onClientKeyPress(stack, player.clientLevel, player, slot, KeyPressListener.KeyPressResult.NO_FLAGS);
			if (result.success()) {
				ModNetworkHandler.sendKeyBindPressToServer(slot, result.flags());
			}
		}
	}

	//	@SubscribeEvent
	//	public static void onMouseClick(final InputEvent.ClickInputEvent event) {
	//		if (event.isAttack()) {
	//			ClientPlayerEntity player = Minecraft.getInstance().player;
	//			if (player != null) {
	//				ItemStack heldStack = player.getHeldItem(event.getHand());
	//				if (!heldStack.isEmpty() && heldStack.getItem() == ModItems.INFESTED_RIFLE.get()) {
	//					event.setSwingHand(false);
	//					event.setCanceled(true);
	//				}
	//			}
	//		}
	//	}

}
