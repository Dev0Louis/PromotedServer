package dev.louis.promotedserver.mixin;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Shadow private ButtonWidget buttonEdit;
    @Shadow private ButtonWidget buttonDelete;

    @Inject(method = "editEntry",locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ServerInfo;name:Ljava/lang/String;")
            , cancellable = true)
    public void doNotEditEntryOfPromotedServers(boolean confirmedAction, CallbackInfo ci, MultiplayerServerListWidget.Entry entry, ServerInfo serverInfo) {
        if(serverInfo.promotedserver$isPromoted()) {
               ci.cancel();
        }

    }

    @Inject(method = "removeEntry",locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "FIELD", target = "net/minecraft/client/gui/screen/multiplayer/MultiplayerScreen.serverList:Lnet/minecraft/client/option/ServerList;")
            , cancellable = true)
    public void doNotRemoveEntryOfPromotedServers(boolean confirmedAction, CallbackInfo ci, MultiplayerServerListWidget.Entry entry) {
        if(entry instanceof MultiplayerServerListWidget.ServerEntry) {
            ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry) entry).getServer();
            if(serverInfo.promotedserver$isPromoted()) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "updateButtonActivationStates",
            at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void disableButtonsForPromotedServers(CallbackInfo ci, MultiplayerServerListWidget.Entry entry) {
        if(entry instanceof MultiplayerServerListWidget.ServerEntry) {
            ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry) entry).getServer();

            if(serverInfo.promotedserver$isPromoted()) {
                this.buttonEdit.active = false;
                this.buttonDelete.active = false;
            } else {
                this.buttonEdit.visible = true;
                this.buttonDelete.visible = true;
            }
        }
    }
}
