package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.ItemStack;

public record VariableOutputComponent(RecipeComponentType<?> type, Codec<VariableOutput> codec) implements RecipeComponent<VariableOutput> {

	public static final RecipeComponentType<VariableOutput> VARIABLE_OUTPUT = RecipeComponentType.unit(KubeJS.id("variable_output"), type -> new VariableOutputComponent(type, VariableOutput.CODEC));

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(VariableOutput.class);
	}

	@Override
	public VariableOutput wrap(RecipeScriptContext cx, Object from) {
		if (from instanceof VariableOutput output) return output;
		if (from instanceof ItemStack stack) return new VariableOutput(stack);
		ItemStack stack = ItemStackComponent.ITEM_STACK.instance().wrap(cx, from);
		return new VariableOutput(stack);
	}

}
