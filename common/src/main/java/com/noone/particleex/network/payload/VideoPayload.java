package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readDouble;
import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record VideoPayload(double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, int flip, double dpb, Vec3 speed, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
  public static final Type<VideoPayload> TYPE = new Type<>(NetworkIdentifiers.VIDEO_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, VideoPayload> CODEC = StreamCodec.of(
          (value, buf) -> {
              buf.writeDouble(value.x);
              buf.writeDouble(value.y);
              buf.writeDouble(value.z);
              buf.writeString(value.path);
              buf.writeDouble(value.scaling);
              buf.writeInt(value.xRotate);
              buf.writeInt(value.yRotate);
              buf.writeInt(value.zRotate);
              buf.writeInt(value.flip);
              buf.writeDouble(value.dpb);
              if (value.speed == null) {
                  buf.writeDouble(0.0D);
                  buf.writeDouble(0.0D);
                  buf.writeDouble(0.0D);
              } else {
                  buf.writeDouble(value.speed.x);
                  buf.writeDouble(value.speed.y);
                  buf.writeDouble(value.speed.z);
              }
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
              String path = buf.readUtf();
              double scaling = buf.readDouble();
              int xRotate = buf.readInt();
              int yRotate = buf.readInt();
              int zRotate = buf.readInt();
              int flip = buf.readInt();
              double dpb = buf.readDouble();
              double vx = buf.readDouble();
              double vy = buf.readDouble();
              double vz = buf.readDouble();
              int age = buf.readInt();
              boolean hasSpeedExpression = buf.readBoolean();
              String speedExpression = readString(buf, hasSpeedExpression);
              double speedStep = readDouble(buf, hasSpeedExpression, 1.0D);
              String group = readString(buf, buf.readBoolean());
              ParticleOptions effect = ParticleTypes.STREAM_CODEC.decode(buf);
              return new VideoPayload(x,y,z,path,scaling,xRotate,yRotate,zRotate,flip,dpb,new Vec3(vx,vy,vz),age,speedExpression,speedStep,group,effect);
          }
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
