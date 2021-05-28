package me.ionar.salhack.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.function.Predicate;
import me.ionar.salhack.events.player.EventPlayerTravel;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.RotationSpoof;
import me.ionar.salhack.util.Timer;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;

public final class ElytraPlusModule extends Module {
    public final Value<Boolean> InstantFly = new Value("InstantFly", new String[]{"InstantFly"}, "Sends the use elytra packet when your off ground", true);
    public final Value<Float> downSpeedBoost = new Value("DownSpeed", new String[]{""}, "", 0.4F, 0.0F, 2.0F, 0.1F);
    public final Value<Float> Speed = new Value("Speed", new String[]{""}, "", 1.2F, 0.0F, 3.0F, 0.1F);
    public final Value<Float> GlideSpeed = new Value("GlideSpeed", new String[]{""}, "", 1.0F, 0.0F, 50.0F, 1.0F);
    public RotationSpoof m_RotationSpoof = null;
    private Timer InstantFlyTimer = new Timer();
    @EventHandler
    private Listener<EventPlayerTravel> OnTravel = new Listener<>((p_Event) -> {
        if (this.mc.player != null) {
            if (!this.mc.player.isElytraFlying() && (Boolean)this.InstantFly.getValue()) {
                this.mc.timer.tickLength = 300.0F;
                if (this.mc.player.onGround) {
                    this.mc.player.jump();
                }
                p_event
                if (this.InstantFlyTimer.passed(500.0D)) {
                    this.InstantFlyTimer.reset();
                    this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, Action.START_FALL_FLYING));
                }
            } else if (this.mc.player.movementInput.jump) {
                double l_MotionSq = Math.sqrt(this.mc.player.motionX * this.mc.player.motionX + this.mc.player.motionZ * this.mc.player.motionZ);
                if (!(l_MotionSq > 1.0D)) {
                    double[] dir = MathUtil.directionSpeedNoForward((double)(Float)this.Speed.getValue());
                    this.mc.player.motionX = dir[0];
                    this.mc.player.motionY = -0.01D;
                    this.mc.player.motionZ = dir[1];
                    this.mc.timer.tickLength = 50.0F;
                    p_Event.cancel();
                }
            } else {
                this.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
                this.mc.timer.tickLength = 50.0F;
                p_Event.cancel();
                double[] dirx = MathUtil.directionSpeed((double)(Float)this.Speed.getValue());
                if (this.mc.player.movementInput.moveStrafe != 0.0F || this.mc.player.movementInput.moveForward != 0.0F) {
                    this.mc.player.motionX = dirx[0];
                    this.mc.player.motionY = 0.0D;
                    this.mc.player.motionZ = dirx[1];
                }

                if (this.mc.player.movementInput.sneak) {
                    this.mc.player.motionY = (double)(-(Float)this.downSpeedBoost.getValue());
                }

                this.mc.player.prevLimbSwingAmount = 0.0F;
                this.mc.player.limbSwingAmount = 0.0F;
                this.mc.player.limbSwing = 0.0F;
            }
        }
    }, new Predicate[0]);

    public ElytraPlusModule() {
        super("ElytraFly+", new String[]{"ElytraFly+"}, "Allows you to fly", "NONE", 2415422, Module.ModuleType.MOVEMENT);
    }

    public void onEnable() {
        super.onEnable();
        if (this.mc.player != null) {
            this.mc.player.capabilities.setFlySpeed(0.915F);
            if (this.mc.player.onGround) {
                this.SendMessage(ChatFormatting.GREEN + "Deploying Elytra..");
            }

        }
    }

    public void onDisable() {
        super.onDisable();
        this.mc.timer.tickLength = 50.0F;
    }
}
