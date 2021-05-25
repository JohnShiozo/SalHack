// Decompiled with: FernFlower
// Class Version: 8
package me.ionar.salhack.mixin.client;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.EventRenderUpdateEquippedItem;
import me.ionar.salhack.events.render.EventTransformSideFirstPersonEvent;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
    @Inject(
        method = {"transformSideFirstPerson"},
        at = {@At("HEAD")}
    )
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo ci) {
        EventTransformSideFirstPersonEvent event = new EventTransformSideFirstPersonEvent(hand);
        SalHackMod.EVENT_BUS.post(event);
    }

    @Inject(
        method = {"updateEquippedItem"},
        at = {@At("HEAD")},
        cancellable = true
    )
    public void updateEquippedItem(CallbackInfo p_Info) {
        EventRenderUpdateEquippedItem l_Event = new EventRenderUpdateEquippedItem();
        SalHackMod.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) {
            p_Info.cancel();
        }

    }
}
