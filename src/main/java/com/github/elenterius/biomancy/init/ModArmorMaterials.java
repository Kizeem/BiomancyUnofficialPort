package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;

public final class ModArmorMaterials {

	public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, BiomancyMod.MOD_ID);

	private static final int[] BASE_DURABILITY = new int[]{11, 16, 15, 13};

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ACOLYTE = ARMOR_MATERIALS.register("acolyte", () -> new ArmorMaterial(
			Map.of(
					ArmorItem.Type.BOOTS, 2,
					ArmorItem.Type.LEGGINGS, 5,
					ArmorItem.Type.CHESTPLATE, 6,
					ArmorItem.Type.HELMET, 2
			),
			0,
			ModSoundEvents.ARMOR_EQUIP_BIO_ALCHEMIST,
			() -> Ingredient.EMPTY,
			List.of(new ArmorMaterial.Layer(BiomancyMod.rl("acolyte"))),
			0.25f,
			0.1f
	));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> WARRIOR = ARMOR_MATERIALS.register("warrior", () -> new ArmorMaterial(
			Map.of(
					ArmorItem.Type.BOOTS, 3,
					ArmorItem.Type.LEGGINGS, 6,
					ArmorItem.Type.CHESTPLATE, 8,
					ArmorItem.Type.HELMET, 3
			),
			0,
			ModSoundEvents.ARMOR_EQUIP_WARRIOR,
			() -> Ingredient.EMPTY,
			List.of(new ArmorMaterial.Layer(BiomancyMod.rl("warrior"))),
			0.5f,
			0
	));


	private ModArmorMaterials() {}

}
