package com.github.elenterius.spatialdb;

import com.github.elenterius.spatialdb.geometry.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class SpatialDBManager {

	private static final SpatialDBManager INSTANCE = new SpatialDBManager();

	private SpatialDBManager() {}

	public static SpatialDBManager getInstance(Level level) {
		return INSTANCE;
	}

	public Shape getClosestShape(Level level, BlockPos pos, Predicate<?> filter) {
		return null;
	}

	public Shape getOrCreateShape(Level level, BlockPos pos, Supplier<Shape> factory) {
		return factory.get();
	}

	public void removeShape(Level level, BlockPos pos) {
	}

	public Shape getAnyShape(Level level, Object entity, SpatialQueryStrategy strategy, Predicate<Shape> predicate) {
		return null;
	}
}