package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.Map;

public abstract class GunbladeItem extends GunItem {

	protected final Multimap<Attribute, AttributeModifier> defaultBladeModifiers;
	protected final Multimap<Attribute, AttributeModifier> defaultGunModifiers;

	protected GunbladeItem(Properties itemProperties, BladeProperties bladeProperties, GunProperties gunProperties, ModProjectiles.ConfiguredProjectile<?> projectile) {
		super(itemProperties, gunProperties, projectile);

		defaultBladeModifiers = createDefaultBladeModifiers(bladeProperties);
		defaultGunModifiers = createDefaultGunModifiers(bladeProperties);
	}

	public static GunbladeMode getMode(ItemStack stack) {
		return GunbladeMode.from(stack);
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		return KeyPressResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		GunState gunState = getGunState(stack);

		if (gunState == GunState.RELOADING) {
			cancelReload(stack, level, player);
		}

		GunbladeMode.set(stack, GunbladeMode.from(stack) == GunbladeMode.RANGED ? GunbladeMode.MELEE : GunbladeMode.RANGED);
		onChangeGunbladeMode(level, player, stack);
	}

	public void onChangeGunbladeMode(ServerLevel level, LivingEntity shooter, ItemStack stack) {}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (GunbladeMode.from(stack) == GunbladeMode.RANGED) {
			return useInRangedMode(level, player, usedHand, stack);
		}
		else {
			return useInMeleeMode(level, player, usedHand, stack);
		}
	}

	public InteractionResultHolder<ItemStack> useInRangedMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		return super.use(level, player, usedHand);
	}

	public InteractionResultHolder<ItemStack> useInMeleeMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		if (GunbladeMode.from(stack) != GunbladeMode.RANGED) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
		}
		else {
			super.onUseTick(level, shooter, stack, remainingUseDuration);
		}
	}

	protected Multimap<Attribute, AttributeModifier> createDefaultBladeModifiers(BladeProperties bladeProperties) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE.value(), new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, bladeProperties.attackDamageModifier(), AttributeModifier.Operation.ADD_VALUE));
		builder.put(Attributes.ATTACK_SPEED.value(), new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, bladeProperties.attackSpeedModifier(), AttributeModifier.Operation.ADD_VALUE));
		return builder.build();
	}

	protected Multimap<Attribute, AttributeModifier> createDefaultGunModifiers(BladeProperties bladeProperties) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE.value(), new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, Math.max(bladeProperties.attackDamageModifier() - 2d, 0.5d), AttributeModifier.Operation.ADD_VALUE));
		builder.put(Attributes.ATTACK_SPEED.value(), new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, Math.max(bladeProperties.attackSpeedModifier() - 0.2d, -3.8d), AttributeModifier.Operation.ADD_VALUE));
		return builder.build();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return GunbladeMode.from(stack).isBlade() ? createItemAttributeModifiers(defaultBladeModifiers) : createItemAttributeModifiers(defaultGunModifiers);
	}

	protected static ItemAttributeModifiers createItemAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers) {
		ItemAttributeModifiers itemModifiers = ItemAttributeModifiers.EMPTY;
		for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
			itemModifiers = itemModifiers.withModifierAdded(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(entry.getKey()), entry.getValue(), EquipmentSlotGroup.MAINHAND);
		}
		return itemModifiers;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
		return GunbladeMode.from(stack).isBlade() && toolAction != ItemAbilities.SWORD_SWEEP && ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (GunbladeMode.from(stack).isGun()) return 0.5f;

		if (state.is(Blocks.COBWEB)) {
			return 15f;
		}

		return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5f : 1f;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (state.getDestroySpeed(level, pos) != 0f) {
			stack.hurtAndBreak(2, miningEntity, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(Blocks.COBWEB);
	}

	public enum GunbladeMode {
		MELEE, RANGED;

		static final String KEY = "biomancy:gunblade_mode";

		public static GunbladeMode from(ItemStack stack) {
			CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
			if (tag == null) return MELEE;

			return GunbladeMode.values()[tag.getByte("ordinal")];
		}

		public static void set(ItemStack stack, GunbladeMode mode) {
			CompoundTag tag = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
			tag.putByte("ordinal", (byte) mode.ordinal());
			stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
		}

		public boolean isBlade() {
			return this == MELEE;
		}

		public boolean isGun() {
			return this == RANGED;
		}
	}

}
