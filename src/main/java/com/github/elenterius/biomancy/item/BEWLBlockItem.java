package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.render.item.BEWLItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import javax.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BEWLBlockItem extends SimpleBlockItem {

	private final Supplier<BlockEntity> cachedBlockEntityWithoutLevel;

	public <T extends Block & EntityBlock> BEWLBlockItem(T block, Properties properties) {
		super(block, properties);
		Supplier<BlockEntity> delegate = () -> block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
		cachedBlockEntityWithoutLevel = new Supplier<>() {
			private BlockEntity cached;
			@Override
			public BlockEntity get() {
				if (cached == null) cached = delegate.get();
				return cached;
			}
		};
	}

	@Nullable
	public BlockEntity getCachedBEWL() {
		return cachedBlockEntityWithoutLevel.get();
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return BEWLItemRenderer.INSTANCE;
			}
		});
	}

}