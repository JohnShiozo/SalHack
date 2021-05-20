package me.ionar.salhack.events.network;

import me.ionar.salhack.events.MCEventBot;
import net.minecraft.network.Packet;

public class EventPacket extends MCEventBot
{
    private Packet<?>  _packet;
    
    public EventPacket(Packet<?>  packet, Stage stage)
    {
        super(stage);
        _packet = packet;
    }

    public Packet<?> getPacket()
    {
        return _packet;
    }
}