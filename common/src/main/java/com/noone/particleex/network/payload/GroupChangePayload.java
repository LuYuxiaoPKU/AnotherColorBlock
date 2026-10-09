package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record GroupChangePayload(int changeType,String group, String expression,String conditionalExpression, Vec3 pos) implements CustomPacketPayload {
  public static final Type<GroupChangePayload> TYPE = new Type<>(NetworkIdentifiers.GROUP_CHANGE_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, GroupChangePayload> CODEC = StreamCodec.of(
          (buf, value) -> {
              buf.writeInt(value.type);
              buf.writeUtf(value.group);
              buf.writeUtf(value.expression);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.conditionalExpression) && !value.conditionalExpression.equals("null"));
              if (!Strings.isNullOrEmpty(value.conditionalExpression) && !value.conditionalExpression.equals("null")) {
                  buf.writeUtf(value.conditionalExpression);
              }

              buf.writeBoolean(value.pos != null);
              if (value.pos != null) {
                  buf.writeDouble(value.pos.x);
                  buf.writeDouble(value.pos.y);
                  buf.writeDouble(value.pos.z);
              }
          },
          buf -> {
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
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
