package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.loot.CatMorningGiftLootModifier;
import com.github.elenterius.biomancy.loot.DespoilLootModifier;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the mod's global loot modifier files.
 *
 * <p>Since NeoForge 1.21.1, the old Forge {@code ICondition} was split into the data pack
 * condition system ({@link net.neoforged.neoforge.common.conditions.ICondition}) and the
 * vanilla loot condition system ({@link LootItemCondition}). The stock
 * {@link GlobalLootModifierProvider#add(String, IGlobalLootModifier, ICondition...)}
 * only accepts the former, so it cannot generate the legacy file layout this mod ships:
 * a {@code conditions} array whose entries are serialized with the <em>vanilla</em>
 * loot condition codecs (i.e. a {@code "condition"} discriminator key). Both modifiers
 * registered here ({@code biomancy:despoil}, {@code biomancy:cat_morning_gift}) require
 * vanilla loot conditions, so this provider serializes the exact {@link LootItemCondition}
 * arrays the modifiers are constructed with and writes the modifier files and the
 * {@code neoforge/loot_modifiers/global_loot_modifiers.json} index in the layout the
 * mod's generated resources use.</p>
 */
public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final PackOutput packOutput;
	private final Map<String, ModifierEntry> modifiers = new LinkedHashMap<>();

	public ModGlobalLootModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider, BiomancyMod.MOD_ID);
		this.packOutput = packOutput;
	}

	@Override
	protected void start() {
		addLootModifier(ModLoot.DESPOIL_SERIALIZER, new DespoilLootModifier());
		addLootModifier(ModLoot.CAT_MORNING_GIFT_SERIALIZER, new CatMorningGiftLootModifier());
	}

	protected <T extends IGlobalLootModifier> void addLootModifier(DeferredHolder<MapCodec<? extends T>, ?> serializer, T lootModifier) {
		modifiers.put(serializer.getId().getPath(), new ModifierEntry(serializer.getId(), lootModifier));
	}

	@Override
	protected CompletableFuture<?> run(CachedOutput cache, HolderLookup.Provider registries) {
		this.registries = registries;
		start();

		Path dataFolder = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK);
		RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);

		List<CompletableFuture<?>> futures = new ArrayList<>();
		JsonArray entries = new JsonArray();

		for (ModifierEntry entry : modifiers.values()) {
			entries.add(entry.serializerId().toString());

			JsonObject json = new JsonObject();
			json.addProperty("type", entry.serializerId().toString());
			json.add("conditions", encodeConditions(ops, entry.modifier()));
			futures.add(saveJson(cache, dataFolder.resolve(BiomancyMod.MOD_ID).resolve("loot_modifiers").resolve(entry.serializerId().getPath() + ".json"), json, false));
		}

		JsonObject index = new JsonObject();
		index.addProperty("replace", false);
		index.add("entries", entries);
		futures.add(saveJson(cache, dataFolder.resolve("neoforge").resolve("loot_modifiers").resolve("global_loot_modifiers.json"), index, true));

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}

	/**
	 * Serializes the loot conditions the given modifier is constructed with, using the vanilla
	 * loot condition codecs (discriminator key: {@code "condition"}).
	 */
	private static JsonArray encodeConditions(RegistryOps<JsonElement> ops, IGlobalLootModifier modifier) {
		JsonArray conditions = new JsonArray();
		for (LootItemCondition condition : getConditions(modifier)) {
			conditions.add(sortKeys(LootItemCondition.DIRECT_CODEC.encodeStart(ops, condition).getOrThrow().getAsJsonObject()));
		}
		return conditions;
	}

	private static LootItemCondition[] getConditions(IGlobalLootModifier modifier) {
		if (modifier instanceof DespoilLootModifier despoil) {
			return despoil.getConditions();
		}
		else if (modifier instanceof CatMorningGiftLootModifier) {
			//mirrors the default constructor of CatMorningGiftLootModifier, which has no public accessor like DespoilLootModifier#getConditions
			return new LootItemCondition[]{
					LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS).build(),
					LootTableIdCondition.builder(BuiltInLootTables.CAT_MORNING_GIFT.location()).build()
			};
		}

		throw new UnsupportedOperationException("Unsupported global loot modifier: " + modifier);
	}

	/**
	 * Recursively sorts the keys of a JSON object alphabetically. Since 1.21.1, the vanilla codec of
	 * {@code minecraft:entity_properties} emits the {@code "predicate"} field before {@code "entity"};
	 * the shipped files use alphabetical order, so we normalize the encoded condition objects to it.
	 */
	private static JsonElement sortKeys(JsonElement element) {
		if (!element.isJsonObject()) return element;

		JsonObject sorted = new JsonObject();
		element.getAsJsonObject().entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEach(entry -> sorted.add(entry.getKey(), sortKeys(entry.getValue())));
		return sorted;
	}

	/**
	 * Writes the JSON in pretty-printed form (2-space indentation, key order as inserted) directly through
	 * {@link CachedOutput}, because {@link DataProvider#saveStable} reorders the top-level keys
	 * (it would place {@code conditions} before {@code type} and {@code entries} before
	 * {@code replace}), which does not match the shipped files.
	 */
	@SuppressWarnings("deprecation") //Hashing.sha1() is deprecated by Guava, but that is the hash used by DataProvider#saveStable for the datagen cache
	private CompletableFuture<?> saveJson(CachedOutput cache, Path path, JsonElement element, boolean trailingNewline) {
		byte[] bytes = (GSON.toJson(element) + (trailingNewline ? "\n" : "")).getBytes(StandardCharsets.UTF_8);
		return CompletableFuture.runAsync(() -> {
			try {
				cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
			}
			catch (IOException e) {
				DataProvider.LOGGER.error("Failed to save file to {}", path, e);
			}
		});
	}

	private record ModifierEntry(ResourceLocation serializerId, IGlobalLootModifier modifier) {}

}
