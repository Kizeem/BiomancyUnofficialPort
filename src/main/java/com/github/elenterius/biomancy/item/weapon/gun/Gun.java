package com.github.elenterius.biomancy.item.weapon.gun;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public interface Gun {

	int ONE_SECOND_IN_TICKS = 20;
	int ONE_HOUR_IN_TICKS = 60 * 60 * 20;
	float MAX_INACCURACY = 1f; //0.0 - 1.0

	String AMMO_KEY = "ammo";
	String RELOAD_TIMESTAMP_KEY = "reload_timestamp";
	String WEAPON_STATE_KEY = "projectile_weapon_state";
	String SHOOT_TIMESTAMP_KEY = "shoot_timestamp";

	static CompoundTag getOrCreateCustomData(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	static void setCustomData(ItemStack stack, CompoundTag tag) {
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}

	default long getShootTimestamp(ItemStack stack) {
		return getOrCreateCustomData(stack).getLong(SHOOT_TIMESTAMP_KEY);
	}

	default void setShootTimestamp(ItemStack stack, long timestamp) {
		CompoundTag tag = getOrCreateCustomData(stack);
		tag.putLong(SHOOT_TIMESTAMP_KEY, timestamp);
		setCustomData(stack, tag);
	}

	default int getDurabilityCost(ItemStack stack) {
		return 1;
	}

	default int getAmmoCost(ItemStack stack) {
		return 1;
	}

	/// Value shouldn't be larger than max ItemStack size of 64
	default int getReloadCost(ItemStack stack) {
		return 1;
	}

	void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter);

	void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack stack);

	default GunState getGunState(ItemStack stack) {
		return GunState.fromId(getOrCreateCustomData(stack).getByte(WEAPON_STATE_KEY));
	}

	default void setGunState(ItemStack stack, GunState state) {
		CompoundTag tag = getOrCreateCustomData(stack);
		tag.putByte(WEAPON_STATE_KEY, state.getId());
		setCustomData(stack, tag);
	}

	default long getReloadStartTime(ItemStack stack) {
		return getOrCreateCustomData(stack).getLong(RELOAD_TIMESTAMP_KEY);
	}

	default void startReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (canReload(stack, shooter)) {
			setGunState(stack, GunState.RELOADING);
			CompoundTag tag = getOrCreateCustomData(stack);
			tag.putLong(RELOAD_TIMESTAMP_KEY, level.getGameTime());
			setCustomData(stack, tag);
			onReloadStarted(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
		}
	}

	default void finishReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player && player.getAbilities().instabuild) {
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
			return;
		}

		ItemStack ammoStack = findAmmoInInv(stack, shooter);
		if (!ammoStack.isEmpty() && ammoStack.getCount() >= getReloadCost(stack)) {
			ammoStack.shrink(getReloadCost(stack));
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, SoundEvents.DISPENSER_FAIL);
		}
	}

	default void stopReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);
		onReloadStopped(stack, level, shooter);
	}

	default void cancelReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);
		onReloadCanceled(stack, level, shooter);
	}

	default void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_START.value());
	}

	default void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {}

	default void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END.value());
	}

	default void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END.value());
	}

	default void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.CROSSBOW_LOADING_END.value());
	}

	default float getReloadProgress(long elapsedTime, long reloadTime) {
		return Mth.clamp((float) elapsedTime / (float) reloadTime, 0f, 1f);
	}

	default boolean canReload(ItemStack stack, LivingEntity shooter) {
		if (getAmmo(stack) >= getMaxAmmo(stack)) return false;
		ItemStack ammo = findAmmoInInv(stack, shooter);
		return !ammo.isEmpty() && ammo.getCount() >= getReloadCost(stack);
	}

	default float modifyProjectileInaccuracy(float baseInaccuracy, ItemStack stack) {
		return baseInaccuracy + (-MAX_INACCURACY * getAccuracy(stack) + MAX_INACCURACY);
	}

	float getAccuracy(ItemStack stack);

	int getDelayBetweenShots(ItemStack stack);

	default float getFireRate(ItemStack stack) {
		return ONE_SECOND_IN_TICKS / (float) getDelayBetweenShots(stack);
	}

	int getReloadDurationTicks(ItemStack stack);

	default float modifyProjectileVelocity(float baseVelocity, ItemStack stack) {
		return baseVelocity;
	}

	float modifyProjectileDamage(float baseDamage, ItemStack stack);

	int modifyProjectileKnockBack(int baseKnockBack, ItemStack stack);

	GunProperties.ShootBehavior getShootBehavior();

	int getMaxAmmo(ItemStack stack);

	ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter);

	default boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) > 0;
	}

	default int getAmmo(ItemStack stack) {
		return getOrCreateCustomData(stack).getInt(AMMO_KEY);
	}

	default void setAmmo(ItemStack stack, int amount) {
		CompoundTag nbt = getOrCreateCustomData(stack);
		nbt.putInt(AMMO_KEY, Mth.clamp(amount, 0, getMaxAmmo(stack)));
		setCustomData(stack, nbt);
	}

	default void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundTag nbt = getOrCreateCustomData(stack);
		nbt.putInt(AMMO_KEY, Math.max(0, nbt.getInt(AMMO_KEY) + amount));
		setCustomData(stack, nbt);
	}

	default void consumeAmmo(ItemStack stack, int amount) {
		addAmmo(stack, -amount);
	}

	default void consumeAmmo(LivingEntity shooter, ItemStack stack, int amount) {
		if (!(shooter instanceof Player player) || !player.getAbilities().instabuild) addAmmo(stack, -amount);
	}

	default void playSFX(Level level, LivingEntity shooter, SoundEvent soundEvent) {
		SoundSource soundSource = shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), soundEvent, soundSource, 1f, 1f / (shooter.getRandom().nextFloat() * 0.5f + 1f) + 0.2f);
	}

	enum GunState {
		NONE((byte) 0), SHOOTING_OR_CHARGING((byte) 1), RELOADING((byte) 2);

		private final byte id;

		GunState(byte id) {
			this.id = id;
		}

		public static GunState fromId(int id) {
			if (id == 0) return NONE;
			if (id == 1) return SHOOTING_OR_CHARGING;
			if (id == 2) return RELOADING;
			return NONE;
		}

		public byte getId() {
			return id;
		}
	}

}
