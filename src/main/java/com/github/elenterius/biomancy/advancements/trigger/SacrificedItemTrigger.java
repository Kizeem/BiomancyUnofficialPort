package com.github.elenterius.biomancy.advancements.trigger;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTriggers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public class SacrificedItemTrigger extends SimpleCriterionTrigger<SacrificedItemTrigger.TriggerInstance> {

	private static final ResourceLocation ID = BiomancyMod.rl("sacrificed_item");

	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack stack) {
		trigger(player, instance -> instance.matches(stack));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
		).apply(instance, TriggerInstance::new));

		public static Criterion<TriggerInstance> sacrificedItems(ItemLike... items) {
			return sacrificedItem(ItemPredicate.Builder.item().of(items).build());
		}

		public static Criterion<TriggerInstance> sacrificedItem(ItemLike item) {
			return sacrificedItem(ItemPredicate.Builder.item().of(item).build());
		}

		public static Criterion<TriggerInstance> sacrificedItem(TagKey<Item> tag) {
			return sacrificedItem(ItemPredicate.Builder.item().of(tag).build());
		}

		public static Criterion<TriggerInstance> sacrificedItem() {
			return ModTriggers.SACRIFICED_ITEM_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
		}

		public static Criterion<TriggerInstance> sacrificedItem(ItemPredicate predicate) {
			return ModTriggers.SACRIFICED_ITEM_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(predicate)));
		}

		public boolean matches(ItemStack stack) {
			return item.isEmpty() || item.get().test(stack);
		}
	}

}
