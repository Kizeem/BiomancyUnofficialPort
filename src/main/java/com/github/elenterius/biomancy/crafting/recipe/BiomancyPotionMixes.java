package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class BiomancyPotionMixes {

	public record MixInfo(Holder<Potion> from, Ingredient ingredient, Holder<Potion> to) {}

	private BiomancyPotionMixes() {}

	public static List<MixInfo> getVanillaPotionMixes() {
		class RecordingBuilder extends PotionBrewing.Builder {
			final List<MixInfo> mixes = new ArrayList<>();

			RecordingBuilder(FeatureFlagSet features) {
				super(features);
			}

			@Override
			public void addMix(Holder<Potion> from, Item ingredient, Holder<Potion> to) {
				mixes.add(new MixInfo(from, Ingredient.of(ingredient), to));
				super.addMix(from, ingredient, to);
			}
		}

		RecordingBuilder builder = new RecordingBuilder(FeatureFlags.REGISTRY.allFlags());
		PotionBrewing.addVanillaMixes(builder);
		return List.copyOf(builder.mixes);
	}

}
