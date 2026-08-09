package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import static com.noone.particleex.network.ClientNetworkHandler.readDouble;
import static com.noone.particleex.network.ClientNetworkHandler.readString;

public record GroupRemovePayload(String group, String expression, Vec3d pos) implements CustomPayload {
  public static final Id<GroupRemovePayload> ID = new Id<>(NetworkIdentifiers.GROUP_REMOVE_PACKET_ID);
  public static final PacketCodec<RegistryByteBuf, GroupRemovePayload> CODEC = PacketCodec.of(
          (value, buf) -> {
              buf.writeString(value.group);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null"));
              if (!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null")) {
                  buf.writeString(value.expression);
              }

              buf.writeBoolean(value.pos != null);
              if (value.pos != null) {
                  buf.writeDouble(value.pos.x);
                  buf.writeDouble(value.pos.y);
                  buf.writeDouble(value.pos.z);
              }
          },
          buf -> {
              String group = buf.readString();
              String expression = readString(buf, buf.readBoolean());
              boolean hasPos = buf.readBoolean();
              Vec3d pos = null;
              if(hasPos){
                  Double x = buf.readDouble();
                  Double y = buf.readDouble();
                  Double z = buf.readDouble();
                  pos = new Vec3d(x, y, z);
              }
              return new GroupRemovePayload(group,expression,pos);
          }
  );
  @Override
   public Id<? extends CustomPayload> getId() {
      return ID;
   }
}
