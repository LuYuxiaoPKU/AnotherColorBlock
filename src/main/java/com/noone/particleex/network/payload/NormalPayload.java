package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public record NormalPayload(double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double dx, double dy, double dz, int count, int age, String speedExpression, double speedStep, String group, ParticleEffect effect) implements CustomPayload {
  public static final Id<NormalPayload> ID = new Id<>(NetworkIdentifiers.NORMAL_PACKET_ID);
  public static final PacketCodec<RegistryByteBuf, NormalPayload> CODEC = PacketCodec.of(
          (value, buf) -> {
              buf.writeDouble(value.x);
              buf.writeDouble(value.y);
              buf.writeDouble(value.z);
              buf.writeFloat(value.red);
              buf.writeFloat(value.green);
              buf.writeFloat(value.blue);
              buf.writeFloat(value.alpha);
              buf.writeDouble(value.vx);
              buf.writeDouble(value.vy);
              buf.writeDouble(value.vz);
              buf.writeDouble(value.dx);
              buf.writeDouble(value.dy);
              buf.writeDouble(value.dz);
              buf.writeInt(value.count);
              buf.writeInt(value.age);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.speedExpression) && !value.speedExpression.equals("null"));
              if (!Strings.isNullOrEmpty(value.speedExpression) && !value.speedExpression.equals("null")) {
                  buf.writeString(value.speedExpression);
                  buf.writeDouble(value.speedStep);
              }
              buf.writeBoolean(!Strings.isNullOrEmpty(value.group) && !value.group.equals("null"));
              if (!Strings.isNullOrEmpty(value.group) && !value.group.equals("null")) {
                  buf.writeString(value.group);
              }
              ParticleTypes.PACKET_CODEC.encode(buf, value.effect);
          },
          buf -> {
             double x = buf.readDouble();
             double y = buf.readDouble();
             double z = buf.readDouble();
             float red = buf.readFloat();
             float green = buf.readFloat();
             float blue = buf.readFloat();
             float alpha = buf.readFloat();
             double vx = buf.readDouble();
             double vy = buf.readDouble();
             double vz = buf.readDouble();
             double dx = buf.readDouble();
             double dy = buf.readDouble();
             double dz = buf.readDouble();
             int count = buf.readInt();
             int age = buf.readInt();
             boolean hasSpeedExpression = buf.readBoolean();
             String speedExpression = ClientNetworkHandler.readString(buf, hasSpeedExpression);
             double speedStep = ClientNetworkHandler.readDouble(buf, hasSpeedExpression, 1.0D);
             String group = ClientNetworkHandler.readString(buf, buf.readBoolean());
             ParticleEffect effect = ParticleTypes.PACKET_CODEC.decode(buf);
             return new NormalPayload(x,y,z,red,green,blue,alpha,vx,vy,vz,dx,dy,dz,count,age,speedExpression,speedStep,group,effect);
          }
  );
  @Override
   public Id<? extends CustomPayload> getId() {
      return ID;
   }
}
