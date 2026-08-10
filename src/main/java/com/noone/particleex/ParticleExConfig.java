package com.noone.particleex;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class ParticleExConfig {
    public static ConfigData config = new ParticleExConfig.ConfigData();

    public static void init() throws IOException {
        Gson gson = new Gson();
        Files.createDirectories(FabricLoader.getInstance().getConfigDir());
        Path file = FabricLoader.getInstance().getConfigDir().resolve("particleex.json");
        if (!Files.exists(file)) {
            BufferedWriter writer = Files.newBufferedWriter(file);
            try {
                gson.toJson(config, writer);
            } catch (Throwable t1) {
                try {
                    writer.close();
                } catch (Throwable t2) {
                    t1.addSuppressed(t2);
                }
                throw t1;
            }
            writer.close();
        }
        BufferedReader reader = Files.newBufferedReader(file);
        try {
            config = gson.fromJson(reader, ConfigData.class);
        } catch (Throwable t1) {
            try {
                reader.close();
            } catch (Throwable t2) {
                t1.addSuppressed(t2);
            }

            throw t1;
        }
        reader.close();
    }
    public static class ConfigData {
        public int maxParticleCount = 65536;
        public boolean ParallelParticleUpdate = false;
    }
}
