package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {

	public static final EntityCapability<IFlagCap, Void> NO_KNOCKBACK_FLAG_CAP = EntityCapability.createVoid(BiomancyMod.rl("no_knockback"), IFlagCap.class);
	public static final BlockCapability<IItemHandler, Direction> ITEM_HANDLER = Capabilities.ItemHandler.BLOCK;
	public static final BlockCapability<IFluidHandler, Direction> FLUID_HANDLER = Capabilities.FluidHandler.BLOCK;

	private static final Map<LivingEntity, IFlagCap> FLAG_CAPS = new WeakHashMap<>();

	private ModCapabilities() {}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			if (LivingEntity.class.isAssignableFrom(entityType.getBaseClass())) {
				registerFlagCap(event, (EntityType<LivingEntity>) entityType);
			}
		}

		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BIO_LAB.get(), (be, side) -> {
			if (side == null || side == Direction.DOWN) return be.getOutputInventory();
			if (side == Direction.UP) return be.getInputInventory();
			return be.getCombinedInventory();
		});

		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BIO_LAB.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.PRIMORDIAL_CRADLE.get(), (be, side) -> be.getFluidConsumer());
	}

	private static <E extends LivingEntity> void registerFlagCap(RegisterCapabilitiesEvent event, EntityType<E> entityType) {
		event.registerEntity(NO_KNOCKBACK_FLAG_CAP, entityType, (entity, context) -> FLAG_CAPS.computeIfAbsent(entity, key -> new FlagCapImpl()));
	}

	public interface IFlagCap {
		boolean isEnabled();

		void set(boolean enabled);

		default void enable() {
			set(true);
		}

		default void disable() {
			set(false);
		}

		default void toggle() {
			set(!isEnabled());
		}
	}

	public static class FlagCapImpl implements IFlagCap {
		private boolean isEnabled = false;

		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void set(boolean enabled) {
			isEnabled = enabled;
		}

	}

}
