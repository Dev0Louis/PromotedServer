package dev.louis.promotedserver.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PromotedServerConfig {
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public List<ServerInfo> promotedServers = new ArrayList<>() {
        @Override
        public void add(int index, ServerInfo element) {
            super.add(index, element);
        }
    };
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
        NbtCompound nbtCompound = NbtIo.read(PATH.resolve("servers.dat"));
        if (nbtCompound == null) {
            if(rerun)return this;
            writeDefault();
            return readConfig(true);
        }
        nbtCompound.getList("servers").ifPresent(nbtList -> {
            for (int i = 0; i < nbtList.size(); ++i) {
                ServerInfo promotedServer = ServerInfo.fromNbt(nbtList.getCompound(i).get());
                promotedServer.promotedserver$markAsPromoted();
                this.promotedServers.add(promotedServer);
            }
        });
        return this;
    }


}