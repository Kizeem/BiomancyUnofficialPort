package com.github.elenterius.biomancy.init;

import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public final class ModRecipeBookTypes {

	// the enum constant BIOMANCY_BIO_FORGE is added to RecipeBookType at runtime via META-INF/enumextensions.json
	public static final EnumProxy<RecipeBookType> BIO_FORGE_PROXY = new EnumProxy<>(RecipeBookType.class);

	private ModRecipeBookTypes() {}

	public static RecipeBookType bioForge() {
		return BIO_FORGE_PROXY.getValue();
	}

	static void init() {
		//force init static fields
	}

}
