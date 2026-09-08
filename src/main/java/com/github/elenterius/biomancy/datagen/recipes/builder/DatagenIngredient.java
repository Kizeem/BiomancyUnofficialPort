package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public final class DatagenIngredient {

	final ResourceLocation resourceLocation;

	public DatagenIngredient(String itemKey) {
		resourceLocation = Objects.requireNonNull(ResourceLocation.tryParse(itemKey));
	}

	public DatagenIngredient(String namespace, String path) {
		resourceLocation = Objects.requireNonNull(ResourceLocation.tryBuild(namespace, path));
	}

	public DatagenIngredient(ResourceLocation itemKey) {
		this.resourceLocation = itemKey;
	}

	public ResourceLocation resourceLocation() {
		return resourceLocation;
	}

	public Ingredient toIngredient() {
		JsonObject json = new JsonObject();
		json.addProperty("item", resourceLocation.toString());
		return Ingredient.CODEC.parse(JsonOps.INSTANCE, (JsonElement) json).getOrThrow();
	}

	public JsonElement toJson() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("item", resourceLocation.toString());
		return jsonObj;
	}

}
