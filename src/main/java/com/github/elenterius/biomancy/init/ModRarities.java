package com.github.elenterius.biomancy.init;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public final class ModRarities {

	//referenced by META-INF/enumextensions.json, isolated so this class is not loaded during enum extension
	public static final class Proxies {
		public static final EnumProxy<Rarity> COMMON = new EnumProxy<>(Rarity.class, -1, "biomancy:common", styleModifier(0xA58369));
		public static final EnumProxy<Rarity> UNCOMMON = new EnumProxy<>(Rarity.class, -1, "biomancy:uncommon", styleModifier(0xB19748));
		public static final EnumProxy<Rarity> RARE = new EnumProxy<>(Rarity.class, -1, "biomancy:rare", styleModifier(0x2E9E3E));
		public static final EnumProxy<Rarity> VERY_RARE = new EnumProxy<>(Rarity.class, -1, "biomancy:very_rare", styleModifier(0xA870E1));
		public static final EnumProxy<Rarity> ULTRA_RARE = new EnumProxy<>(Rarity.class, -1, "biomancy:ultra_rare", styleModifier(0xFF3D51));

		private Proxies() {}

		private static UnaryOperator<Style> styleModifier(int rgbColor) {
			return style -> style.withColor(TextColor.fromRgb(rgbColor));
		}
	}

	public static final Rarity COMMON = Proxies.COMMON.getValue();
	public static final Rarity UNCOMMON = Proxies.UNCOMMON.getValue();
	public static final Rarity RARE = Proxies.RARE.getValue();
	public static final Rarity VERY_RARE = Proxies.VERY_RARE.getValue();
	public static final Rarity ULTRA_RARE = Proxies.ULTRA_RARE.getValue();

	private ModRarities() {}

	public static int getRGBColor(ItemStack stack) {
		TextColor color = stack.getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
		return color != null ? color.getValue() : 0xFF_FF_FF;
	}

	public static int getARGBColor(ItemStack stack) {
		return getRGBColor(stack) | 0xFF_00_00_00;
	}

}
