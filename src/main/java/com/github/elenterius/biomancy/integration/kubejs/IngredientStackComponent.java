package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStackComponent(RecipeComponentType<?> type, Codec<IngredientStack> codec) implements RecipeComponent<IngredientStack> {

	public static final RecipeComponentType<IngredientStack> INGREDIENT_STACK = RecipeComponentType.unit(KubeJS.id("ingredient_stack"), type -> new IngredientStackComponent(type, IngredientStack.CODEC));

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(IngredientStack.class);
	}

	@Override
	public IngredientStack wrap(RecipeScriptContext cx, Object from) {
		if (from instanceof IngredientStack stack) return stack;
		if (from instanceof ItemStack stack) return new IngredientStack(Ingredient.of(stack.getItem()), stack.getCount());
		Ingredient ingredient = IngredientComponent.INGREDIENT.instance().wrap(cx, from);
		return new IngredientStack(ingredient, 1);
	}

}
