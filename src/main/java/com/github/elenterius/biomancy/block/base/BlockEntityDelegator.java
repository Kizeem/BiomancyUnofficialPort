package com.github.elenterius.biomancy.block.base;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import javax.annotation.Nullable;

@Deprecated
public class BlockEntityDelegator extends SimpleSyncedBlockEntity implements IBlockEntityDelegator {

	private BlockPos delegatePos = BlockPos.ZERO;
	private boolean isValid = false;

	public BlockEntityDelegator(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BE_DELEGATOR.get(), pos, state);
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {}

	@Override
	public BlockPos getDelegatePos() {
		return delegatePos;
	}

	@Override
	public @Nullable BlockEntity getDelegate() {
		if (level != null && isValid && !remove) {
			BlockEntity blockEntity = level.getBlockEntity(delegatePos);
			if (blockEntity != null && (blockEntity == this || blockEntity.isRemoved())) { //catch self reference
				setDelegate(null);
				return null;
			}
			if (blockEntity == null) {
				isValid = false;
			}
			return blockEntity;
		}
		return null;
	}

	@Override
	public void setDelegate(@Nullable BlockEntity blockEntity) {
		if (blockEntity == this || blockEntity == null) { //prevent self reference
			delegatePos = BlockPos.ZERO;
			isValid = false;
		}
		if (blockEntity != null) {
			delegatePos = blockEntity.getBlockPos();
			isValid = true;
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		isValid = false;
		if (tag.contains("DelegatePos")) {
			delegatePos = BlockPos.of(tag.getLong("DelegatePos"));
			isValid = true;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		if (isValid) {
			tag.putLong("DelegatePos", delegatePos.asLong());
		}
	}

	public <T, C> T getCapability(BlockCapability<T, C> capability, @Nullable C context) {
		if (!remove) {
			BlockEntity delegate = getDelegate();
			if (delegate != null && !delegate.isRemoved()) {
				return level.getCapability(capability, delegate.getBlockPos(), context);
			}
		}
		return null;
	}

}
