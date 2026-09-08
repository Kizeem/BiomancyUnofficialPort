package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.item.*;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.shield.ThornShieldItem;
import com.github.elenterius.biomancy.item.weapon.DespoilingSwordItem;
import com.github.elenterius.biomancy.item.weapon.GrenadeItem;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import com.github.elenterius.biomancy.item.weapon.gun.CausticGunbladeItem;
import com.github.elenterius.biomancy.item.weapon.gun.DevArmCannonItem;
import com.github.elenterius.biomancy.item.weapon.gun.ImpalerItem;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BiomancyMod.MOD_ID);
	public static final DeferredRegister<Item> DEV_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, BiomancyMod.MOD_ID);

	//# Material / Mob Loot
	public static final DeferredHolder<Item, ?> MOB_FANG = registerSimpleItem("mob_fang");
	public static final DeferredHolder<Item, ?> MOB_CLAW = registerSimpleItem("mob_claw");
	public static final DeferredHolder<Item, ?> MOB_SINEW = registerSimpleItem("mob_sinew", ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, ?> MOB_MARROW = registerItem("mob_marrow", props -> new BoneMarrowItem(props.food(ModFoods.MARROW_FLUID).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, ?> WITHERED_MOB_MARROW = registerItem("withered_mob_marrow", props -> new BoneMarrowItem(props.food(ModFoods.CORROSIVE_FLUID).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> GENERIC_MOB_GLAND = registerItem("mob_gland", props -> new SimpleItem(props.food(ModFoods.POOR_FLESH).rarity(ModRarities.UNCOMMON)));
	public static final DeferredHolder<Item, ?> TOXIN_GLAND = registerItem("toxin_gland", props -> new SimpleItem(props.food(ModFoods.TOXIN_GLAND).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, ?> VOLATILE_GLAND = registerItem("volatile_gland", props -> new VolatileGlandItem(props.food(ModFoods.VOLATILE_GLAND).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, ?> ACIDIC_EGG = registerItem("acidic_egg", AcidicEggItem::new);

	//# Complex Components
	public static final DeferredHolder<Item, ?> FLESH_BITS = registerSimpleItem("flesh_bits");
	public static final DeferredHolder<Item, ?> BONE_FRAGMENTS = registerSimpleItem("bone_fragments");
	public static final DeferredHolder<Item, ?> TOUGH_FIBERS = registerSimpleItem("tough_fibers");
	public static final DeferredHolder<Item, ?> ELASTIC_FIBERS = registerSimpleItem("elastic_fibers");
	public static final DeferredHolder<Item, ?> MINERAL_FRAGMENT = registerSimpleItem("mineral_fragment");
	public static final DeferredHolder<Item, ?> GEM_FRAGMENTS = registerSimpleItem("gem_fragments");

	//# Basic Components
	public static final DeferredHolder<Item, ?> NUTRIENTS = registerSimpleItem("nutrients");
	public static final DeferredHolder<Item, ?> ORGANIC_MATTER = registerSimpleItem("organic_matter");
	public static final DeferredHolder<Item, ?> BIO_LUMENS = registerItem("bio_lumens", BioluminescentGooItem::new);
	public static final DeferredHolder<Item, ?> EXOTIC_DUST = registerSimpleItem("exotic_dust");
	public static final DeferredHolder<Item, ?> STONE_POWDER = registerSimpleItem("stone_powder");

	//# Specific Components
	public static final DeferredHolder<Item, ?> REGENERATIVE_FLUID = registerSimpleItem("regenerative_fluid");
	public static final DeferredHolder<Item, ?> WITHERING_OOZE = registerSimpleItem("withering_ooze");
	public static final DeferredHolder<Item, ?> HORMONE_SECRETION = registerSimpleItem("hormone_secretion");
	public static final DeferredHolder<Item, ?> TOXIN_EXTRACT = registerSimpleItem("toxin_extract");
	public static final DeferredHolder<Item, ?> ACID_EXTRACT = registerItem("acid_extract", properties -> new SimpleItem(properties.food(ModFoods.GASTRIC_JUICE)));
	public static final DeferredHolder<Item, ?> BILE = registerSimpleItem("bile");
	public static final DeferredHolder<Item, ?> VOLATILE_FLUID = registerSimpleItem("volatile_fluid");

	//# Serum
	public static final DeferredHolder<Item, ?> VIAL = registerSimpleItem("vial");
	public static final DeferredHolder<Item, ?> ORGANIC_COMPOUND = registerSimpleVialItem("organic_compound");
	public static final DeferredHolder<Item, ?> UNSTABLE_COMPOUND = registerItem("unstable_compound", UnstableCompoundItem::new);
	public static final DeferredHolder<Item, ?> GENETIC_COMPOUND = registerSimpleVialItem("genetic_compound");
	public static final DeferredHolder<Item, ?> EXOTIC_COMPOUND = registerSimpleVialItem("exotic_compound");
	public static final DeferredHolder<Item, ?> HEALING_ADDITIVE = registerSimpleVialItem("healing_additive");
	public static final DeferredHolder<Item, ?> DECAYING_ADDITIVE = registerSimpleVialItem("decaying_additive");
	public static final DeferredHolder<Item, ?> REJUVENATION_SERUM = registerSerumItem(ModSerums.REJUVENATION_SERUM);
	public static final DeferredHolder<Item, ?> AGEING_SERUM = registerSerumItem(ModSerums.AGEING_SERUM);
	public static final DeferredHolder<Item, ?> ENLARGEMENT_SERUM = registerSerumItem(ModSerums.ENLARGEMENT_SERUM);
	public static final DeferredHolder<Item, ?> SHRINKING_SERUM = registerSerumItem(ModSerums.SHRINKING_SERUM);
	public static final DeferredHolder<Item, ?> BREEDING_STIMULANT = registerSerumItem(ModSerums.BREEDING_STIMULANT);
	public static final DeferredHolder<Item, ?> ABSORPTION_BOOST = registerSerumItem(ModSerums.ABSORPTION_BOOST);
	public static final DeferredHolder<Item, ?> CLEANSING_SERUM = registerSerumItem(ModSerums.CLEANSING_SERUM);
	public static final DeferredHolder<Item, ?> INSOMNIA_CURE = registerSerumItem(ModSerums.INSOMNIA_CURE);
	public static final DeferredHolder<Item, ?> FRENZY_SERUM = registerSerumItem(ModSerums.FRENZY_SERUM);
	public static final DeferredHolder<Item, ?> POTION_SERUM = registerItem("potion_serum", props -> new PotionSerumItem(props.stacksTo(16).rarity(ModRarities.UNCOMMON)));

	//## Special
	public static final DeferredHolder<Item, ?> PRIMORDIAL_CORE = registerSimpleItem("primordial_core", ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, ?> LIVING_FLESH = registerItem("living_flesh", props -> new SimpleItem(props.food(ModFoods.LIVING_FLESH).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> ESSENCE = registerItem("essence", EssenceItem::new);

	//# Tools
	public static final DeferredHolder<Item, ?> DESPOIL_SICKLE = registerItem("despoil_sickle", props -> SwordSmithy.forge(DespoilingSwordItem::new, ModTiers.PRIMAL_FLESH, 10, 1, props.rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> ESSENCE_EXTRACTOR = registerItem("extractor", props -> new ExtractorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, ?> INJECTOR = registerItem("injector", props -> new InjectorItem(props.durability(200).rarity(ModRarities.RARE)));
	public static final DeferredHolder<Item, ?> RAVENOUS_CLAWS = registerItem("ravenous_claws", props -> new RavenousClawsItem(ModTiers.BIOFLESH, 4f, 3.5f, 250, props.rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> CAUSTIC_GUNBLADE = registerItem("caustic_gunblade", props -> new CausticGunbladeItem(200, props.stacksTo(1).rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> IMPALER = registerItem("impaler", props -> new ImpalerItem(200, props.stacksTo(1).rarity(ModRarities.ULTRA_RARE)));
	public static final DeferredHolder<Item, ?> GRENADE_CASING = registerSimpleItem("grenade_casing");
	public static final DeferredHolder<Item, ?> TOXIN_GRENADE = registerItem("toxin_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, ?> ACID_GRENADE = registerItem("acid_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, ?> DECAY_GRENADE = registerItem("decay_grenade", GrenadeItem::new);
	public static final DeferredHolder<Item, ?> INCENDIARY_GRENADE = registerItem("incendiary_grenade", GrenadeItem::new);

	//# Shield
	public static final DeferredHolder<Item, ?> THORN_SHIELD = registerItem("thorn_shield", props -> new ThornShieldItem(250, props.stacksTo(1).rarity(ModRarities.VERY_RARE)));

	//# Armor
	public static final DeferredHolder<Item, ?> ACOLYTE_ARMOR_HELMET = registerLivingArmorHelmet("acolyte_armor", ModArmorMaterials.ACOLYTE, 200, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, ?> ACOLYTE_ARMOR_CHESTPLATE = registerLivingArmorChestplate("acolyte_armor", ModArmorMaterials.ACOLYTE, 250, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, ?> ACOLYTE_ARMOR_LEGGINGS = registerLivingArmorLeggings("acolyte_armor", ModArmorMaterials.ACOLYTE, 250, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, ?> ACOLYTE_ARMOR_BOOTS = registerLivingArmorBoots("acolyte_armor", ModArmorMaterials.ACOLYTE, 200, AcolyteArmorItem::new);
	public static final DeferredHolder<Item, ?> WARRIOR_ARMOR_HELMET = registerLivingArmorHelmet("warrior_armor", ModArmorMaterials.WARRIOR, 200 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, ?> WARRIOR_ARMOR_CHESTPLATE = registerLivingArmorChestplate("warrior_armor", ModArmorMaterials.WARRIOR, 250 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, ?> WARRIOR_ARMOR_LEGGINGS = registerLivingArmorLeggings("warrior_armor", ModArmorMaterials.WARRIOR, 250 * 2, WarriorArmorItem::new);
	public static final DeferredHolder<Item, ?> WARRIOR_ARMOR_BOOTS = registerLivingArmorBoots("warrior_armor", ModArmorMaterials.WARRIOR, 200 * 2, WarriorArmorItem::new);

	//# Misc
	public static final DeferredHolder<Item, ?> NUTRIENT_PASTE = registerItem("nutrient_paste", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final DeferredHolder<Item, ?> NUTRIENT_BAR = registerItem("nutrient_bar", props -> new EffectCureItem(props.food(ModFoods.NUTRIENT_BAR)));
	public static final DeferredHolder<Item, ?> BLOOMBERRY = registerItem("bloomberry", props -> new BloomberryItem(props.food(ModFoods.NUTRIENT_PASTE)));
	public static final DeferredHolder<Item, ?> FERTILIZER = registerItem("fertilizer", props -> new FertilizerItem(props.rarity(ModRarities.UNCOMMON)));
	public static final DeferredHolder<Item, ?> CREATOR_MIX = registerSimpleItem("creator_mix");
	public static final DeferredHolder<Item, ?> ACID_BUCKET = registerItem("acid_bucket", properties -> new BucketItem(ModFluids.ACID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON)));
	public static final DeferredHolder<Item, ?> GELLING_AGENT = registerSimpleItem("gelling_agent");

	public static final DeferredHolder<Item, ?> MASCOT_BANNER_PATTERNS = registerItem("mascot_patterns", props -> new MaykerBannerPatternItem(ModBannerPatterns.TAG_MASCOT, props));

	//## Internal
	public static final DeferredHolder<Item, ?> TAB_ICON = registerSimpleItem("tab_icon");

	//## Dev
	public static final DeferredHolder<Item, ?> DEV_ARM_CANNON = registerDevItem("dev_arm_cannon", props -> new DevArmCannonItem(props.stacksTo(1).durability(ModTiers.BIOFLESH.getUses()).rarity(ModRarities.ULTRA_RARE)));
	public static final DeferredHolder<Item, ?> DEV_GUIDE_BOOK = registerDevItem("guide_book", props -> new GuideBookItem(props.stacksTo(1).rarity(ModRarities.RARE)));

	//# Block Items

	//## Machine
	public static final DeferredHolder<Item, ?> PRIMORDIAL_CRADLE = registerBlockItem(ModBlocks.PRIMORDIAL_CRADLE, block -> new BEWLBlockItem((PrimordialCradleBlock) block, createProperties().rarity(ModRarities.VERY_RARE)));
	public static final DeferredHolder<Item, ?> BIO_FORGE = registerSimpleBlockItem(ModBlocks.BIO_FORGE, ModRarities.RARE);
	public static final DeferredHolder<Item, ?> DECOMPOSER = registerSimpleBlockItem(ModBlocks.DECOMPOSER, ModRarities.RARE);
	public static final DeferredHolder<Item, ?> BIO_LAB = registerSimpleBlockItem(ModBlocks.BIO_LAB, ModRarities.RARE);
	public static final DeferredHolder<Item, ?> DIGESTER = registerSimpleBlockItem(ModBlocks.DIGESTER, ModRarities.RARE);

	//## Storage, Automation & Utility
	public static final DeferredHolder<Item, ?> TONGUE = registerSimpleBlockItem(ModBlocks.TONGUE, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, ?> MAW_HOPPER = registerSimpleBlockItem(ModBlocks.MAW_HOPPER, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, ?> FLESHKIN_CHEST = registerBlockItem(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockItem::new, ModRarities.UNCOMMON);
	public static final DeferredHolder<Item, ?> STORAGE_SAC = registerBlockItem(ModBlocks.STORAGE_SAC, block -> new StorageSacBlockItem(block, createProperties().stacksTo(1)));
	public static final DeferredHolder<Item, ?> VIAL_HOLDER = registerSimpleBlockItem(ModBlocks.VIAL_HOLDER);
	public static final DeferredHolder<Item, ?> JUMP_PAD = registerSimpleBlockItem(ModBlocks.JUMP_PAD);
	public static final DeferredHolder<Item, ?> CHRYSALIS = registerBlockItem(ModBlocks.CHRYSALIS, ChrysalisBlockItem::new, ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, ?> MODULAR_LARYNX = registerSimpleBlockItem(ModBlocks.MODULAR_LARYNX);
	public static final DeferredHolder<Item, ?> FLESH_SPIKE = registerSimpleBlockItem(ModBlocks.FLESH_SPIKE);
	public static final DeferredHolder<Item, ?> FLESHKIN_PRESSURE_PLATE = registerSimpleBlockItem(ModBlocks.FLESHKIN_PRESSURE_PLATE);
	public static final DeferredHolder<Item, ?> WATER_GEL_BLOCK = registerSimpleBlockItem(ModBlocks.WATER_GEL_BLOCK);
	public static final DeferredHolder<Item, ?> ACID_SPLATTER = registerSimpleBlockItem(ModBlocks.ACID_SPLATTER);
	public static final DeferredHolder<Item, ?> VOLATILE_SPLATTER = registerSimpleBlockItem(ModBlocks.VOLATILE_SPLATTER);

	//public static final DeferredHolder<Item, ?> NEURAL_INTERCEPTOR = registerSimpleBlockItem(ModBlocks.NEURAL_INTERCEPTOR, ModRarities.VERY_RARE);
	//	public static final DeferredHolder<Item, ?> FLESHKIN_DOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_DOOR);
	//	public static final DeferredHolder<Item, ?> FLESHKIN_TRAPDOOR = registerSimpleBlockItem(ModBlocks.FLESHKIN_TRAPDOOR);

	public static final DeferredHolder<Item, ?> FLESH_IRIS_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_IRIS_DOOR);
	public static final DeferredHolder<Item, ?> FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FLESH_DOOR);
	public static final DeferredHolder<Item, ?> FULL_FLESH_DOOR = registerSimpleBlockItem(ModBlocks.FULL_FLESH_DOOR);
	public static final DeferredHolder<Item, ?> TENDON_CHAIN = registerBlockItemFactory(ModBlocks.TENDON_CHAIN, FleshChainBlockItem::new);
	public static final DeferredHolder<Item, ?> FLESH_LADDER = registerSimpleBlockItem(ModBlocks.FLESH_LADDER);
	public static final DeferredHolder<Item, ?> FLESH_FENCE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE);
	public static final DeferredHolder<Item, ?> FLESH_FENCE_GATE = registerSimpleBlockItem(ModBlocks.FLESH_FENCE_GATE);
	public static final DeferredHolder<Item, ?> YELLOW_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.YELLOW_BIO_LANTERN);
	public static final DeferredHolder<Item, ?> BLUE_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.BLUE_BIO_LANTERN);
	public static final DeferredHolder<Item, ?> PRIMORDIAL_BIO_LANTERN = registerSimpleBlockItem(ModBlocks.PRIMORDIAL_BIO_LANTERN);

	//## Membranes
	public static final DeferredHolder<Item, ?> BIOMETRIC_MEMBRANE = registerBlockItem(ModBlocks.BIOMETRIC_MEMBRANE, BiometricMembraneBlockItem::new, ModRarities.VERY_RARE);
	public static final DeferredHolder<Item, ?> ONEWAY_MEMBRANE = registerSimpleBlockItem(ModBlocks.ONEWAY_MEMBRANE);
	public static final DeferredHolder<Item, ?> IMPERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, ?> IMPERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.IMPERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, ?> BABY_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, ?> BABY_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, ?> ADULT_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, ?> ADULT_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, ?> PRIMAL_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, ?> PRIMAL_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE);
	public static final DeferredHolder<Item, ?> UNDEAD_PERMEABLE_MEMBRANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE);
	public static final DeferredHolder<Item, ?> UNDEAD_PERMEABLE_MEMBRANE_PANE = registerSimpleBlockItem(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE);

	//## Building Blocks
	public static final DeferredHolder<Item, ?> FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FLESH);
	public static final DeferredHolder<Item, ?> FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FLESH_SLAB);
	public static final DeferredHolder<Item, ?> FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> FLESH_WALL = registerSimpleBlockItem(ModBlocks.FLESH_WALL);
	public static final DeferredHolder<Item, ?> PACKED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PACKED_FLESH);
	public static final DeferredHolder<Item, ?> PACKED_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> PACKED_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> PACKED_FLESH_WALL = registerSimpleBlockItem(ModBlocks.PACKED_FLESH_WALL);
	public static final DeferredHolder<Item, ?> FIBROUS_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH);
	public static final DeferredHolder<Item, ?> FIBROUS_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> FIBROUS_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> FIBROUS_FLESH_WALL = registerSimpleBlockItem(ModBlocks.FIBROUS_FLESH_WALL);
	public static final DeferredHolder<Item, ?> FLESH_PILLAR = registerSimpleBlockItem(ModBlocks.FLESH_PILLAR);
	public static final DeferredHolder<Item, ?> CHISELED_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.CHISELED_FLESH);
	public static final DeferredHolder<Item, ?> ORNATE_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.ORNATE_FLESH);
	public static final DeferredHolder<Item, ?> ORNATE_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.ORNATE_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> TUBULAR_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.TUBULAR_FLESH_BLOCK);

	public static final DeferredHolder<Item, ?> PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH);
	public static final DeferredHolder<Item, ?> PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, ?> SMOOTH_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH);
	public static final DeferredHolder<Item, ?> SMOOTH_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> SMOOTH_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> SMOOTH_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, ?> FIBROUS_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH);
	public static final DeferredHolder<Item, ?> FIBROUS_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> FIBROUS_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> FIBROUS_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.FIBROUS_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, ?> POROUS_PRIMAL_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH);
	public static final DeferredHolder<Item, ?> POROUS_PRIMAL_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> POROUS_PRIMAL_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> POROUS_PRIMAL_FLESH_WALL = registerSimpleBlockItem(ModBlocks.POROUS_PRIMAL_FLESH_WALL);
	public static final DeferredHolder<Item, ?> MALIGNANT_FLESH_BLOCK = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH);
	public static final DeferredHolder<Item, ?> MALIGNANT_FLESH_SLAB = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_SLAB);
	public static final DeferredHolder<Item, ?> MALIGNANT_FLESH_STAIRS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_STAIRS);
	public static final DeferredHolder<Item, ?> MALIGNANT_FLESH_WALL = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_WALL);
	public static final DeferredHolder<Item, ?> MALIGNANT_FLESH_VEINS = registerSimpleBlockItem(ModBlocks.MALIGNANT_FLESH_VEINS);
	public static final DeferredHolder<Item, ?> PRIMAL_BLOOM = registerSimpleBlockItem(ModBlocks.PRIMAL_BLOOM);
	public static final DeferredHolder<Item, ?> BLOOMLIGHT = registerSimpleBlockItem(ModBlocks.BLOOMLIGHT);
	public static final DeferredHolder<Item, ?> PRIMAL_ORIFICE = registerSimpleBlockItem(ModBlocks.PRIMAL_ORIFICE);
	public static final DeferredHolder<Item, ?> PRIMAL_BONE = registerSimpleBlockItem(ModBlocks.PRIMAL_BONE);

	//# Spawn Eggs
	public static final DeferredHolder<Item, ?> HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.HUNGRY_FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final DeferredHolder<Item, ?> FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_BLOB, 0xe9967a, 0xf6d2c6);
	public static final DeferredHolder<Item, ?> LEGACY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.LEGACY_FLESH_BLOB, 0xeec5da, 0xffc0cb);
	public static final DeferredHolder<Item, ?> PRIMORDIAL_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_FLESH_BLOB, 0xde6074, 0xc343fe);
	public static final DeferredHolder<Item, ?> PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB, 0x752144, 0x752144);
	public static final DeferredHolder<Item, ?> FLESH_COW_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_COW, 0xe9967a, 0x9d7572);
	public static final DeferredHolder<Item, ?> FLESH_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_SHEEP, 0xe9967a, 0xf9bbd4);
	public static final DeferredHolder<Item, ?> FLESH_PIG_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_PIG, 0xe9967a, 0xed7684);
	public static final DeferredHolder<Item, ?> FLESH_CHICKEN_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.FLESH_CHICKEN, 0xe9967a, 0xce4e65);
	public static final DeferredHolder<Item, ?> CHROMA_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.CHROMA_SHEEP, 0xe9967a, 0xf9bbd4);
	public static final DeferredHolder<Item, ?> THICK_FUR_SHEEP_SPAWN_EGG = registerSpawnEgg(ModEntityTypes.THICK_FUR_SHEEP, 0xe9967a, 0xf9bbd4);

	private ModItems() {}

	public static Stream<Item> stream() {
		return ModItems.ITEMS.getEntries().stream().map(DeferredHolder::get);
	}

	public static <T extends Item> Stream<T> findItems(Class<T> clazz) {
		return ModItems.ITEMS.getEntries().stream()
				.map(DeferredHolder::get)
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	public static <T extends Item> Stream<DeferredHolder<Item, ?>> findEntries(Class<T> clazz) {
		//noinspection unchecked
		return ModItems.ITEMS.getEntries().stream()
				.filter(registryObject -> clazz.isInstance(registryObject.get()))
				.map(registryObject -> (DeferredHolder<Item, ?>) registryObject);
	}

	private static <T extends Item> DeferredHolder<Item, ?> registerItem(String name, Function<Item.Properties, T> factory) {
		return ITEMS.register(name, () -> factory.apply(createProperties()));
	}

	private static <T extends Item> DeferredHolder<Item, ?> registerDevItem(String name, Function<Item.Properties, T> factory) {
		return ITEMS.register(name, () -> factory.apply(createProperties()));
	}

	private static <T extends Block> DeferredHolder<Item, ?> registerSimpleBlockItem(DeferredHolder<Block, ?> blockHolder) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), createProperties()));
	}

	private static <T extends Block> DeferredHolder<Item, ?> registerSimpleBlockItem(DeferredHolder<Block, ?> blockHolder, Rarity rarity) {
		return registerSimpleBlockItem(blockHolder, () -> createProperties().rarity(rarity));
	}

	private static <T extends Block> DeferredHolder<Item, ?> registerSimpleBlockItem(DeferredHolder<Block, ?> blockHolder, Supplier<Item.Properties> properties) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> new SimpleBlockItem(blockHolder.get(), properties.get()));
	}

	private static <I extends BlockItem> DeferredHolder<Item, ?> registerBlockItem(DeferredHolder<Block, ?> blockHolder, Function<Block, I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.apply(blockHolder.get()));
	}

	private static <I extends BlockItem> DeferredHolder<Item, ?> registerBlockItemFactory(DeferredHolder<Block, ?> blockHolder, IBlockItemFactory<I> factory) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties()));
	}

	private static <I extends BlockItem> DeferredHolder<Item, ?> registerBlockItem(DeferredHolder<Block, ?> blockHolder, IBlockItemFactory<I> factory, Rarity rarity) {
		return ITEMS.register(blockHolder.getId().getPath(), () -> factory.create(blockHolder.get(), createProperties().rarity(rarity)));
	}

	private static <M extends ArmorMaterial, I extends ArmorItem> DeferredHolder<Item, ?> registerArmorHelmet(String name, M material, ArmorFactory<M, ArmorItem.Type, I> factory) {
		return registerArmor(name + "_helmet", material, ArmorItem.Type.HELMET, factory);
	}

	private static <M extends ArmorMaterial, I extends ArmorItem> DeferredHolder<Item, ?> registerArmorChestplate(String name, M material, ArmorFactory<M, ArmorItem.Type, I> factory) {
		return registerArmor(name + "_chestplate", material, ArmorItem.Type.CHESTPLATE, factory);
	}

	private static <M extends ArmorMaterial, I extends ArmorItem> DeferredHolder<Item, ?> registerArmorLeggings(String name, M material, ArmorFactory<M, ArmorItem.Type, I> factory) {
		return registerArmor(name + "_leggings", material, ArmorItem.Type.LEGGINGS, factory);
	}

	private static <M extends ArmorMaterial, I extends ArmorItem> DeferredHolder<Item, ?> registerArmorBoots(String name, M material, ArmorFactory<M, ArmorItem.Type, I> factory) {
		return registerArmor(name + "_boots", material, ArmorItem.Type.BOOTS, factory);
	}

	private static <M extends ArmorMaterial, T extends ArmorItem.Type, I extends ArmorItem> DeferredHolder<Item, ?> registerArmor(String name, M material, T type, ArmorFactory<M, T, I> factory) {
		return ITEMS.register(name, () -> factory.create(material, type, createProperties().stacksTo(1)));
	}

	private static <I extends ArmorItem> DeferredHolder<Item, ?> registerLivingArmorHelmet(String name, DeferredHolder<ArmorMaterial, ArmorMaterial> material, int maxNutrients, LivingArmorFactory<ArmorItem.Type, I> factory) {
		return registerLivingArmor(name + "_helmet", material, ArmorItem.Type.HELMET, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, ?> registerLivingArmorChestplate(String name, DeferredHolder<ArmorMaterial, ArmorMaterial> material, int maxNutrients, LivingArmorFactory<ArmorItem.Type, I> factory) {
		return registerLivingArmor(name + "_chestplate", material, ArmorItem.Type.CHESTPLATE, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, ?> registerLivingArmorLeggings(String name, DeferredHolder<ArmorMaterial, ArmorMaterial> material, int maxNutrients, LivingArmorFactory<ArmorItem.Type, I> factory) {
		return registerLivingArmor(name + "_leggings", material, ArmorItem.Type.LEGGINGS, maxNutrients, factory);
	}

	private static <I extends ArmorItem> DeferredHolder<Item, ?> registerLivingArmorBoots(String name, DeferredHolder<ArmorMaterial, ArmorMaterial> material, int maxNutrients, LivingArmorFactory<ArmorItem.Type, I> factory) {
		return registerLivingArmor(name + "_boots", material, ArmorItem.Type.BOOTS, maxNutrients, factory);
	}

	private static <T extends ArmorItem.Type, I extends ArmorItem> DeferredHolder<Item, ?> registerLivingArmor(String name, DeferredHolder<ArmorMaterial, ArmorMaterial> material, T type, int maxNutrients, LivingArmorFactory<T, I> factory) {
		return ITEMS.register(name, () -> factory.create(material, type, maxNutrients, createProperties().stacksTo(1).rarity(ModRarities.VERY_RARE)));
	}

	private static <T extends EntityType<? extends Mob>> DeferredHolder<Item, ?> registerSpawnEgg(DeferredHolder<EntityType<?>, T> mobHolder, int primaryColor, int accentColor) {
		return ITEMS.register(mobHolder.getId().getPath() + "_spawn_egg", () -> new DeferredSpawnEggItem(mobHolder, primaryColor, accentColor, createProperties()));
	}

	private static <T extends Serum> DeferredHolder<Item, ?> registerSerumItem(DeferredHolder<Serum, T> registryObject) {
		return ITEMS.register(registryObject.getId().getPath(), () -> new SerumItem(createProperties().stacksTo(16).rarity(ModRarities.UNCOMMON), registryObject));
	}

	private static DeferredHolder<Item, ?> registerSimpleVialItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
	}

	private static DeferredHolder<Item, ?> registerSimpleItem(String name) {
		return ITEMS.register(name, () -> new SimpleItem(createProperties()));
	}

	private static DeferredHolder<Item, ?> registerSimpleItem(String name, Rarity rarity) {
		return registerSimpleItem(name, () -> createProperties().rarity(rarity));
	}

	private static Item.Properties createProperties() {
		return new Item.Properties().rarity(ModRarities.COMMON);
	}

	private static DeferredHolder<Item, ?> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
		return ITEMS.register(name, () -> new SimpleItem(properties.get()));
	}

	private interface SwordSmithy<T extends SwordItem> {
		AttributeSupplier PLAYER_ATTRIBUTES = Player.createAttributes().build();

		static <T extends SwordItem> T forge(SwordSmithy<T> smithy, Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
			int attackDamageModifier = Mth.floor(attackDamage - (PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_DAMAGE) + tier.getAttackDamageBonus()));
			float attackSpeedModifier = attackSpeed - (float) PLAYER_ATTRIBUTES.getValue(Attributes.ATTACK_SPEED);
			return smithy.forge(tier, attackDamageModifier, attackSpeedModifier, properties);
		}

		T forge(Tier tier, int attackDamageModifier, float attackSpeedModifier, Item.Properties properties);
	}

	interface IBlockItemFactory<I extends BlockItem> {
		I create(Block block, Item.Properties properties);
	}

	interface ArmorFactory<M extends ArmorMaterial, T extends ArmorItem.Type, I extends ArmorItem> {
		I create(M material, T type, Item.Properties properties);
	}

	interface LivingArmorFactory<T extends ArmorItem.Type, I extends ArmorItem> {
		I create(Holder<ArmorMaterial> material, T type, int maxNutrients, Item.Properties properties);
	}

}
