package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.PayloadCodecUtil;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public class NormalPayload implements com.noone.particleex.network.ParticlePayload {
  public final double x;
  public final double y;
  public final double z;
  public final float red;
  public final float green;
  public final float blue;
  public final float alpha;
  public final double vx;
  public final double vy;
  public final double vz;
  public final double dx;
  public final double dy;
  public final double dz;
  public final int count;
  public final int age;
  public final String speedExpression;
  public final double speedStep;
  public final String group;
  public final ParticleOptions effect;
  public NormalPayload(double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double dx, double dy, double dz, int count, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.alpha = alpha;
    this.vx = vx;
    this.vy = vy;
    this.vz = vz;
    this.dx = dx;
    this.dy = dy;
    this.dz = dz;
    this.count = count;
    this.age = age;
    this.speedExpression = speedExpression;
    this.speedStep = speedStep;
    this.group = group;
    this.effect = effect;
  }
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
              buf.writeInt(this.count);
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
              buf.writeResourceLocation(net.minecraft.core.Registry.PARTICLE_TYPE.getKey(this.effect.getType()));
              this.effect.writeToNetwork(buf);
  }

  public static NormalPayload read(FriendlyByteBuf buf) {
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
             ParticleOptions effect = com.noone.particleex.network.PayloadCodecUtil.readParticle(buf);
             return new NormalPayload(x,y,z,red,green,blue,alpha,vx,vy,vz,dx,dy,dz,count,age,speedExpression,speedStep,group,effect);
  }
  @Override
  public net.minecraft.resources.ResourceLocation packetId() {
    return NetworkIdentifiers.NORMAL_PACKET_ID;
  }
}
