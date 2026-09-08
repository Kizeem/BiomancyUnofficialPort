package com.github.elenterius.biomancy.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;

public class VariableOutput {

	private final Item item;
	private final @Nullable
	ItemStack template; //carries item components, never null when count > 0 outputs are expected with components
	private final ItemCountRange countRange;

	public static final StreamCodec<RegistryFriendlyByteBuf, VariableOutput> STREAM_CODEC = StreamCodec.of(
			(buffer, output) -> {
				ItemStack.STREAM_CODEC.encode(buffer, output.getItemStack());
				ItemCountRange.toNetwork(buffer, output.countRange);
			},
			buffer -> new VariableOutput(ItemStack.STREAM_CODEC.decode(buffer), ItemCountRange.fromNetwork(buffer))
	);

	public static final Codec<VariableOutput> CODEC = new Codec<>() {
		@Override
		public <T> DataResult<T> encode(VariableOutput input, DynamicOps<T> ops, T prefix) {
			return DataResult.success(JsonOps.INSTANCE.convertTo(ops, writeToJson(input)));
		}

		@Override
		public <T> DataResult<com.mojang.datafixers.util.Pair<VariableOutput, T>> decode(DynamicOps<T> ops, T input) {
			try {
				JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
				return DataResult.success(com.mojang.datafixers.util.Pair.of(VariableOutput.deserialize(element.getAsJsonObject()), input));
			}
			catch (Exception e) {
				return DataResult.error(() -> "Failed to parse VariableOutput: " + e.getMessage());
			}
		}
	};

	private static JsonObject writeToJson(VariableOutput output) {
		JsonObject json = new JsonObject();
		JsonElement itemElement = ItemStack.STRICT_CODEC.encodeStart(JsonOps.INSTANCE, output.getItemStack()).getOrThrow();
		if (itemElement instanceof JsonObject itemObject) {
			itemObject.entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue()));
		}

		JsonObject rangeObject = new JsonObject();
		ItemCountRange.toJson(rangeObject, output.countRange);
		json.add("countRange", rangeObject);
		return json;
	}

	public VariableOutput(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public VariableOutput(ItemStack stack, int count) {
		this(stack.getItem(), stack.copyWithCount(1), new ItemCountRange.ConstantValue(count));
	}

	public VariableOutput(ItemStack stack, int min, int max) {
		this(stack.getItem(), stack.copyWithCount(1), new ItemCountRange.UniformRange(min, max));
	}

	public VariableOutput(ItemStack stack, int n, float p) {
		this(stack.getItem(), stack.copyWithCount(1), new ItemCountRange.BinomialRange(n, p));
	}

	public VariableOutput(ItemLike item) {
		this(item, 1);
	}

	public VariableOutput(ItemLike item, int count) {
		this(item, new ItemCountRange.ConstantValue(count));
	}

	public VariableOutput(ItemLike item, int min, int max) {
		this(item, new ItemCountRange.UniformRange(min, max));
	}

	public VariableOutput(ItemLike item, int n, float p) {
		this(item, new ItemCountRange.BinomialRange(n, p));
	}

	public VariableOutput(ItemStack stack, ItemCountRange countRange) {
		this(stack.getItem(), stack.copyWithCount(1), countRange);
	}

	public VariableOutput(ItemLike item, ItemCountRange countRange) {
		this(item, null, countRange);
	}

	public VariableOutput(ItemLike item, @Nullable ItemStack template, ItemCountRange countRange) {
		this.item = item.asItem();
		this.template = template;
		this.countRange = countRange;
	}

	public Item getItem() {
		return item;
	}

	public ItemStack getItemStack() {
		return template != null ? template.copyWithCount(1) : new ItemStack(item);
	}

	public ItemStack getItemStack(RandomSource rng) {
		int count = getCount(rng);
		if (count < 1) return ItemStack.EMPTY;

		ItemStack stack = getItemStack();
		stack.setCount(count);

		return stack;
	}

	public int getCount(RandomSource rng) {
		return countRange.getCount(rng);
	}

	public ItemCountRange getCountRange() {return countRange;}

	public JsonObject serialize() {
		JsonObject result = new JsonObject();
		JsonElement itemElement = ItemStack.STRICT_CODEC.encodeStart(JsonOps.INSTANCE, getItemStack()).getOrThrow();
		if (itemElement instanceof JsonObject itemObject) {
			result.addProperty("item", GsonHelper.getAsString(itemObject, "id"));
			if (itemObject.has("components")) result.add("components", itemObject.get("components"));
		}
		else {
			result.addProperty("item", Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).toString());
		}

		JsonObject obj = new JsonObject();
		ItemCountRange.toJson(obj, countRange);
		result.add("countRange", obj);

		return result;
	}

	public static VariableOutput deserialize(JsonObject jsonObject) {
		if (!jsonObject.has("id") && jsonObject.has("item")) {
			jsonObject.addProperty("id", jsonObject.get("item").getAsString());
			jsonObject.remove("item");
		}
		ItemStack stack = ItemStack.STRICT_CODEC.parse(JsonOps.INSTANCE, jsonObject).getOrThrow();
		if (!stack.isEmpty() && jsonObject.has("nbt") && jsonObject.get("nbt").isJsonPrimitive()) {
			try {
				CompoundTag legacyTag = TagParser.parseTag(jsonObject.get("nbt").getAsString());
				CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
				for (String key : legacyTag.getAllKeys()) {
					customData.put(key, legacyTag.get(key));
				}
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
			}
			catch (CommandSyntaxException ignored) {}
		}
		ItemCountRange countRange = ItemCountRange.fromJson(GsonHelper.getAsJsonObject(jsonObject, "countRange"));
		if (stack.isEmpty()) throw new JsonParseException("Result can't be Empty");
		return new VariableOutput(stack, countRange);
	}

}
