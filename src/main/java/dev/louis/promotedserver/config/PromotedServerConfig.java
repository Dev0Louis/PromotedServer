package dev.louis.promotedserver.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PromotedServerConfig {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public List<ServerInfo> promotedServers = new ArrayList<>();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("promotedserver");

    public static void writeDefault() {
        try {
            PATH.toFile().mkdirs();
            NbtList nbtList = new NbtList();
            new PromotedServerConfig().promotedServers.forEach(serverInfo -> nbtList.add(serverInfo.toNbt()));
            NbtCompound nbt = new NbtCompound();
            nbt.put("servers", nbtList);
            NbtIo.write(nbt, PATH);
        } catch (Exception ignored) {}
    }

    public static PromotedServerConfig readConfig() {
        try {
            return new PromotedServerConfig().readConfig(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PromotedServerConfig readConfig(boolean rerun) throws IOException {
        NbtCompound nbtCompound = NbtIo.read(Path.of("servers.dat"));
        if (nbtCompound == null) {
            if(rerun)return this;
            writeDefault();
            return readConfig(true);
        }
        NbtList nbtList = nbtCompound.getList("servers", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            ServerInfo promotedServer = ServerInfo.fromNbt(nbtList.getCompound(i));
            promotedServer.promotedserver$markAsPromoted();
            this.promotedServers.add(promotedServer);
        }
        return this;
    }


}