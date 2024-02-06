package dev.louis.promotedserver.mixin;

import dev.louis.promotedserver.config.PromotedServerConfig;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(ServerList.class)
public abstract class ServerListMixin {

    @Shadow @Final private List<ServerInfo> servers;
    @Shadow public abstract ServerInfo get(int index);

    @Inject(method = "loadFile",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NbtIo;read(Ljava/nio/file/Path;)Lnet/minecraft/nbt/NbtCompound;"))
    public void addPromotedServers(CallbackInfo ci) {
        var promotedServers = PromotedServerConfig.readConfig().promotedServers;
        this.servers.addAll(promotedServers);
        //this.servers.add(new CustomServerInfo("ChainSMP", "chain.smpmc.eu", false, true));
    }

    @ModifyVariable(
            method = "saveFile",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    public Iterator<ServerInfo> removePromotedServersBeforeSaving(Iterator<ServerInfo> value) {
        List<ServerInfo> serverInfosToRemove = new ArrayList<>();
        for (ServerInfo serverInfo : this.servers) {
            if(serverInfo.promotedserver$isPromoted()) {
                serverInfosToRemove.add(serverInfo);
            }
        }
        List<ServerInfo> returnServerInfos = new ArrayList<>(this.servers);
        returnServerInfos.removeAll(serverInfosToRemove);
        return returnServerInfos.iterator();
    }

    @Inject(
            method = "swapEntries",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void noMoveMixin(int index1, int index2, CallbackInfo ci) {
        if(this.get(index1).promotedserver$isPromoted() || this.get(index2).promotedserver$isPromoted()) {
            ci.cancel();
        }

    }


}
