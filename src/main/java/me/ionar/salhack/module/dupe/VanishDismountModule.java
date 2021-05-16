package me.ionar.salhack.module.dupe;

import java.util.function.Predicate;
import me.ionar.salhack.events.network.EventNetworkPacketEvent;
import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.module.Module;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public final class VanishDismountModule extends Module {
    private Entity Riding = null;
    @EventHandler
    private Listener<EventPlayerUpdate> OnUpdate = new Listener((p_Event) -> {
        if (this.Riding != null) {
            if (!this.mc.player.isRiding()) {
                this.mc.player.onGround = true;
                this.Riding.setPosition(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ);
                this.mc.player.connection.sendPacket(new CPacketVehicleMove(this.Riding));
            }
        }
    }, new Predicate[0]);
    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener((p_Event) -> {
        int var5;
        int l_EntityId;
        if (p_Event.getPacket() instanceof SPacketSetPassengers) {
            if (this.Riding == null) {
                return;
            }

            SPacketSetPassengers l_Packet = (SPacketSetPassengers)p_Event.getPacket();
            Entity en = this.mc.world.getEntityByID(l_Packet.getEntityId());
            if (en == this.Riding) {
                int[] var4 = l_Packet.getPassengerIds();
                var5 = var4.length;

                for(l_EntityId = 0; l_EntityId < var5; ++l_EntityId) {
                    int i = var4[l_EntityId];
                    Entity ent = this.mc.world.getEntityByID(i);
                    if (ent == this.mc.player) {
                        return;
                    }
                }

                this.SendMessage("You dismounted. RIP");
                this.toggle();
            }
        } else if (p_Event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities l_Packetx = (SPacketDestroyEntities)p_Event.getPacket();
            int[] var10 = l_Packetx.getEntityIDs();
            int var11 = var10.length;

            for(var5 = 0; var5 < var11; ++var5) {
                l_EntityId = var10[var5];
                if (l_EntityId == this.Riding.getEntityId()) {
                    this.SendMessage("Entity is now null SPacketDestroyEntities");
                    return;
                }
            }
        }

    }, new Predicate[0]);
    @EventHandler
    private Listener<EntityJoinWorldEvent> OnWorldEvent = new Listener((p_Event) -> {
        if (p_Event.getEntity() == this.mc.player) {
            this.SendMessage("Joined world event!");
        }

    }, new Predicate[0]);

    public VanishDismountModule() {
        super("VanishDismount", new String[]{"VD"}, "Vanish dismounts from entity", "NONE", 14396196, Module.ModuleType.DUPE);
    }

    public void toggleNoSave() {
    }

    public void onEnable() {
        super.onEnable();
        if (this.mc.player == null) {
            this.Riding = null;
            this.toggle();
        } else if (!this.mc.player.isRiding()) {
            this.SendMessage("You are not riding an entity.");
            this.Riding = null;
            this.toggle();
        } else {
            this.Riding = this.mc.player.getRidingEntity();
            this.mc.player.dismountRidingEntity();
            this.mc.world.removeEntity(this.Riding);
        }
    }

    public void onDisable() {
        super.onDisable();
        if (this.Riding != null) {
            this.Riding.isDead = false;
            if (!this.mc.player.isRiding()) {
                this.mc.world.spawnEntity(this.Riding);
                this.mc.player.startRiding(this.Riding, true);
            }

            this.Riding = null;
            this.SendMessage("Forced a remount.");
        }

    }
}
