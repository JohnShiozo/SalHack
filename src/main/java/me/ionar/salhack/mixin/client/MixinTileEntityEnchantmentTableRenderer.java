package me.ionar.salhack.mixin.client;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.EventRenderEnchantingTableBook;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityEnchantmentTableRenderer.class)
public class MixinTileEntityEnchantmentTableRenderer {
    @Inject(method = "render", at = @At(value = "INVOKE"), cancellable = true)
    private void renderEnchantingTableBook(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        EventRenderEnchantingTableBook l_Event = new EventRenderEnchantingTableBook();
        SalHackMod.EVENT_BUS.post(l_Event);
        if(l_Event.isCancelled()) ci.cancel();
    }
}