package com.github.elenterius.biomancy.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.item.weapon.DespoilingSwordItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DespoilLootModifier extends LootModifier {

	private static final Marker MARKER = MarkerManager.getMarker("DespoilLootModifier");

	public static final Supplier<MapCodec<DespoilLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst ->
			codecStart(inst).apply(inst, DespoilLootModifier::new)
	));

	public static final String LOOT_PREFIX = "biomancy/despoil/";

	public DespoilLootModifier() {
		this(
				//Can't use MatchTool, because the tool is missing for Entity Kills (1.18.2, 1.19.2)
				//only apply the loot modifier to adult mobs killed by a player
				new LootItemCondition[]{
						LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false))).build(),
						LootItemKilledByPlayerCondition.killedByPlayer().build()
				});
	}

	public DespoilLootModifier(LootItemCondition[] conditions) {
		super(conditions);
	}

	public static ResourceLocation getLootTableId(EntityType<?> entityType) {
		ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		return key.withPrefix(LOOT_PREFIX);
	}

	public static ResourceLocation getLootTableId(ResourceLocation entityTypeId) {
		return entityTypeId.withPrefix(LOOT_PREFIX);
	}

	protected static int getDespoilLevel(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
		if (killer instanceof LivingEntity livingEntity) {
			Holder<Enchantment> despoilHolder = ModEnchantments.getHolder(ModEnchantments.DESPOIL, lootContext.getLevel().registryAccess());
			int itemDespoilLevel = despoilHolder.value().getSlotItems(livingEntity).values().stream()
					.mapToInt(stack -> getDespoilLevel(despoilHolder, stack))
					.max()
					.orElse(0);

			MobEffectInstance effectInstance = livingEntity.getEffect(ModMobEffects.DESPOIL);
			int effectDespoilLevel = effectInstance != null ? effectInstance.getAmplifier() + 1 : 0;

			ItemStack mainHand = livingEntity.getMainHandItem();
			ItemStack offHand = livingEntity.getOffhandItem();
			if (isDespoilingSickle(mainHand) || isDespoilingSickle(offHand)) {
				itemDespoilLevel = Math.max(itemDespoilLevel, 1);
			}

			return Math.max(itemDespoilLevel, effectDespoilLevel);
		}

		return 0;
	}

	private static boolean isDespoilingSickle(ItemStack stack) {
		return stack.getItem() instanceof DespoilingSwordItem sword && sword.isNotBroken(stack);
	}

	protected static int getDespoilLevel(Holder<Enchantment> despoilHolder, ItemStack stack) {
		return stack.getEnchantmentLevel(despoilHolder);
	}

	protected static boolean isUsingTool(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.ATTACKING_ENTITY);

		if (killer instanceof LivingEntity livingEntity) {
			ItemStack stack = livingEntity.getMainHandItem();
			return stack.is(Tags.Items.TOOLS);
		}

		return false;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	public LootItemCondition[] getConditions() {
		return conditions;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);

		if (thisEntity instanceof LivingEntity || thisEntity instanceof Player) {
			LootTable lootTable = getLootTable(context.getLevel(), thisEntity);
			if (lootTable == LootTable.EMPTY) {
				BiomancyMod.LOGGER.warn(MARKER, "Missing despoil loot table for entity type '{}'", BuiltInRegistries.ENTITY_TYPE.getKey(thisEntity.getType()));
				return generatedLoot;
			}

			int despoilLevel = getDespoilLevel(context);
			if (despoilLevel <= 0) return generatedLoot;

			boolean usingTool = isUsingTool(context);
			float despoilChance = (usingTool ? 0.32f : 0.16f) + 0.32f * despoilLevel;

			int itemCount = generatedLoot.size();
			LootParams lootParams = createLootParams(context);
			Consumer<ItemStack> stackSplitter = LootTable.createStackSplitter(context.getLevel(), generatedLoot::add);

			for (int roll = 0; roll < despoilLevel; roll++) {
				if (context.getRandom().nextFloat() > despoilChance) continue;
				getRandomItems(lootTable, lootParams, stackSplitter);
			}

			BiomancyMod.LOGGER.info(MARKER, "Despoil applied to '{}': level={}, chance={}, dropped={} item(s)", BuiltInRegistries.ENTITY_TYPE.getKey(thisEntity.getType()), despoilLevel, despoilChance, generatedLoot.size() - itemCount);
		}

		return generatedLoot;
	}

	private static void getRandomItems(LootTable lootTable, LootParams lootParams, Consumer<ItemStack> output) {
		//noinspection deprecation
		lootTable.getRandomItemsRaw(lootParams, output); //we use the 'Raw' method to prevent a stackoverflow caused by calling of ForgeHooks#modifyLoot inside GlobalLootModifiers
	}

	protected LootTable getLootTable(ServerLevel level, Entity entity) {
		ResourceLocation lootTableId = getLootTableId(entity.getType());
		ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
		return level.getServer().reloadableRegistries().getLootTable(key);
	}

	protected LootParams createLootParams(LootContext context) {
		LootParams.Builder builder = new LootParams.Builder(context.getLevel())
				.withParameter(LootContextParams.THIS_ENTITY, context.getParam(LootContextParams.THIS_ENTITY))
				.withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
				.withParameter(LootContextParams.DAMAGE_SOURCE, context.getParam(LootContextParams.DAMAGE_SOURCE))
				.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, context.getParamOrNull(LootContextParams.ATTACKING_ENTITY))
				.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY));

		if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
			Player player = context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
			builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
		}

		return builder.create(LootContextParamSets.ENTITY);
	}

}
