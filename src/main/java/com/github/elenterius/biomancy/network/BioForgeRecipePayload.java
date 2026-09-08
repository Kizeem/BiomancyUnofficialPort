package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BioForgeRecipePayload(int containerId, ResourceLocation recipeId) implements CustomPacketPayload {

	public static final Type<BioForgeRecipePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BiomancyMod.MOD_ID, "bio_forge_recipe"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BioForgeRecipePayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, BioForgeRecipePayload::containerId,
			ResourceLocation.STREAM_CODEC, BioForgeRecipePayload::recipeId,
			BioForgeRecipePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
