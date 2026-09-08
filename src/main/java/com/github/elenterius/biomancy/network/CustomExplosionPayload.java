package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.mixin.accessor.ExplosionAccessor;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//client bound payload
public record CustomExplosionPayload(ExplosionUtil.ExplosionType explosionType, @Nullable Integer sourceId, double x, double y, double z, float radius, List<BlockPos> toBlow, float knockbackX, float knockbackY, float knockbackZ) implements CustomPacketPayload {

	public static final Type<CustomExplosionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BiomancyMod.MOD_ID, "custom_explosion"));

	public static final StreamCodec<ByteBuf, CustomExplosionPayload> STREAM_CODEC = StreamCodec.ofMember(CustomExplosionPayload::write, CustomExplosionPayload::read);

	private static void write(CustomExplosionPayload payload, ByteBuf buf) {
		if (buf instanceof net.minecraft.network.FriendlyByteBuf friendly) {
			friendly.writeByte(payload.explosionType.id());

			if (payload.sourceId != null) {
				friendly.writeBoolean(true);
				friendly.writeVarInt(payload.sourceId);
			}
			else {
				friendly.writeBoolean(false);
			}

			friendly.writeDouble(payload.x);
			friendly.writeDouble(payload.y);
			friendly.writeDouble(payload.z);
			friendly.writeFloat(payload.radius);

			int xi = Mth.floor(payload.x);
			int yi = Mth.floor(payload.y);
			int zi = Mth.floor(payload.z);
			friendly.writeCollection(payload.toBlow, (buffer, pos) -> {
				buffer.writeByte(pos.getX() - xi);
				buffer.writeByte(pos.getY() - yi);
				buffer.writeByte(pos.getZ() - zi);
			});

			friendly.writeFloat(payload.knockbackX);
			friendly.writeFloat(payload.knockbackY);
			friendly.writeFloat(payload.knockbackZ);
		}
	}

	private static CustomExplosionPayload read(ByteBuf buf) {
		if (buf instanceof net.minecraft.network.FriendlyByteBuf friendly) {
			ExplosionUtil.ExplosionType type = ExplosionUtil.ExplosionType.fromId(friendly.readByte());

			Integer sourceId = null;
			if (friendly.readBoolean()) {
				sourceId = friendly.readVarInt();
			}

			double x = friendly.readDouble();
			double y = friendly.readDouble();
			double z = friendly.readDouble();
			float radius = friendly.readFloat();

			int xi = Mth.floor(x);
			int yi = Mth.floor(y);
			int zi = Mth.floor(z);
			List<BlockPos> toBlow = friendly.readList(buffer -> new BlockPos(
					buffer.readByte() + xi,
					buffer.readByte() + yi,
					buffer.readByte() + zi
			));

			float knockbackX = friendly.readFloat();
			float knockbackY = friendly.readFloat();
			float knockbackZ = friendly.readFloat();

			return new CustomExplosionPayload(type, sourceId, x, y, z, radius, toBlow, knockbackX, knockbackY, knockbackZ);
		}
		throw new IllegalStateException("Expected FriendlyByteBuf");
	}

	public static CustomExplosionPayload of(ExplosionUtil.ExplosionType type, Explosion explosion, ServerPlayer serverPlayer) {
		Entity source = explosion.getDirectSourceEntity();
		Integer sourceId = source != null ? source.getId() : null;

		Vec3 position = explosion.center();

		Vec3 knockback = explosion.getHitPlayers().get(serverPlayer);
		float kx = knockback != null ? (float) knockback.x : 0f;
		float ky = knockback != null ? (float) knockback.y : 0f;
		float kz = knockback != null ? (float) knockback.z : 0f;

		return new CustomExplosionPayload(type, sourceId, position.x, position.y, position.z,
				((ExplosionAccessor) explosion).getRadius(), new ArrayList<>(explosion.getToBlow()), kx, ky, kz);
	}

	public static void handleClient(CustomExplosionPayload packet) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		LocalPlayer player = minecraft.player;
		if (level == null || player == null) return;

		Entity source = null;
		if (packet.sourceId != null) {
			source = level.getEntity(packet.sourceId);
		}

		Explosion explosion = packet.explosionType.clientFactory.create(level, source, packet.x, packet.y, packet.z, packet.radius, packet.toBlow);
		explosion.finalizeExplosion(true);
		player.setDeltaMovement(player.getDeltaMovement().add(packet.knockbackX, packet.knockbackY, packet.knockbackZ));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
