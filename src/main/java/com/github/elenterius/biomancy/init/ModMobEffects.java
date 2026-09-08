package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.statuseffect.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModMobEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, BiomancyMod.MOD_ID);

	public static final DeferredHolder<MobEffect, ?> CORROSIVE = EFFECTS.register("corrosive", () -> new CorrosiveEffect(MobEffectCategory.HARMFUL, 0x39FF14));
	public static final DeferredHolder<MobEffect, ?> ARMOR_SHRED = EFFECTS.register("armor_shred", () -> new ArmorShredEffect(MobEffectCategory.HARMFUL, 20, 0x909090)
			.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("biomancy", "a15ed03e-c5db-4cf8-a0f5-4eb4657bb731"), -1f, AttributeModifier.Operation.ADD_VALUE));
	public static final DeferredHolder<MobEffect, ?> BLEED = EFFECTS.register("bleed", () -> new BleedEffect(MobEffectCategory.HARMFUL, 0x8a0303, 2));
	public static final DeferredHolder<MobEffect, ?> TOXIN = EFFECTS.register("toxin", () -> new ToxinEffect(MobEffectCategory.HARMFUL, 0x87a363));
	public static final DeferredHolder<MobEffect, ?> VOLATILE = EFFECTS.register("volatile", () -> new VolatileEffect(MobEffectCategory.HARMFUL, 0xff681f));

	public static final DeferredHolder<MobEffect, ?> ESSENCE_ANEMIA = EFFECTS.register("essence_anemia", () -> new EssenceAnemiaEffect(MobEffectCategory.HARMFUL, 0xfefefe)
			.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("biomancy", "a6ca3300-17d9-41c7-b29d-af93fa367b23"), -0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
	);
	public static final DeferredHolder<MobEffect, ?> DESPOIL = EFFECTS.register("despoil", () -> new DespoilEffect(MobEffectCategory.BENEFICIAL, 0xdd77ff));
	public static final DeferredHolder<MobEffect, ?> LIBIDO = EFFECTS.register("libido", () -> new LibidoEffect(MobEffectCategory.NEUTRAL, 0xe06a78));

	public static final DeferredHolder<MobEffect, ?> FRENZY = EFFECTS.register("frenzy", () -> new FrenzyEffect(MobEffectCategory.BENEFICIAL, 0xd1001c)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("biomancy", "1f1fb00f-d6bc-4b42-8533-422054cea63d"), 6f, AttributeModifier.Operation.ADD_VALUE) // Strength ~II
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("biomancy", "14e2a39c-abb5-43a4-9449-522eec57ff2e"), 0.225f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("biomancy", "08a20d5b-60ce-4769-9e67-71cab0abe989"), 0.175f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	public static final DeferredHolder<MobEffect, ?> WITHDRAWAL = EFFECTS.register("withdrawal", () -> new WithdrawalEffect(0x5c4b88)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("biomancy", "8dadcbe5-9098-4545-b07c-3e9120c84232"), -3, AttributeModifier.Operation.ADD_VALUE)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("biomancy", "0f1be88c-cbb2-455c-8559-0b420caa980d"), -0.1125f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("biomancy", "ab116bd1-196b-4bf8-a136-6c24e7c0e80d"), -0.0625f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	public static final DeferredHolder<MobEffect, ?> PRIMORDIAL_INFESTATION = EFFECTS.register("primordial_infestation", () -> new StatusEffect(MobEffectCategory.HARMFUL, 0xbe3ee1, false));

	private ModMobEffects() {}

}
