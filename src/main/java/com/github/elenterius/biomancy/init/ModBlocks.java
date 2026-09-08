package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import com.github.elenterius.biomancy.block.base.DirectionalPillarSlabBlock;
import com.github.elenterius.biomancy.block.base.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.bioforge.BioForgeBlock;
import com.github.elenterius.biomancy.block.biolab.BioLabBlock;
import com.github.elenterius.biomancy.block.bloom.BloomBlock;
import com.github.elenterius.biomancy.block.cauldron.AcidCauldron;
import com.github.elenterius.biomancy.block.chrysalis.ChrysalisBlock;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlock;
import com.github.elenterius.biomancy.block.digester.DigesterBlock;
import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlock;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.membrane.*;
import com.github.elenterius.biomancy.block.modularlarynx.ModularLarynxBlock;
import com.github.elenterius.biomancy.block.orifice.OrificeBlock;
import com.github.elenterius.biomancy.block.ownable.OwnablePressurePlateBlock;
import com.github.elenterius.biomancy.block.splatter.AcidSplatterBlock;
import com.github.elenterius.biomancy.block.splatter.VolatileSplatterBlock;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlock;
import com.github.elenterius.biomancy.block.tongue.TongueBlock;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.fluid.AcidBlock;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModBlocks {

	private static final float FLESH_DESTROY_SPEED = 3f;
	private static final float FLESH_EXPLOSION_RESISTANCE = 3f;

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, BiomancyMod.MOD_ID);

	//## Manual Crafting
	public static final DeferredHolder<Block, ?> PRIMORDIAL_CRADLE = register("primordial_cradle", PrimordialCradleBlock::new);
	public static final DeferredHolder<Block, ?> BIO_FORGE = register("bio_forge", BioForgeBlock::new);

	//## Machine
	public static final DeferredHolder<Block, ?> DECOMPOSER = register("decomposer", properties -> new DecomposerBlock(properties.noOcclusion()));
	public static final DeferredHolder<Block, ?> BIO_LAB = register("bio_lab", BioLabBlock::new);
	public static final DeferredHolder<Block, ?> DIGESTER = register("digester", DigesterBlock::new);

	//## Automation, Redstone & Storage
	public static final DeferredHolder<Block, ?> STORAGE_SAC = register("storage_sac", StorageSacBlock::new);
	public static final DeferredHolder<Block, ?> TONGUE = register("tongue", TongueBlock::new);
	public static final DeferredHolder<Block, ?> MAW_HOPPER = register("maw_hopper", MawHopperBlock::new);
	public static final DeferredHolder<Block, ?> FLESHKIN_CHEST = register("fleshkin_chest", properties -> new FleshkinChestBlock(properties, FLESH_DESTROY_SPEED));
	public static final DeferredHolder<Block, ?> FLESHKIN_PRESSURE_PLATE = register("fleshkin_pressure_plate", properties -> new OwnablePressurePlateBlock(properties, ModBlockSetTypes.FLESH_SET_TYPE.get()));
	public static final DeferredHolder<Block, ?> CHRYSALIS = register("chrysalis", ChrysalisBlock::new);

	//## Building Materials
	public static final DeferredHolder<Block, ?> FLESH = register("flesh", FleshBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_STAIRS = registerStairs(FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_WALL = registerWall(FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_SLAB = registerSlab(FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> PACKED_FLESH = register("packed_flesh", () -> new FleshBlock(createToughFleshProperties()));
	public static final DeferredHolder<Block, ?> PACKED_FLESH_STAIRS = registerStairs(PACKED_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> PACKED_FLESH_WALL = registerWall(PACKED_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> PACKED_FLESH_SLAB = registerSlab(PACKED_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_FLESH = register("fibrous_flesh", FleshBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_FLESH_STAIRS = registerStairs(FIBROUS_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_FLESH_WALL = registerWall(FIBROUS_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_FLESH_SLAB = registerSlab(FIBROUS_FLESH, DirectionalSlabBlock::new);

	//## Decoration
	public static final DeferredHolder<Block, ?> TUBULAR_FLESH_BLOCK = register("tubular_flesh", RotatedPillarBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_PILLAR = register("flesh_pillar", () -> new RotatedPillarBlock(createBonyFleshProperties()));
	public static final DeferredHolder<Block, ?> CHISELED_FLESH = register("chiseled_flesh", () -> new FleshBlock(createBonyFleshProperties()));
	public static final DeferredHolder<Block, ?> ORNATE_FLESH = register("ornate_flesh", () -> new RotatedPillarBlock(createBonyFleshProperties()));
	public static final DeferredHolder<Block, ?> ORNATE_FLESH_SLAB = registerSlab(ORNATE_FLESH, DirectionalPillarSlabBlock::new);

	//## Primal Ecosystem
	public static final DeferredHolder<Block, ?> PRIMAL_FLESH = register("primal_flesh", properties -> new FleshBlock(properties, ModPlantTypes.PlantType.PRIMAL_FLESH));
	public static final DeferredHolder<Block, ?> PRIMAL_FLESH_STAIRS = registerStairs(PRIMAL_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> PRIMAL_FLESH_SLAB = registerSlab(PRIMAL_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> PRIMAL_FLESH_WALL = registerWall(PRIMAL_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> SMOOTH_PRIMAL_FLESH = register("smooth_primal_flesh", properties -> new FleshBlock(properties, ModPlantTypes.PlantType.PRIMAL_FLESH));
	public static final DeferredHolder<Block, ?> SMOOTH_PRIMAL_FLESH_STAIRS = registerStairs(SMOOTH_PRIMAL_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> SMOOTH_PRIMAL_FLESH_SLAB = registerSlab(SMOOTH_PRIMAL_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> SMOOTH_PRIMAL_FLESH_WALL = registerWall(SMOOTH_PRIMAL_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_PRIMAL_FLESH = register("fibrous_primal_flesh", properties -> new FleshBlock(properties, ModPlantTypes.PlantType.PRIMAL_FLESH));
	public static final DeferredHolder<Block, ?> FIBROUS_PRIMAL_FLESH_STAIRS = registerStairs(FIBROUS_PRIMAL_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_PRIMAL_FLESH_WALL = registerWall(FIBROUS_PRIMAL_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> FIBROUS_PRIMAL_FLESH_SLAB = registerSlab(FIBROUS_PRIMAL_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> POROUS_PRIMAL_FLESH = register("porous_primal_flesh", properties -> new FleshBlock(properties, ModPlantTypes.PlantType.PRIMAL_FLESH));
	public static final DeferredHolder<Block, ?> POROUS_PRIMAL_FLESH_STAIRS = registerStairs(POROUS_PRIMAL_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> POROUS_PRIMAL_FLESH_SLAB = registerSlab(POROUS_PRIMAL_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> POROUS_PRIMAL_FLESH_WALL = registerWall(POROUS_PRIMAL_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> MALIGNANT_FLESH = register("malignant_flesh", properties -> new FleshBlock(properties, ModPlantTypes.PlantType.PRIMAL_FLESH));
	public static final DeferredHolder<Block, ?> MALIGNANT_FLESH_STAIRS = registerStairs(MALIGNANT_FLESH, StairBlock::new);
	public static final DeferredHolder<Block, ?> MALIGNANT_FLESH_SLAB = registerSlab(MALIGNANT_FLESH, DirectionalSlabBlock::new);
	public static final DeferredHolder<Block, ?> MALIGNANT_FLESH_WALL = registerWall(MALIGNANT_FLESH, WallBlock::new);
	public static final DeferredHolder<Block, ?> MALIGNANT_FLESH_VEINS = register("malignant_flesh_veins", () -> new FleshVeinsBlock(createFleshVeinsProperties().noCollission().noOcclusion()));
	public static final DeferredHolder<Block, ?> PRIMAL_BLOOM = register("primal_bloom", properties -> new BloomBlock(properties.randomTicks().noOcclusion().lightLevel(BloomBlock::getLightEmission)));
	public static final DeferredHolder<Block, ?> BLOOMLIGHT = register("bloomlight", properties -> new Block(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15)));
	public static final DeferredHolder<Block, ?> PRIMAL_ORIFICE = register("primal_orifice", properties -> new OrificeBlock(properties.randomTicks().lightLevel(OrificeBlock.lightEmission(7))));
	public static final DeferredHolder<Block, ?> PRIMAL_BONE = register("primal_bone_block", () -> new RotatedPillarBlock(createBoneProperties()));

	//## Utility
	public static final DeferredHolder<Block, ?> MODULAR_LARYNX = register("modular_larynx", ModularLarynxBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_SPIKE = register("flesh_spike", () -> new FleshSpikeBlock(createBonyFleshProperties().noOcclusion()));
	public static final DeferredHolder<Block, ?> VIAL_HOLDER = register("vial_holder", VialHolderBlock::new);
	public static final DeferredHolder<Block, ?> JUMP_PAD = register("active_muscle", JumpPadBlock::new);
	//public static final DeferredHolder<Block, ?> NEURAL_INTERCEPTOR = register("neural_interceptor", NeuralInterceptorBlock::new);

	//## Membranes
	public static final DeferredHolder<Block, ?> IMPERMEABLE_MEMBRANE = registerMembrane("impermeable_membrane", IgnoreEntityCollisionPredicate.NEVER);
	public static final DeferredHolder<Block, ?> IMPERMEABLE_MEMBRANE_PANE = registerMembranePane("impermeable_membrane_pane", IgnoreEntityCollisionPredicate.NEVER);
	public static final DeferredHolder<Block, ?> BABY_PERMEABLE_MEMBRANE = registerMembrane("baby_permeable_membrane", IgnoreEntityCollisionPredicate.IS_BABY_MOB);
	public static final DeferredHolder<Block, ?> BABY_PERMEABLE_MEMBRANE_PANE = registerMembranePane("baby_permeable_membrane_pane", IgnoreEntityCollisionPredicate.IS_BABY_MOB);
	public static final DeferredHolder<Block, ?> ADULT_PERMEABLE_MEMBRANE = registerMembrane("adult_permeable_membrane", IgnoreEntityCollisionPredicate.IS_ADULT_MOB);
	public static final DeferredHolder<Block, ?> ADULT_PERMEABLE_MEMBRANE_PANE = registerMembranePane("adult_permeable_membrane_pane", IgnoreEntityCollisionPredicate.IS_ADULT_MOB);
	public static final DeferredHolder<Block, ?> PRIMAL_PERMEABLE_MEMBRANE = registerMembrane("primal_permeable_membrane", IgnoreEntityCollisionPredicate.IS_ALIVE_MOB, SpreadingMembraneBlock::new);
	public static final DeferredHolder<Block, ?> PRIMAL_PERMEABLE_MEMBRANE_PANE = registerMembranePane("primal_permeable_membrane_pane", IgnoreEntityCollisionPredicate.IS_ALIVE_MOB);
	public static final DeferredHolder<Block, ?> UNDEAD_PERMEABLE_MEMBRANE = registerMembrane("undead_permeable_membrane", IgnoreEntityCollisionPredicate.IS_UNDEAD_MOB);
	public static final DeferredHolder<Block, ?> UNDEAD_PERMEABLE_MEMBRANE_PANE = registerMembranePane("undead_permeable_membrane_pane", IgnoreEntityCollisionPredicate.IS_UNDEAD_MOB);
	public static final DeferredHolder<Block, ?> BIOMETRIC_MEMBRANE = registerMembrane("biometric_membrane", BiometricMembraneBlock::new);
	public static final DeferredHolder<Block, ?> ONEWAY_MEMBRANE = registerMembrane("oneway_membrane", OnewayMembraneBlock::new);

	//## Light Sources
	public static final DeferredHolder<Block, ?> PRIMORDIAL_BIO_LANTERN = register("primordial_bio_lantern", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));
	public static final DeferredHolder<Block, ?> YELLOW_BIO_LANTERN = register("bio_lantern_yellow", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));
	public static final DeferredHolder<Block, ?> BLUE_BIO_LANTERN = register("bio_lantern_blue", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));

	//## Fluids
	public static final DeferredHolder<Block, ?> ACID_FLUID_BLOCK = register("acid_fluid_block", () -> new AcidBlock((FlowingFluid) ModFluids.ACID.get(), copyProperties(Blocks.WATER)));
	public static final DeferredHolder<Block, ?> ACID_CAULDRON = register("acid_cauldron", () -> new AcidCauldron(copyProperties(Blocks.CAULDRON)));
	public static final DeferredHolder<Block, ?> ACID_SPLATTER = register("acid_splatter", () -> new AcidSplatterBlock(createProperties().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.FROGSPAWN)));
	public static final DeferredHolder<Block, ?> VOLATILE_SPLATTER = register("volatile_splatter", () -> new VolatileSplatterBlock(createProperties().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.FROGSPAWN)));
	public static final DeferredHolder<Block, ?> WATER_GEL_BLOCK = register("water_gel_block", properties -> new WaterGelBlock(properties.mapColor(MapColor.WATER).strength(0.5f, 100f).speedFactor(0.8f).jumpFactor(1.2f).noOcclusion().sound(ModSoundTypes.GEL_BLOCK).isRedstoneConductor(ModBlocks::neverValid)));

	//## Misc
	public static final DeferredHolder<Block, ?> FLESH_LADDER = register("flesh_ladder", () -> new LadderBlock(createBonyFleshProperties().noOcclusion()));
	public static final DeferredHolder<Block, ?> FLESH_FENCE = register("flesh_fence", FleshFenceBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_FENCE_GATE = register("flesh_fence_gate", () -> new FleshFenceGateBlock(createBonyFleshProperties().noOcclusion()));
	public static final DeferredHolder<Block, ?> FLESH_IRIS_DOOR = register("flesh_iris_door", IrisDoorBlock::new);
	public static final DeferredHolder<Block, ?> FLESH_DOOR = register("flesh_door", FleshDoorBlock::new);
	public static final DeferredHolder<Block, ?> FULL_FLESH_DOOR = register("full_flesh_door", FullFleshDoorBlock::new);
	public static final DeferredHolder<Block, ?> TENDON_CHAIN = register("tendon_chain", properties -> new FleshChainBlock(properties.noOcclusion()));

	private ModBlocks() {}

	private static <T extends Block> DeferredHolder<Block, ?> register(String name, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(createFleshProperties()));
	}

	private static <T extends Block> DeferredHolder<Block, ?> register(String name, Supplier<T> factory) {
		return BLOCKS.register(name, factory);
	}

	private static <T extends StairBlock> DeferredHolder<Block, ?> registerStairs(DeferredHolder<Block, ? extends Block> parent, StairBlockFactory<T> factory) {
		return registerStairs(parent.getId().getPath() + "_stairs", parent, factory);
	}

	private static <T extends StairBlock> DeferredHolder<Block, ?> registerStairs(String name, DeferredHolder<Block, ? extends Block> parent, StairBlockFactory<T> factory) {
		return BLOCKS.register(name, () -> factory.create(parent.get().defaultBlockState(), copyProperties(parent.get())));
	}

	private static <T extends WallBlock> DeferredHolder<Block, ?> registerWall(DeferredHolder<Block, ? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return registerWall(parent.getId().getPath() + "_wall", parent, factory);
	}

	private static <T extends WallBlock> DeferredHolder<Block, ?> registerWall(String name, DeferredHolder<Block, ? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(copyProperties(parent.get())));
	}

	private static <T extends DirectionalSlabBlock> DeferredHolder<Block, ?> registerSlab(DeferredHolder<Block, ? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return registerSlab(parent.getId().getPath() + "_slab", parent, factory);
	}

	private static <T extends DirectionalSlabBlock> DeferredHolder<Block, ?> registerSlab(String name, DeferredHolder<Block, ? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(copyProperties(parent.get())));
	}

	private static DeferredHolder<Block, ?> registerMembrane(String name, IgnoreEntityCollisionPredicate predicate) {
		return registerMembrane(name, predicate, MembraneBlock::new);
	}

	private static <T extends MembraneBlock> DeferredHolder<Block, ?> registerMembrane(String name, IgnoreEntityCollisionPredicate predicate, MembraneBlockFactory<T> factory) {
		return register(name, props -> {
			props = props.noOcclusion().isRedstoneConductor(ModBlocks::neverValid).isSuffocating(ModBlocks::neverValid).isViewBlocking(ModBlocks::neverValid);
			return factory.create(props, predicate);
		});
	}

	private static <T extends Block & Membrane> DeferredHolder<Block, ?> registerMembrane(String name, Function<BlockBehaviour.Properties, T> factory) {
		return register(name, properties -> {
			properties = properties.noOcclusion().isRedstoneConductor(ModBlocks::neverValid).isSuffocating(ModBlocks::neverValid).isViewBlocking(ModBlocks::neverValid);
			return factory.apply(properties);
		});
	}

	private static DeferredHolder<Block, ?> registerMembranePane(String name, IgnoreEntityCollisionPredicate predicate) {
		return registerMembranePane(name, predicate, MembranePaneBlock::new);
	}

	private static <T extends MembranePaneBlock> DeferredHolder<Block, ?> registerMembranePane(String name, IgnoreEntityCollisionPredicate predicate, MembranePaneBlockFactory<T> factory) {
		return register(name, props -> {
			props = props.noOcclusion().isRedstoneConductor(ModBlocks::neverValid).isSuffocating(ModBlocks::neverValid).isViewBlocking(ModBlocks::neverValid);
			return factory.create(props, predicate);
		});
	}

	private static <T extends MembranePaneBlock> DeferredHolder<Block, ?> registerMembranePane(String name, Function<BlockBehaviour.Properties, T> factory) {
		return register(name, properties -> {
			properties = properties.noOcclusion().isRedstoneConductor(ModBlocks::neverValid).isSuffocating(ModBlocks::neverValid).isViewBlocking(ModBlocks::neverValid);
			return factory.apply(properties);
		});
	}

	public static BlockBehaviour.Properties copyProperties(BlockBehaviour behaviour) {
		return BlockBehaviour.Properties.ofFullCopy(behaviour);
	}

	public static BlockBehaviour.Properties createProperties() {
		return BlockBehaviour.Properties.of();
	}

	public static BlockBehaviour.Properties createFleshProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(FLESH_DESTROY_SPEED, FLESH_EXPLOSION_RESISTANCE).sound(ModSoundTypes.FLESH_BLOCK).isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static BlockBehaviour.Properties createFleshVeinsProperties() {
		return BlockBehaviour.Properties.of()
				.strength(FLESH_DESTROY_SPEED, FLESH_EXPLOSION_RESISTANCE)
				//.forceSolidOff()
				.noCollission()
				.pushReaction(PushReaction.DESTROY)
				.sound(ModSoundTypes.FLESH_BLOCK)
				.isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static BlockBehaviour.Properties createToughFleshProperties() {
		return createFleshProperties().strength(FLESH_DESTROY_SPEED * 2, FLESH_EXPLOSION_RESISTANCE * 4);
	}

	public static BlockBehaviour.Properties createBonyFleshProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(FLESH_DESTROY_SPEED + 1, FLESH_EXPLOSION_RESISTANCE * 2).sound(ModSoundTypes.BONY_FLESH_BLOCK).isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static BlockBehaviour.Properties createBoneProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).instrument(NoteBlockInstrument.XYLOPHONE).requiresCorrectToolForDrops().strength(FLESH_DESTROY_SPEED * 2, FLESH_EXPLOSION_RESISTANCE * 4).sound(SoundType.BONE_BLOCK).isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static boolean isValidFleshkinSpawn(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return entityType.is(ModEntityTags.FLESHKIN) && state.isFaceSturdy(level, pos, Direction.UP);
	}

	public static boolean neverValid(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

	public static boolean neverValid(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}


	interface StairBlockFactory<T extends StairBlock> {
		T create(BlockState state, BlockBehaviour.Properties properties);
	}

	interface MembraneBlockFactory<T extends MembraneBlock> {
		T create(BlockBehaviour.Properties properties, IgnoreEntityCollisionPredicate predicate);
	}

	interface MembranePaneBlockFactory<T extends MembranePaneBlock> {
		T create(BlockBehaviour.Properties properties, IgnoreEntityCollisionPredicate predicate);
	}
}
