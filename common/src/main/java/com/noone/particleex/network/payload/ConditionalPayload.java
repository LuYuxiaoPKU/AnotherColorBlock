package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import static com.noone.particleex.network.PayloadCodecUtil.readDouble;
import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record ConditionalPayload(double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double dx, double dy, double dz,String expression,double step, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
  @Override
  public void write(FriendlyByteBuf buf) {
              buf.writeDouble(this.x);
              buf.writeDouble(this.y);
              buf.writeDouble(this.z);
              buf.writeFloat(this.red);
              buf.writeFloat(this.green);
              buf.writeFloat(this.blue);
              buf.writeFloat(this.alpha);
              buf.writeDouble(this.vx);
              buf.writeDouble(this.vy);
              buf.writeDouble(this.vz);
              buf.writeDouble(this.dx);
              buf.writeDouble(this.dy);
              buf.writeDouble(this.dz);
              buf.writeBoolean(!Strings.isNullOrEmpty(this.expression) && !this.expression.equals("null"));
              if (!Strings.isNullOrEmpty(this.expression) && !this.expression.equals("null")) {
                  buf.writeUtf(this.expression);
                  buf.writeDouble(this.step);
              }

              buf.writeInt(this.age);
              buf.writeBoolean(!Strings.isNullOrEmpty(this.speedExpression) && !this.speedExpression.equals("null"));
              if (!Strings.isNullOrEmpty(this.speedExpression) && !this.speedExpression.equals("null")) {
                  buf.writeUtf(this.speedExpression);
                  buf.writeDouble(this.speedStep);
              }

              buf.writeBoolean(!Strings.isNullOrEmpty(this.group) && !this.group.equals("null"));
              if (!Strings.isNullOrEmpty(this.group) && !this.group.equals("null")) {
                  buf.writeUtf(this.group);
              }
              buf.writeUtf(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(this.effect.getType()).toString());
              this.effect.writeToNetwork(buf);
  }

  public static ConditionalPayload read(FriendlyByteBuf buf) {
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
              ParticleOptions effect = ParticleOptions.fromNetwork(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.get(net.minecraft.resources.new ResourceLocation(buf.readUtf())), buf);
             return new ConditionalPayload(x,y,z,red,green,blue,alpha,vx,vy,vz,dx,dy,dz,expression,step,age,speedExpression,speedStep,group,effect);
  }

  @Override
  public net.minecraft.resources.ResourceLocation id() {
    return NetworkIdentifiers.CONDITIONAL_PACKET_ID;
  }
}
