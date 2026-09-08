package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModPlantTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public class FleshBlock extends Block {

	protected final ModPlantTypes.PlantType supportedPlantType;

	public FleshBlock(Properties properties) {
		this(properties, ModPlantTypes.PlantType.FLESH);
	}

	public FleshBlock(Properties properties, ModPlantTypes.PlantType supportedPlantType) {
		super(properties);
		this.supportedPlantType = supportedPlantType;
	}

	@Override
	public TriState canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, BlockState plant) {
		return TriState.TRUE;
	}

}
