package me.ionar.salhack.module.movement;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.events.packet.PacketEvent;

import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
 
public class AirJumpModule extends Module{
	
public AirJumpModule()
    {
      super("AirJump", new String[]
      { "AirJump" }, "Allows you to jump while in air.", "NONE", -1, ModuleType.MOVEMENT);
    }

    @EventHandler
    private Listener<PacketEvent> OnSendPacket = new Listener<PacketEvent>(Event ->
    {
	if (mc.player != null) {
	mc.player.onGround = mc.player.ticksExisted % 2 == 0;
		}
	});
}
