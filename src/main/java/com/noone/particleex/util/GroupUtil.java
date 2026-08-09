package com.noone.particleex.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.noone.particleex.mixin.IParticleMixin;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.particle.Particle;

public class GroupUtil {
   private static final Map<String, List<Particle>> GROUPS = Maps.newHashMap();

   public static void add(String group, Particle particle) {
      if (group != null && !group.equals("null")) {
         String[] groups = group.split("\\|");

          for (String str : groups) {
              if (!GROUPS.containsKey(str)) {
                  GROUPS.put(str, Lists.newArrayList());
              }

              GROUPS.get(str).add(particle);
          }

      }
   }

   public static void remove(String group, String expression, double cx, double cy, double cz) {
      if (group != null && !group.equals("null")) {
         String[] groups = group.split("\\|");

          for (String str : groups) {
              if (GROUPS.containsKey(str)) {
                  List<Particle> particles = GROUPS.get(str);
                  if (expression != null && !expression.equals("null")) {
                      IExecutable exe = ExpressionUtil.parse(expression);
                      ParticleStruct data = null;
                      if (exe != null) {
                          data = exe.getData();
                      }

                      for (Particle o : particles) {
                          if (o.isAlive()) {
                              double dx = ((IParticleMixin) o).getX() - cx;
                              double dy = ((IParticleMixin) o).getY() - cy;
                              double dz = ((IParticleMixin) o).getZ() - cz;
                              if(data != null) {
                                 data.x = dx;
                                 data.y = dy;
                                 data.z = dz;
                                 data.s1 = Math.atan2(dz, dx);
                                 data.s2 = Math.atan2(dy, Math.hypot(dx, dz));
                                 data.dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                 data.age = ((IParticleMixin) o).getAge();
                              }
                              if (exe != null && exe.invoke() != 0) {
                                  o.markDead();
                              }
                          }
                      }

                      particles.removeIf((particle) -> !particle.isAlive());
                  } else {
                      for (Particle o : particles) {
                          o.markDead();
                      }
                      particles.clear();
                  }
              }
          }

      }
   }

   public static List<Particle> get(String group) {
      if (group != null && !group.equals("null")) {
         List<Particle> particles = Lists.newArrayList();
         String[] groups = group.split("\\|");
          for (String str : groups) {
              if (GROUPS.containsKey(str)) {
                  particles.addAll(GROUPS.get(str));
              }
          }

         return particles;
      } else {
         return Collections.emptyList();
      }
   }

   public static void clear() {

       for (List<Particle> particleList : GROUPS.values()) {
           particleList.clear();
       }

      GROUPS.clear();
   }
}
