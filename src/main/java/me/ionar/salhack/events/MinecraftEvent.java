package me.ionar.salhack.events;

import me.ionar.salhack.main.Wrapper;
import me.zero.alpine.fork.event.type.Cancellable;

public class MinecraftEvent extends Cancellable
{
    private Stage _stage = Stage.Pre;
    private final float partialTicks;
    private Era era = Era.PRE;

    public Era getEra()
    {
        return era;
    }


    public MinecraftEvent()
    {
        partialTicks = Wrapper.GetMC().getRenderPartialTicks();
    }
    
    public MinecraftEvent(Stage stage)
    {
        this();
        _stage = stage;
    }

    public Stage getStage()
    {
        return _stage;
    }

    public void setEra(Stage stage)
    {
        this.setCancelled(false);
        _stage = stage;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
    
    public enum Stage
    {
        Pre,
        Post,
    }

    public enum Era
    {
        PRE,
        PERI,
        POST,
    }
}
