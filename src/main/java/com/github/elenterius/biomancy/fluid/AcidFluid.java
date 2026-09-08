package com.github.elenterius.biomancy.fluid;

import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import com.github.elenterius.biomancy.init.ModBlocks;

public abstract class AcidFluid extends FlowingFluid {

	@Override
	public FluidType getFluidType() {
		return ModFluids.ACID_TYPE.get();
	}

	@Override
	protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
		return toBlockState.getBlock() instanceof FleshVeinsBlock || super.canSpreadTo(level, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluid);
	}

	@Override
	protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
		if (state.getBlock() instanceof FleshVeinsBlock) {
			beforeDestroyingBlock(level, pos, state);
			level.setBlock(pos, fluidState.createLegacyBlock(), 3);
			return;
		}

		super.spreadTo(level, pos, state, direction, fluidState);
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		if (state.is(ModBlockTags.ACID_DESTRUCTIBLE)) return; //don't drop any block resources i.e. "destroy" them

		Block.dropResources(state, level, pos, level.getBlockEntity(pos));
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModBlocks.ACID_FLUID_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}

	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == ModFluids.ACID.get() || fluid == ModFluids.FLOWING_ACID.get();
	}

	@Override
	public Item getBucket() {
		return ModItems.ACID_BUCKET.get();
	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return 2;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return 2;
	}

	@Override
	protected boolean canConvertToSource(Level level) {
		return false;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return 5;
	}

	@Override
	protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
		super.createFluidStateDefinition(builder);
		builder.add(LEVEL);
	}

	public static class Flowing extends AcidFluid {

		public Flowing() {
			super();
			this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		public Fluid getFlowing() {
			return ModFluids.FLOWING_ACID.get();
		}

		@Override
		public Fluid getSource() {
			return ModFluids.ACID.get();
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends AcidFluid {

		public Source() {
			super();
		}

		@Override
		public Fluid getFlowing() {
			return ModFluids.FLOWING_ACID.get();
		}

		@Override
		public Fluid getSource() {
			return ModFluids.ACID.get();
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

}
