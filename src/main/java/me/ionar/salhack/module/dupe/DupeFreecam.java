package me.ionar.salhack.module.dupe;

import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.module.Module;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class DupeFreecam extends Module {
    private static Vec3d pos;
    private static Vec2f pitchyaw;
    private static boolean isRidingEntity;
    public static boolean enabled;
    private static Entity ridingEntity;
    private static EntityOtherPlayerMP originalPlayer;
    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener((p_Event) -> {
        this.mc.addScheduledTask(() -> {
            if (this.mc.player != null && this.mc.player.capabilities != null) {
                this.mc.player.capabilities.allowFlying = true;
                this.mc.player.capabilities.isFlying = true;
            }
        });
        this.mc.player.capabilities.setFlySpeed(0.5F);
        this.mc.player.noClip = true;
        this.mc.player.onGround = false;
        this.mc.player.fallDistance = 0.0F;
        if (!this.mc.gameSettings.keyBindForward.isPressed() && !this.mc.gameSettings.keyBindBack.isPressed() && !this.mc.gameSettings.keyBindLeft.isPressed() && !this.mc.gameSettings.keyBindRight.isPressed() && !this.mc.gameSettings.keyBindJump.isPressed() && !this.mc.gameSettings.keyBindSneak.isPressed()) {
            this.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        }

    }, new Predicate[0]);
    private List<CPacketPlayer> PacketsToIgnore = new ArrayList();

    public DupeFreecam() {
        super("DupeFreecam", new String[]{"DupeFreecam"}, "DupeFreecam", "NONE", 14361655, Module.ModuleType.DUPE);
    }

    public void onEnable() {
        if (this.mc.player != null && this.mc.world != null) {
            super.onEnable();
            enabled = true;
            if (isRidingEntity = this.mc.player.isRiding()) {
                ridingEntity = this.mc.player.getRidingEntity();
                this.mc.player.dismountRidingEntity();
            }

            pos = this.mc.player.getPositionVector();
            pitchyaw = this.mc.player.getPitchYaw();
            originalPlayer = new EntityOtherPlayerMP(this.mc.world, this.mc.getSession().getProfile());
            originalPlayer.copyLocationAndAnglesFrom(this.mc.player);
            originalPlayer.rotationYawHead = this.mc.player.rotationYawHead;
            originalPlayer.inventory = this.mc.player.inventory;
            originalPlayer.inventoryContainer = this.mc.player.inventoryContainer;
            this.mc.world.addEntityToWorld(-100, originalPlayer);
            if (isRidingEntity) {
                originalPlayer.startRiding(ridingEntity, true);
            }

        }
    }

    public void onDisable() {
        super.onDisable();
        this.PacketsToIgnore.clear();
        this.mc.addScheduledTask(() -> {
            EntityPlayerSP entityPlayerSP = this.mc.player;
            if (entityPlayerSP != null && entityPlayerSP.capabilities != null) {
                PlayerCapabilities gmCaps = new PlayerCapabilities();
                this.mc.playerController.getCurrentGameType().configurePlayerCapabilities(gmCaps);
                PlayerCapabilities capabilities = entityPlayerSP.capabilities;
                capabilities.allowFlying = gmCaps.allowFlying;
                capabilities.isFlying = gmCaps.allowFlying && capabilities.isFlying;
                capabilities.setFlySpeed(gmCaps.getFlySpeed());
            }
        });
        if (this.mc.player != null && originalPlayer != null) {
            enabled = false;
            originalPlayer.dismountRidingEntity();
            this.mc.world.removeEntityFromWorld(-100);
            originalPlayer = null;
            this.mc.player.noClip = false;
            this.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            if (isRidingEntity) {
                this.mc.player.startRiding(ridingEntity, true);
                ridingEntity = null;
                isRidingEntity = false;
            }

        }
    }

    @SubscribeEvent
    public void onWorldLoad(Load event) {
        if (enabled && originalPlayer != null && this.mc.player != null) {
            pos = this.mc.player.getPositionVector();
        }
    }

    @SubscribeEvent
    public void onEntityRender(Pre<?> event) {
        if (originalPlayer != null && this.mc.player != null && this.mc.player.equals(event.getEntity())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onRenderTag(net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre<?> event) {
        if (originalPlayer != null && this.mc.player != null && this.mc.player.equals(event.getEntity())) {
            event.setCanceled(true);
        }

    }

    static {
        pos = Vec3d.ZERO;
        pitchyaw = Vec2f.ZERO;
        enabled = false;
    }
}
