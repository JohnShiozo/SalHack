package me.ionar.salhack.events.network;

import me.ionar.salhack.events.MinecraftEvent;
import net.minecraft.network.Packet;

public class EventNetworkPacketEvent extends MinecraftEvent {
    public Packet m_Packet;

    public EventNetworkPacketEvent(Packet p_Packet) {
        this.m_Packet = p_Packet;
    }

    public Packet GetPacket() {
        return this.m_Packet;
    }

    public Packet getPacket() {
        return this.m_Packet;
    }
}
