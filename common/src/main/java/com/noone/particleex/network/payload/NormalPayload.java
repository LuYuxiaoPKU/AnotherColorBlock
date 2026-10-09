package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.PayloadCodecUtil;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public record NormalPayload(double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double dx, double dy, double dz, int count, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
  public static final Type<NormalPayload> TYPE = new Type<>(NetworkIdentifiers.NORMAL_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, NormalPayload> CODEC = StreamCodec.of(
          (buf, value) -> {
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
              ParticleTypes.STREAM_CODEC.encode(buf, value.effect);
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
             String speedExpression = PayloadCodecUtil.readString(buf, hasSpeedExpression);
             double speedStep = PayloadCodecUtil.readDouble(buf, hasSpeedExpression, 1.0D);
             String group = PayloadCodecUtil.readString(buf, buf.readBoolean());
             ParticleOptions effect = ParticleTypes.STREAM_CODEC.decode(buf);
             return new NormalPayload(x,y,z,red,green,blue,alpha,vx,vy,vz,dx,dy,dz,count,age,speedExpression,speedStep,group,effect);
          }
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
