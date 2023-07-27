package dev.louis.promotedserver.mixin;

import dev.louis.promotedserver.api.PromotedServerInfo;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class ServerInfoMixin implements PromotedServerInfo {
    @Unique
    public boolean promotedserver$isPromoted = false;

    public void promotedserver$markAsPromoted() {
        this.promotedserver$isPromoted = true;
    }

    public boolean promotedserver$isPromoted() {
        return this.promotedserver$isPromoted;
    }
}
