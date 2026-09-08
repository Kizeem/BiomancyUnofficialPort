package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModAttributes {
	
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, BiomancyMod.MOD_ID);

	private ModAttributes() {}

}
