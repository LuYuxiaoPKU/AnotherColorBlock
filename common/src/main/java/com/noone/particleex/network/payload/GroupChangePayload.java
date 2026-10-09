package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.PayloadCodecUtil.readString;

public record GroupChangePayload(int type,String group, String expression,String conditionalExpression, Vec3d pos) implements CustomPacketPayload {
  public static final Type<GroupChangePayload> TYPE = new Type<>(NetworkIdentifiers.GROUP_CHANGE_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, GroupChangePayload> CODEC = StreamCodec.of(
          (value, buf) -> {
              buf.writeInt(value.type);
              buf.writeString(value.group);
              buf.writeString(value.expression);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.conditionalExpression) && !value.conditionalExpression.equals("null"));
              if (!Strings.isNullOrEmpty(value.conditionalExpression) && !value.conditionalExpression.equals("null")) {
                  buf.writeString(value.conditionalExpression);
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
              String group = buf.readString();
              String expression = buf.readString();
              String conditionalExpression = readString(buf, buf.readBoolean());
              boolean hasPos = buf.readBoolean();
              Vec3d pos = null;
              if(hasPos){
                  Double x = buf.readDouble();
                  Double y = buf.readDouble();
                  Double z = buf.readDouble();
                  pos = new Vec3d(x, y, z);
              }
              return new GroupChangePayload(type,group,expression,conditionalExpression,pos);
          }
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
