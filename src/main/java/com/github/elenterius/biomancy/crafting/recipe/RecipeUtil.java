package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.ArrayList;
import java.util.List;

public final class RecipeUtil {

	public static final class JsonKeys {
		public static final String INGREDIENT = "ingredient";
		public static final String INGREDIENTS = "ingredients";
		public static final String REACTANT = "reactant";
		public static final String RESULT = "result";
		public static final String RESULTS = "results";
		public static final String PROCESSING_TIME = "processingTime";
		public static final String NUTRIENTS_COST = "nutrientsCost";
		public static final String BIO_FORGE_TAB = BioForgeTab.JSON_KEY;

		// misc recipe stuff
		public static final String GROUP = "group";
		public static final String CONDITIONS = "conditions";

		// item related keys
		public static final String COUNT = "count";
		public static final String TAG = "tag";
		public static final String ID = "id";

		private JsonKeys() {}
	}

	public static final class TagKeys {
		public static final String FORGE_CAPS = "ForgeCaps";

		private TagKeys() {}
	}

	private RecipeUtil() {}

	public static JsonObject writeItemStack(ItemStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty(JsonKeys.ID, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
		if (stack.getCount() > 1) json.addProperty(JsonKeys.COUNT, stack.getCount());
		return json;
	}

	public static ItemStack readItemStack(JsonObject json) {
		String itemName = GsonHelper.getAsString(json, JsonKeys.ID);
		Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName));
		return new ItemStack(item, GsonHelper.getAsInt(json, JsonKeys.COUNT, 1));
	}

	public static final Codec<ItemStack> ITEM_STACK_WITH_LEGACY_NBT_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<T> encode(ItemStack input, DynamicOps<T> ops, T prefix) {
			return ItemStack.STRICT_CODEC.encode(input, ops, prefix);
		}

		@Override
		public <T> DataResult<com.mojang.datafixers.util.Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
			try {
				JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
				CompoundTag legacyTag = null;
				if (element.isJsonObject() && element.getAsJsonObject().has("nbt")) {
					JsonObject obj = element.getAsJsonObject().deepCopy();
					legacyTag = TagParser.parseTag(obj.get("nbt").getAsString());
					obj.remove("nbt");
					element = obj;
				}

				DataResult<com.mojang.datafixers.util.Pair<ItemStack, T>> result = ItemStack.STRICT_CODEC.decode(JsonOps.INSTANCE, element).map(pair -> com.mojang.datafixers.util.Pair.of(pair.getFirst(), input));
				final CompoundTag legacyNbt = legacyTag;
				if (legacyNbt == null || legacyNbt.isEmpty()) return result;

				return result.flatMap(pair -> {
					ItemStack stack = pair.getFirst().copy();
					CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
					for (String key : legacyNbt.getAllKeys()) {
						customData.put(key, legacyNbt.get(key));
					}
					stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
					return DataResult.success(com.mojang.datafixers.util.Pair.of(stack, pair.getSecond()));
				});
			}
			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Failed to parse legacy nbt: " + e.getMessage());
			}
			catch (Exception e) {
				return DataResult.error(() -> "Failed to parse item stack: " + e.getMessage());
			}
		}
	};

	public static void writeItem(FriendlyByteBuf buffer, @Nullable Item item) {
		if (item == null || item == Items.AIR) {
			buffer.writeBoolean(false);
		}
		else {
			buffer.writeBoolean(true);
			buffer.writeVarInt(BuiltInRegistries.ITEM.getId(item));
		}
	}

	public static @Nullable Item readItem(FriendlyByteBuf buffer) {
		return !buffer.readBoolean() ? null : BuiltInRegistries.ITEM.byId(buffer.readVarInt());
	}

	public static Ingredient readIngredient(JsonObject json, String memberName) {
		JsonElement element = GsonHelper.isArrayNode(json, memberName) ? GsonHelper.getAsJsonArray(json, memberName) : GsonHelper.getAsJsonObject(json, memberName);
		return Ingredient.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow();
	}

	public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (int i = 0; i < jsonArray.size(); i++) {
			Ingredient ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, jsonArray.get(i)).getOrThrow();
			if (!ingredient.isEmpty()) {
				list.add(ingredient);
			}
		}
		return list;
	}

	public static List<VariableOutput> readVariableProductionOutputs(JsonArray jsonArray) {
		List<VariableOutput> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(VariableOutput.deserialize(jsonArray.get(i).getAsJsonObject()));
		}
		return list;
	}

	public static List<IngredientStack> readIngredientStacks(JsonArray jsonArray) {
		List<IngredientStack> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			IngredientStack ingredientStack = IngredientStack.fromJson(jsonArray.get(i).getAsJsonObject());
			if (!ingredientStack.ingredient().isEmpty()) {
				list.add(ingredientStack);
			}
		}
		return list;
	}

	public static List<Ingredient> flattenIngredientStacks(List<IngredientStack> ingredients) {
		List<Ingredient> flatIngredients = new ArrayList<>();
		for (IngredientStack ingredientStack : ingredients) {
			Ingredient ingredient = ingredientStack.ingredient();
			for (int i = 0; i < ingredientStack.count(); i++) {
				flatIngredients.add(ingredient); //insert the same ingredient instances
			}
		}
		return flatIngredients;
	}

}
