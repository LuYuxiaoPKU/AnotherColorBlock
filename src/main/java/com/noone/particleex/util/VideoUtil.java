package com.noone.particleex.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noone.particleex.ParticleEx;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class VideoUtil {
   private static final ThreadPoolExecutor VIDEO_DECODER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Video Decoder #%d").setDaemon(true).setUncaughtExceptionHandler((thread, throwable) -> ClientMessageUtil.addChatMessage(throwable)).build());
   private static final File VIDEODIR = new File("./particleVideos");
   private static volatile URLClassLoader javacvLoader;

   public static void decoder(String path, Predicate<BufferedImage> consumer) {
      URLClassLoader loader = getJavacvLoader();
      if (loader == null) {
         ParticleEx.LOGGER.info("javacv jars not found, video function disabled. Put the javacv jars into the javacv folder under the game directory.");
         return;
      }
      VIDEO_DECODER_THREAD_POOL.execute(() -> {
         try {
            decode(loader, path, consumer);
         } catch (Exception e) {
            Throwable cause = e;
            while (cause instanceof InvocationTargetException && cause.getCause() != null) {
               cause = cause.getCause();
            }
            ParticleEx.LOGGER.info(cause.getMessage());
         }
      });
   }

   private static URLClassLoader getJavacvLoader() {
      URLClassLoader loader = javacvLoader;
      if (loader != null) {
         return loader;
      }
      synchronized (VideoUtil.class) {
         if (javacvLoader != null) {
            return javacvLoader;
         }
         Path dir = FabricLoader.getInstance().getGameDir().resolve("javacv");
         try {
            if (Files.exists(dir) && !Files.isDirectory(dir)) {
               Files.delete(dir);
            }
            Files.createDirectories(dir);
            List<URL> urls = new ArrayList<>();
            try (Stream<Path> stream = Files.list(dir)) {
               stream.filter(p -> p.toString().endsWith(".jar")).forEach(p -> {
                  try {
                     urls.add(p.toUri().toURL());
                  } catch (Exception ignored) {
                  }
               });
            }
            if (urls.isEmpty()) {
               ParticleEx.LOGGER.info("No javacv jars found in " + dir + ", video function disabled. Put the javacv jars into this folder.");
               return null;
            }
            javacvLoader = new URLClassLoader(urls.toArray(new URL[0]), VideoUtil.class.getClassLoader());
         } catch (Exception e) {
            ParticleEx.LOGGER.info(e.getMessage());
            return null;
         }
         return javacvLoader;
      }
   }

   private static void decode(URLClassLoader loader, String path, Predicate<BufferedImage> consumer) throws Exception {
      File videoFile = new File(VIDEODIR, path).getCanonicalFile();
      if (!videoFile.getPath().startsWith(VIDEODIR.getCanonicalPath() + File.separator)) {
         throw new IOException("invalid video path: " + path);
      }
      Class<?> grabberClass = Class.forName("org.bytedeco.javacv.FFmpegFrameGrabber", true, loader);
      Class<?> converterClass = Class.forName("org.bytedeco.javacv.Java2DFrameConverter", true, loader);
      Class<?> frameClass = Class.forName("org.bytedeco.javacv.Frame", true, loader);
      Constructor<?> grabberConstructor = grabberClass.getConstructor(File.class);
      Constructor<?> converterConstructor = converterClass.getConstructor();
      Method start = grabberClass.getMethod("start");
      Method stop = grabberClass.getMethod("stop");
      Method close = grabberClass.getMethod("close");
      Method grab = grabberClass.getMethod("grab");
      Method getFrameRate = grabberClass.getMethod("getFrameRate");
      Method getLengthInVideoFrames = grabberClass.getMethod("getLengthInVideoFrames");
      Method convert = converterClass.getMethod("convert", frameClass);
      Method converterClose = converterClass.getMethod("close");
      Field imageField = frameClass.getField("image");

      Object grabber = grabberConstructor.newInstance(videoFile);
      try {
         Object converter = converterConstructor.newInstance();
         try {
            start.invoke(grabber);
            long startTime = System.currentTimeMillis();
            int count = 0;
            double rate = (Double) getFrameRate.invoke(grabber);
            int length = (Integer) getLengthInVideoFrames.invoke(grabber) - 1;

            while (count < length) {
               long curTime = System.currentTimeMillis();
               Object frame = null;

               while ((double) (curTime - startTime) > (double) (count * 1000) / rate) {
                  frame = grab.invoke(grabber);
                  if (frame != null && imageField.get(frame) != null) {
                     ++count;
                  }
               }

               if (frame != null && !consumer.test((BufferedImage) convert.invoke(converter, frame))) {
                  break;
               }

               Thread.sleep(10L);
            }

            stop.invoke(grabber);
         } finally {
            converterClose.invoke(converter);
         }
      } finally {
         close.invoke(grabber);
      }
   }
}
