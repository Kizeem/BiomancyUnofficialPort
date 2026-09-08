package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.UnstableCompoundItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class VanillaGameEventHandler {

	private VanillaGameEventHandler() {}

	@SubscribeEvent
	public static void onEvent(final VanillaGameEvent event) {
		if (!event.getVanillaEvent().is(GameEvent.HIT_GROUND)) return;

		Entity cause = event.getCause();
		if (cause instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof UnstableCompoundItem) {
			UnstableCompoundItem.explode(itemEntity, false);
		}
	}

	@SubscribeEvent
	public static void onRegisterBrewingRecipes(final RegisterBrewingRecipesEvent event) {
		ModRecipes.registerBrewingRecipes(event.getBuilder());
	}

}
