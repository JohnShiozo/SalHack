package me.ionar.salhack.events.network;

import net.minecraft.network.Packet;

public class EventServerPacket extends EventPacket
{
    public EventServerPacket(Packet<?> packet, Stage stage)
    {
        super(packet, stage);
    }
}