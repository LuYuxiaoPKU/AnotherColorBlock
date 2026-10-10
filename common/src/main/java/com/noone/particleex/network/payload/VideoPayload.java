package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readDouble;
import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record VideoPayload(double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, int flip, double dpb, Vec3 speed, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements com.noone.particleex.network.ParticlePayload {
  @Override
  public void write(FriendlyByteBuf buf) {
              buf.writeDouble(this.x);
              buf.writeDouble(this.y);
              buf.writeDouble(this.z);
              buf.writeUtf(this.path);
              buf.writeDouble(this.scaling);
              buf.writeInt(this.xRotate);
              buf.writeInt(this.yRotate);
              buf.writeInt(this.zRotate);
              buf.writeInt(this.flip);
              buf.writeDouble(this.dpb);
              if (this.speed == null) {
                  buf.writeDouble(0.0D);
                  buf.writeDouble(0.0D);
                  buf.writeDouble(0.0D);
              } else {
                  buf.writeDouble(this.speed.x);
                  buf.writeDouble(this.speed.y);
                  buf.writeDouble(this.speed.z);
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

              buf.writeId(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE, this.effect.getType());
              this.effect.writeToNetwork(buf);
  }

  public static VideoPayload read(FriendlyByteBuf buf) {
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
              ParticleOptions effect = com.noone.particleex.network.PayloadCodecUtil.readParticle(buf);
              return new VideoPayload(x,y,z,path,scaling,xRotate,yRotate,zRotate,flip,dpb,new Vec3(vx,vy,vz),age,speedExpression,speedStep,group,effect);
  }
}
