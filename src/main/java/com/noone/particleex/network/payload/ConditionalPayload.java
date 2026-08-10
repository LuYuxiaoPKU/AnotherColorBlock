package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.noone.particleex.network.ClientNetworkHandler.readDouble;
import static com.noone.particleex.network.ClientNetworkHandler.readString;

public record ConditionalPayload(double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double dx, double dy, double dz,String expression,double step, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
  public static final Type<ConditionalPayload> ID = new Type<>(NetworkIdentifiers.CONDITIONAL_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalPayload> CODEC = StreamCodec.ofMember(
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
              buf.writeBoolean(!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null"));
              if (!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null")) {
                  buf.writeUtf(value.expression);
                  buf.writeDouble(value.step);
              }

              buf.writeInt(value.age);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.speedExpression) && !value.speedExpression.equals("null"));
              if (!Strings.isNullOrEmpty(value.speedExpression) && !value.speedExpression.equals("null")) {
                  buf.writeUtf(value.speedExpression);
                  buf.writeDouble(value.speedStep);
              }

              buf.writeBoolean(!Strings.isNullOrEmpty(value.group) && !value.group.equals("null"));
              if (!Strings.isNullOrEmpty(value.group) && !value.group.equals("null")) {
                  buf.writeUtf(value.group);
              }
              ParticleTypes.STREAM_CODEC.encode(buf,value.effect);
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
              boolean hasExpression = buf.readBoolean();
              String expression = readString(buf, hasExpression);
              double step = readDouble(buf, hasExpression, 0.1D);
              int age = buf.readInt();
              boolean hasSpeedExpression = buf.readBoolean();
              String speedExpression = readString(buf, hasSpeedExpression);
              double speedStep = readDouble(buf, hasSpeedExpression, 1.0D);
              String group = readString(buf, buf.readBoolean());
              ParticleOptions effect = ParticleTypes.STREAM_CODEC.decode(buf);
             return new ConditionalPayload(x,y,z,red,green,blue,alpha,vx,vy,vz,dx,dy,dz,expression,step,age,speedExpression,speedStep,group,effect);
          }
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
