package me.ionar.salhack.module.movement;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.events.packet.PacketEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
 
public class AirJumpModule extends Module
{

	public AirJumpModule()
    {
        super("AirJump", new String[]
                { "AirJump" }, "Allows you to jump while in air.", "NONE", -1, ModuleType.MOVEMENT);
    }

	@EventHandler
    private Listener<PacketEvent> OnSendPacket = new Listener<>(p_Event ->
    {
		if (mc.player != null) {
			mc.player.onGround = mc.player.ticksExisted % 2 == 0;
		}
	});
}
