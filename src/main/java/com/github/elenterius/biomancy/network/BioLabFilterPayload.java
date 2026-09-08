package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record BioLabFilterPayload(int containerId, List<ItemStack> filters) implements CustomPacketPayload {

	public static final Type<BioLabFilterPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BiomancyMod.MOD_ID, "bio_lab_filter"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BioLabFilterPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, BioLabFilterPayload::containerId,
			ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), BioLabFilterPayload::filters,
			BioLabFilterPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
