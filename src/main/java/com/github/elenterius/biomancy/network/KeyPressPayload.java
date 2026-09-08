package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record KeyPressPayload(int slotIndex, byte flag) implements CustomPacketPayload {

	public static final Type<KeyPressPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BiomancyMod.MOD_ID, "key_press"));

	public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, KeyPressPayload::slotIndex,
			ByteBufCodecs.BYTE, KeyPressPayload::flag,
			KeyPressPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
