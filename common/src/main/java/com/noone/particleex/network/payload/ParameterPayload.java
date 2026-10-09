package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.ParticleEx;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector4f;

import static com.noone.particleex.network.PayloadCodecUtil.*;

public record ParameterPayload(boolean polar, boolean tick, boolean rgba, double x, double y, double z, Vector4f color, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, ParticleOptions effect) implements CustomPacketPayload {
  public static final Type<ParameterPayload> TYPE = new Type<>(NetworkIdentifiers.PARAMETER_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, ParameterPayload> CODEC = StreamCodec.of(
          (value, buf) -> {
              buf.writeBoolean(value.polar);
              buf.writeBoolean(value.tick);
              buf.writeBoolean(value.rgba);
              buf.writeDouble(value.x);
              buf.writeDouble(value.y);
              buf.writeDouble(value.z);
              if (!value.rgba) {
                  buf.writeFloat(value.color.x);
                  buf.writeFloat(value.color.y);
                  buf.writeFloat(value.color.z);
                  buf.writeFloat(value.color.w);
              }

              buf.writeDouble(value.vx);
              buf.writeDouble(value.vy);
              buf.writeDouble(value.vz);
              buf.writeDouble(value.begin);
              buf.writeDouble(value.end);
              buf.writeString(value.expression);
              buf.writeDouble(value.step);
              if (value.tick) {
                  buf.writeInt(value.cpt);
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
              ParticleOptions effect = ParticleTypes.STREAM_CODEC.decode(buf);
              return new ParameterPayload(polar,tick,rgba,x,y,z,color,vx,vy,vz,begin,end,expression,step,cpt,age,speedExpression,speedStep,group,effect);
          }
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
