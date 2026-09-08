package com.github.elenterius.biomancy.fluid;

import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class AcidBlock extends LiquidBlock {

	public AcidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
		super(fluid, properties);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	protected void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
		if (level.random.nextFloat() > 0.4f) return;

		for (int i = 0; i < 3; i++) {
			float p = level.random.nextFloat();
			int yOffset;
			if (p < 0.2f) yOffset = 1;
			else if (p < 0.6f) yOffset = -1;
			else yOffset = 0;

			BlockPos targetPos = blockPos.offset(random.nextInt(3) - 1, yOffset, random.nextInt(3) - 1);

			if (!level.isLoaded(targetPos)) return;

			BlockState targetState = level.getBlockState(targetPos);
			if (targetState.isAir()) return;

			if (level.random.nextFloat() >= 0.1f) {
				Block block = targetState.getBlock();
				if (corrodeCopper(level, targetPos, block, targetState)) continue;
				if (destroyBlock(level, targetPos, block, targetState)) continue;
				if (erodeBlock(level, targetPos, block, targetState)) continue;
			}
			else if (level.random.nextFloat() < 0.5f) {
				Block block = targetState.getBlock();
				destroyFleshVeins(level, targetPos, block, targetState);
			}
		}
	}

	protected boolean corrodeCopper(ServerLevel level, BlockPos pos, Block block, BlockState blockState) {
		if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, state));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
			return true;
		}

		return false;
	}

	protected boolean destroyBlock(ServerLevel level, BlockPos pos, Block block, BlockState blockState) {
		if (!blockState.is(ModBlockTags.ACID_DESTRUCTIBLE)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	protected boolean erodeBlock(ServerLevel level, BlockPos pos, Block block, BlockState blockState) {
		if (!AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.containsKey(block)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.get(block));
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	protected void destroyFleshVeins(ServerLevel level, BlockPos pos, Block block, BlockState blockState) {
		if (block instanceof FleshVeinsBlock) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(LEVEL).equals(7)) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
			}
		}
		else if (random.nextInt(10) == 0) {
			level.addParticle(ParticleTypes.UNDERWATER, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0, 0, 0);
		}
	}
}
