package me.ionar.salhack.events.packet;

import me.ionar.salhack.events.MinecraftEvent;
import net.minecraft.network.Packet;

public class PacketSendEvent extends MinecraftEvent {
	public Packet<?> packet;

	public PacketSendEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}
}
