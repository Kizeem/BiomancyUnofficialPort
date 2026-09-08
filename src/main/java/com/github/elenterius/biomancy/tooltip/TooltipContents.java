package com.github.elenterius.biomancy.tooltip;

import com.github.elenterius.biomancy.client.gui.tooltip.TooltipHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

/**
 * Component Contents that act as a temporary stand in for a TooltipComponent.
 * This placeholder will be replaced by the contained TooltipComponent during the GatherComponents Event
 * by {@link TooltipHandler#onGatherTooltipComponents}
 * <br>
 * This allows easy insertion of TooltipComponents inside of {@link Item#appendHoverText}
 */
public record TooltipContents(TooltipComponent component) implements ComponentContents {

	public static final ComponentContents.Type<TooltipContents> TYPE = new ComponentContents.Type<>(MapCodec.unit(new TooltipContents(new TooltipComponent() {
		@Override
		public int hashCode() {
			return 0;
		}
	})), "biomancy_tooltip");

	@Override
	public ComponentContents.Type<? extends ComponentContents> type() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "tooltip{" + component + "}";
	}
}
