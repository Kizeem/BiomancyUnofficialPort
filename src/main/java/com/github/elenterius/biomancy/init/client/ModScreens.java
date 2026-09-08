package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModScreens {

	private ModScreens() {}

	@SubscribeEvent
	static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.DECOMPOSER.get(), DecomposerScreen::new);
		event.register(ModMenuTypes.BIO_LAB.get(), BioLabScreen::new);
		event.register(ModMenuTypes.STORAGE_SAC.get(), StorageSacScreen::new);
		event.register(ModMenuTypes.FLESHKIN_CHEST.get(), FleshkinChestScreen::new);
		event.register(ModMenuTypes.DIGESTER.get(), DigesterScreen::new);
		event.register(ModMenuTypes.BIO_FORGE.get(), BioForgeScreen::new);
	}

}
