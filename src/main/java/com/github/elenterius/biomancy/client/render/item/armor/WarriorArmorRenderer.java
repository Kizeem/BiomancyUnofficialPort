package com.github.elenterius.biomancy.client.render.item.armor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class WarriorArmorRenderer extends GeoArmorRenderer<WarriorArmorItem> {

	public WarriorArmorRenderer() {
		super(new DefaultedItemGeoModel<>(BiomancyMod.rl("armor/warrior_armor")));
	}

	@Override
	public RenderType getRenderType(WarriorArmorItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityCutoutNoCull(texture);
	}

}