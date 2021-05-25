package me.ionar.salhack.events;

import me.ionar.salhack.events.bus.Cancellable;
import me.ionar.salhack.main.Wrapper;

public class MCEventBot extends Cancellable
{
    private Stage _stage = Stage.Pre;
    private final float partialTicks;
    private Era era = Era.PRE;

    public Era getEra()
    {
        return era;
    }


    public MCEventBot()
    {
        partialTicks = Wrapper.GetMC().getRenderPartialTicks();
    }
    
    public MCEventBot(Stage stage)
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

