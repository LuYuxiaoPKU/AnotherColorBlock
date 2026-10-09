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

public record VideoMatrixPayload(double x, double y, double z, String path, double scaling, double[][] matrix, double dpb, Vec3d speed, int age, String speedExpression, double speedStep, String group, ParticleEffect effect) implements CustomPacketPayload {
   public static final Type<VideoMatrixPayload> TYPE = new Type<>(NetworkIdentifiers.VIDEO_MATRIX_PACKET_ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, VideoMatrixPayload> CODEC = StreamCodec.of(
          (value, buf) -> {
              buf.writeDouble(value.x);
              buf.writeDouble(value.y);
              buf.writeDouble(value.z);
              buf.writeString(value.path);
              buf.writeDouble(value.scaling);
              buf.writeInt(value.matrix.length);
              buf.writeInt(value.matrix.length > 0 ? value.matrix[0].length : 0);

              for(int row = 0; row < value.matrix.length; ++row) {
                  for(int col = 0; col < value.matrix[0].length; ++col) {
                      buf.writeDouble(value.matrix[row][col]);
                  }
              }
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
              ParticleTypes.STREAM_CODEC.encode(buf,value.effect);
          },
          buf -> {
              double x = buf.readDouble();
              double y = buf.readDouble();
              double z = buf.readDouble();
              String path = buf.readString();
              double scaling = buf.readDouble();
              int rows = buf.readInt();
              int cols = buf.readInt();
              double[][] matrix = new double[rows][cols];
              for(int row = 0; row < rows; ++row) {
                  for(int col = 0; col < cols; ++col) {
                      matrix[row][col] = buf.readDouble();
                  }
              }
              double dpb = buf.readDouble();
              double vx = buf.readDouble();
              double vy = buf.readDouble();
              double vz = buf.readDouble();
              int age = buf.readInt();
              boolean hasSpeedExpression = buf.readBoolean();
              String speedExpression = readString(buf, hasSpeedExpression);
              double speedStep = readDouble(buf, hasSpeedExpression, 1.0D);
              String group = readString(buf, buf.readBoolean());
              ParticleEffect effect = ParticleTypes.STREAM_CODEC.decode(buf);
              return new VideoMatrixPayload(x,y,z,path,scaling,matrix,dpb,new Vec3d(vx,vy,vz),age,speedExpression,speedStep,group,effect);
          }
   );
   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
