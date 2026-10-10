package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.PayloadCodecUtil;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readDouble;
import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record GroupRemovePayload(String group, String expression, Vec3 pos) implements CustomPacketPayload {
  @Override
  public void write(FriendlyByteBuf buf) {
              buf.writeUtf(this.group);
              buf.writeBoolean(!Strings.isNullOrEmpty(this.expression) && !this.expression.equals("null"));
              if (!Strings.isNullOrEmpty(this.expression) && !this.expression.equals("null")) {
                  buf.writeUtf(this.expression);
              }

              buf.writeBoolean(this.pos != null);
              if (this.pos != null) {
                  buf.writeDouble(this.pos.x);
                  buf.writeDouble(this.pos.y);
                  buf.writeDouble(this.pos.z);
              }
  }

  public static GroupRemovePayload read(FriendlyByteBuf buf) {
              String group = buf.readUtf();
              String expression = readString(buf, buf.readBoolean());
              boolean hasPos = buf.readBoolean();
              Vec3 pos = null;
              if(hasPos){
                  Double x = buf.readDouble();
                  Double y = buf.readDouble();
                  Double z = buf.readDouble();
                  pos = new Vec3(x, y, z);
              }
              return new GroupRemovePayload(group,expression,pos);
  }

  @Override
  public net.minecraft.resources.ResourceLocation id() {
    return NetworkIdentifiers.GROUPREMOVE_PACKET_ID;
  }
}
