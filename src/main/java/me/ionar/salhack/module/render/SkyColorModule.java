package me.ionar.salhack.module.render;

import java.awt.Color;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkyColorModule extends Module {
    public final Value<Integer> r = new Value("Red", new String[]{"R"}, "Color red.", 255, 0, 255, 5);
    public final Value<Integer> g = new Value("Green", new String[]{"G"}, "Green color.", 0, 0, 255, 5);
    public final Value<Integer> b = new Value("Blue", new String[]{"B"}, "Blue color.", 255, 0, 255, 5);

    public SkyColorModule() {
        super("SkyColor", new String[]{"SKYC"}, "Changes the color of the sky.", "NONE", 14367780, Module.ModuleType.RENDER);
    }

    public int getSkyColorByTemp(float par1) {
        return Color.red.getRGB();
    }

    @SubscribeEvent
    public void fogColors(FogColors event) {
        event.setRed((float)(Integer)this.r.getValue() / 255.0F);
        event.setGreen((float)(Integer)this.g.getValue() / 255.0F);
        event.setBlue((float)(Integer)this.b.getValue() / 255.0F);
    }

    @SubscribeEvent
    public void fogDensity(FogDensity event) {
        event.setDensity(0.0F);
        event.setCanceled(true);
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
