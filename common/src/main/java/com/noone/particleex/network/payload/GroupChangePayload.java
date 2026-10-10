package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record GroupChangePayload(int changeType,String group, String expression,String conditionalExpression, Vec3 pos) implements com.noone.particleex.network.ParticlePayload {
  @Override
  public void write(FriendlyByteBuf buf) {
              buf.writeInt(this.changeType);
              buf.writeUtf(this.group);
              buf.writeUtf(this.expression);
              buf.writeBoolean(!Strings.isNullOrEmpty(this.conditionalExpression) && !this.conditionalExpression.equals("null"));
              if (!Strings.isNullOrEmpty(this.conditionalExpression) && !this.conditionalExpression.equals("null")) {
                  buf.writeUtf(this.conditionalExpression);
              }

              buf.writeBoolean(this.pos != null);
              if (this.pos != null) {
                  buf.writeDouble(this.pos.x);
                  buf.writeDouble(this.pos.y);
                  buf.writeDouble(this.pos.z);
              }
  }

  public static GroupChangePayload read(FriendlyByteBuf buf) {
              int type = buf.readInt();
              String group = buf.readUtf();
              String expression = buf.readUtf();
              String conditionalExpression = readString(buf, buf.readBoolean());
              boolean hasPos = buf.readBoolean();
              Vec3 pos = null;
              if(hasPos){
                  Double x = buf.readDouble();
                  Double y = buf.readDouble();
                  Double z = buf.readDouble();
                  pos = new Vec3(x, y, z);
              }
              return new GroupChangePayload(type,group,expression,conditionalExpression,pos);
  }
  @Override
  public net.minecraft.resources.ResourceLocation packetId() {
    return NetworkIdentifiers.GROUP_CHANGE_PAYLOAD_PACKET_ID;
  }
}
