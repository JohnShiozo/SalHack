package me.ionar.salhack.module.movement;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.events.packet.PacketEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AirJumpModule extends Module {

	public AirJumpModule()
    {
        super("AirJump", new String[]
                { "AirJump" }, "Allows you to jump while in air.", "NONE", -1, ModuleType.MOVEMENT);
    }

	@SubscribeEvent
	public void onSendPacket(PacketEvent event) {
		if (mc.player != null) {
			mc.player.onGround = mc.player.ticksExisted % 2 == 0;
		}
	}

}
