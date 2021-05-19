package me.ionar.salhack.events.render;

import me.ionar.salhack.events.MinecraftEvent;
import net.minecraft.util.EnumHandSide;

public class EventTransformSideFirstPersonEvent extends MinecraftEvent {
    private final EnumHandSide handSide;

    public EventTransformSideFirstPersonEvent(EnumHandSide handSide) {
        this.handSide = handSide;
    }

    public EnumHandSide getHandSide() {
        return this.handSide;
    }
}
