package me.ionar.salhack.module.render;

import java.util.function.Predicate;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.EventTransformSideFirstPersonEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ViewModelModule extends Module {
    public static Value<Double> xLeft = new Value("Left X", new String[]{""}, "LeftX", 2.0D, 0.0D, 4.0D, 0.1D);
    public static Value<Double> yLeft = new Value("Left Y", new String[]{""}, "LeftY", 2.2D, 0.0D, 4.0D, 0.1D);
    public static Value<Double> zLeft = new Value("Left Z", new String[]{""}, "LeftZ", 1.2D, 0.0D, 4.0D, 0.1D);
    public static Value<Double> xRight = new Value("Right X", new String[]{""}, "RightX", 2.0D, 0.0D, 4.0D, 0.1D);
    public static Value<Double> yRight = new Value("Right Y", new String[]{""}, "RightY", 2.2D, 0.0D, 4.0D, 0.1D);
    public static Value<Double> zRight = new Value("Right Z", new String[]{""}, "RightZ", 1.2D, 0.0D, 4.0D, 0.1D);
    @EventHandler
    private final Listener<EventTransformSideFirstPersonEvent> eventListener = new Listener<EventTransformSideFirstPersonEvent>(event -> {
        if (event.getHandSide() == EnumHandSide.RIGHT) {
            GlStateManager.translate((Double)xRight.getValue() - 2.0D, (Double)yRight.getValue() - 2.0D, (Double)zRight.getValue() - 2.0D);
        } else if (event.getHandSide() == EnumHandSide.LEFT) {
            GlStateManager.translate((Double)xLeft.getValue() - 2.0D, (Double)yLeft.getValue() - 2.0D, (Double)zLeft.getValue() - 2.0D);
        }

    }, new Predicate[0]);

    public ViewModelModule() {
        super("ViewModel", new String[]{""}, "Changes the viewport.", "NONE", -1, Module.ModuleType.RENDER);
    }

    public void onEnable() {
        SalHackMod.EVENT_BUS.subscribe((Listenable)this);
    }

    public void onDisable() {
        SalHackMod.EVENT_BUS.unsubscribe((Listenable)this);
    }
}
