package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.AnyFoodIngredient;
import com.github.elenterius.biomancy.crafting.EssenceIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModIngredientTypes {

	public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, BiomancyMod.MOD_ID);

	public static final DeferredHolder<IngredientType<?>, ?> ANY_FOOD = INGREDIENT_TYPES.register("any_food", () -> new IngredientType<>(AnyFoodIngredient.MAP_CODEC, AnyFoodIngredient.STREAM_CODEC));
	public static final DeferredHolder<IngredientType<?>, ?> ESSENCE = INGREDIENT_TYPES.register("essence", () -> new IngredientType<>(EssenceIngredient.MAP_CODEC, EssenceIngredient.STREAM_CODEC));

	private ModIngredientTypes() {}

}