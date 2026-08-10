package com.noone.particleex.network.payload;

import com.google.common.base.Strings;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import static com.noone.particleex.network.ClientNetworkHandler.readDouble;
import static com.noone.particleex.network.ClientNetworkHandler.readString;

public record GroupRemovePayload(String group, String expression, Vec3 pos) implements CustomPacketPayload {
  public static final Type<GroupRemovePayload> ID = new Type<>(NetworkIdentifiers.GROUP_REMOVE_PACKET_ID);
  public static final StreamCodec<RegistryFriendlyByteBuf, GroupRemovePayload> CODEC = StreamCodec.ofMember(
          (value, buf) -> {
              buf.writeUtf(value.group);
              buf.writeBoolean(!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null"));
              if (!Strings.isNullOrEmpty(value.expression) && !value.expression.equals("null")) {
                  buf.writeUtf(value.expression);
              }

              buf.writeBoolean(value.pos != null);
              if (value.pos != null) {
                  buf.writeDouble(value.pos.x);
                  buf.writeDouble(value.pos.y);
                  buf.writeDouble(value.pos.z);
              }
          },
          buf -> {
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
  );
  @Override
   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
