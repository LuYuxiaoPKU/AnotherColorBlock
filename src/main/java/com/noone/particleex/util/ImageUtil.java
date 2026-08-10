package com.noone.particleex.util;

import com.google.common.collect.Maps;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageUtil {
   private static final Map<File, Map<Double, BufferedImage>> IMAGEBUF = Maps.newHashMap();
   private static final File IMAGEDIR = new File("./particleImages");

    public static BufferedImage readImage(String path, double scaling) throws IOException {
       File imageFile = new File(IMAGEDIR, path).getCanonicalFile();
       if (!imageFile.getPath().startsWith(IMAGEDIR.getCanonicalPath() + File.separator)) {
          throw new IOException("invalid image path: " + path);
       }
       BufferedImage resultImage;
      int width;
      int height;
      int dw;
      BufferedImage image;
      int dh;
      Graphics2D graphics;
      if (IMAGEBUF.containsKey(imageFile)) {
         Map<Double, BufferedImage> buf = IMAGEBUF.get(imageFile);
         if (buf.containsKey(scaling)) {
            resultImage = buf.get(scaling);
         } else {
            image = buf.get(1.0D);
            width = image.getWidth();
            height = image.getHeight();
            dw = (int)((double)width * scaling);
            dh = (int)((double)height * scaling);
            resultImage = new BufferedImage(dw, dh, image.getType());
            graphics = resultImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, dw, dh, 0, 0, width, height, null);
            graphics.dispose();
            buf.put(scaling, resultImage);
         }
      } else {
         Map<Double, BufferedImage> buf = Maps.newHashMap();
         image = ImageIO.read(imageFile);
         if (scaling == 1.0D) {
            resultImage = image;
            buf.put(1.0D, image);
         } else {
            width = image.getWidth();
            height = image.getHeight();
            dw = (int)((double)width * scaling);
            dh = (int)((double)height * scaling);
            resultImage = new BufferedImage(dw, dh, image.getType());
            graphics = resultImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, dw, dh, 0, 0, width, height, null);
            graphics.dispose();
            buf.put(1.0D, image);
            buf.put(scaling, resultImage);
         }

         IMAGEBUF.put(imageFile, buf);
      }
      return resultImage;
   }

   public static void clear() {
      IMAGEBUF.clear();
   }

   static {
      if (IMAGEDIR.exists() && IMAGEDIR.isFile()) {
         IMAGEDIR.delete();
      }

      if (!IMAGEDIR.exists()) {
         IMAGEDIR.mkdirs();
      }

   }
}
