package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector4f;

import static com.noone.particleex.network.PayloadCodecUtil.*;

public class ParameterPayload implements com.noone.particleex.network.ParticlePayload {
  public final boolean polar;
  public final boolean tick;
  public final boolean rgba;
  public final double x;
  public final double y;
  public final double z;
  public final Vector4f color;
  public final double vx;
  public final double vy;
  public final double vz;
  public final double begin;
  public final double end;
  public final String expression;
  public final double step;
  public final int cpt;
  public final int age;
  public final String speedExpression;
  public final double speedStep;
  public final String group;
  public final ParticleOptions effect;
  public ParameterPayload(boolean polar, boolean tick, boolean rgba, double x, double y, double z, Vector4f color, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) {
    this.polar = polar;
    this.tick = tick;
    this.rgba = rgba;
    this.x = x;
    this.y = y;
    this.z = z;
    this.color = color;
    this.vx = vx;
    this.vy = vy;
    this.vz = vz;
    this.begin = begin;
    this.end = end;
    this.expression = expression;
    this.step = step;
    this.cpt = cpt;
    this.age = age;
    this.speedExpression = speedExpression;
    this.speedStep = speedStep;
    this.group = group;
    this.effect = effect;
  }
  @Override
  public void write(FriendlyByteBuf buf) {
              buf.writeBoolean(this.polar);
              buf.writeBoolean(this.tick);
              buf.writeBoolean(this.rgba);
              buf.writeDouble(this.x);
              buf.writeDouble(this.y);
              buf.writeDouble(this.z);
              if (!this.rgba) {
                  buf.writeFloat(this.color.x);
                  buf.writeFloat(this.color.y);
                  buf.writeFloat(this.color.z);
                  buf.writeFloat(this.color.w);
              }

              buf.writeDouble(this.vx);
              buf.writeDouble(this.vy);
              buf.writeDouble(this.vz);
              buf.writeDouble(this.begin);
              buf.writeDouble(this.end);
              buf.writeUtf(this.expression);
              buf.writeDouble(this.step);
              if (this.tick) {
                  buf.writeInt(this.cpt);
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
              buf.writeResourceLocation(net.minecraft.core.Registry.PARTICLE_TYPE.getKey(this.effect.getType()));
              this.effect.writeToNetwork(buf);
  }

  public static ParameterPayload read(FriendlyByteBuf buf) {
              boolean polar = buf.readBoolean();
              boolean tick = buf.readBoolean();
              boolean rgba = buf.readBoolean();
              double x = buf.readDouble();
              double y = buf.readDouble();
              double z = buf.readDouble();
              Vector4f color = null;
              if(!rgba){
                  float red = buf.readFloat();
                  float green = buf.readFloat();
                  float blue = buf.readFloat();
                  float alpha = buf.readFloat();
                  color = new Vector4f(red, green, blue, alpha);
              }
              double vx = buf.readDouble();
              double vy = buf.readDouble();
              double vz = buf.readDouble();
              double begin = buf.readDouble();
              double end = buf.readDouble();
              String expression = buf.readUtf();
              double step = buf.readDouble();
              int cpt = readInt(buf, tick);
              int age = buf.readInt();
              boolean hasSpeedExpression = buf.readBoolean();
              String speedExpression = readString(buf, hasSpeedExpression);
              double speedStep = readDouble(buf, hasSpeedExpression, 1.0D);
              String group = readString(buf, buf.readBoolean());
              ParticleOptions effect = com.noone.particleex.network.PayloadCodecUtil.readParticle(buf);
              return new ParameterPayload(polar,tick,rgba,x,y,z,color,vx,vy,vz,begin,end,expression,step,cpt,age,speedExpression,speedStep,group,effect);
  }
  @Override
  public net.minecraft.resources.ResourceLocation packetId() {
    return NetworkIdentifiers.PARAMETER_PACKET_ID;
  }

  public boolean polar() { return this.polar; }
  public boolean tick() { return this.tick; }
  public boolean rgba() { return this.rgba; }
  public double x() { return this.x; }
  public double y() { return this.y; }
  public double z() { return this.z; }
  public Vector4f color() { return this.color; }
  public double vx() { return this.vx; }
  public double vy() { return this.vy; }
  public double vz() { return this.vz; }
  public double begin() { return this.begin; }
  public double end() { return this.end; }
  public String expression() { return this.expression; }
  public double step() { return this.step; }
  public int cpt() { return this.cpt; }
  public int age() { return this.age; }
  public String speedExpression() { return this.speedExpression; }
  public double speedStep() { return this.speedStep; }
  public String group() { return this.group; }
  public ParticleOptions effect() { return this.effect; }
}
