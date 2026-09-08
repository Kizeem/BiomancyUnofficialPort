package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.serum.*;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public final class ModSerums {

	public static final DeferredRegister<Serum> SERUMS = DeferredRegister.create(BiomancyMod.rl("serum"), BiomancyMod.MOD_ID);

	static {
		SERUMS.makeRegistry(builder -> builder.defaultKey(BiomancyMod.rl("empty")));
	}

	public static final Supplier<Registry<Serum>> REGISTRY = SERUMS.getRegistry();

	public static final DeferredHolder<Serum, ?> EMPTY = SERUMS.register("empty", () -> BasicSerum.EMPTY);

	public static final DeferredHolder<Serum, ?> AGEING_SERUM = SERUMS.register("ageing_serum", () -> new AgeingSerum(0xACBE00));
	public static final DeferredHolder<Serum, ?> ENLARGEMENT_SERUM = SERUMS.register("enlargement_serum", () -> new EnlargementSerum(0xDD5225));
	public static final DeferredHolder<Serum, ?> SHRINKING_SERUM = SERUMS.register("shrinking_serum", () -> new ShrinkingSerum(0x819D5A));
	public static final DeferredHolder<Serum, ?> REJUVENATION_SERUM = SERUMS.register("rejuvenation_serum", () -> new RejuvenationSerum(0x75F36F));
	public static final DeferredHolder<Serum, ?> BREEDING_STIMULANT = SERUMS.register("breeding_stimulant", () -> new BreedingSerum(0x70174E));
	public static final DeferredHolder<Serum, ?> ABSORPTION_BOOST = SERUMS.register("absorption_boost", () -> new AbsorptionSerum(0xF5A21F));
	public static final DeferredHolder<Serum, ?> INSOMNIA_CURE = SERUMS.register("insomnia_cure", () -> new InsomniaCureSerum(0x9B70B2));
	public static final DeferredHolder<Serum, ?> CLEANSING_SERUM = SERUMS.register("cleansing_serum", () -> new CleansingSerum(0x371667));
	public static final DeferredHolder<Serum, ?> FRENZY_SERUM = SERUMS.register("frenzy_serum", () -> new FrenzySerum(0xD1001C));
	public static final DeferredHolder<Serum, ?> POTION_SERUM = SERUMS.register("potion_serum", PotionSerum::new);

	private ModSerums() {}

}
