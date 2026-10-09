package com.noone.particleex.util;

public interface IParticle {
   void setExe(IExecutable var1);

   void setStep(double var1);

   double getCenterX();

   void setCenterX(double var1);

   double getCenterY();

   void setCenterY(double var1);

   double getCenterZ();

   void setCenterZ(double var1);

   void setCustomMove(boolean var1);

   void setStop(boolean var1);

   void setRenderColor(float red, float green, float blue, float alpha);

   void customTick();
}
