package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector4f;

import static com.noone.particleex.network.PayloadCodecUtil.*;

public record ParameterPayload(boolean polar, boolean tick, boolean rgba, double x, double y, double z, Vector4f color, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
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
              buf.writeUtf(this.effect.getType().getRegisteredName());
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
              ParticleOptions effect = ParticleOptions.fromNetwork(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.get(net.minecraft.resources.ResourceLocation.parse(buf.readUtf())), buf);
              return new ParameterPayload(polar,tick,rgba,x,y,z,color,vx,vy,vz,begin,end,expression,step,cpt,age,speedExpression,speedStep,group,effect);
  }

  @Override
  public net.minecraft.resources.ResourceLocation id() {
    return NetworkIdentifiers.PARAMETER_PACKET_ID;
  }
}
