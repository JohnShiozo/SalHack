package me.ionar.salhack.module.movement;

import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityLlama;

public class EntitySpeedModule extends Module {

    public EntitySpeedModule() {
        super("EntitySpeed", new String[] {""}, "Move fast on entities.", "NONE", -1, ModuleType.MOVEMENT);
    }

    public static final Value<Double> Speed = new Value<Double>("Speed", new String[] {""}, "Speed your entity moves.", 1.0,1.0,5.0,0.1);

    @EventHandler
    private final Listener<EventPlayerUpdate> listener = new Listener<>(event -> {
        if (mc.player == null || mc.player.getRidingEntity() == null) return;

        Entity ridingEntity = mc.player.getRidingEntity();

        if (ridingEntity instanceof EntityLlama) {
            ridingEntity.rotationYaw = mc.player.rotationYaw;
            ((EntityLlama) ridingEntity).rotationYawHead = mc.player.rotationYawHead;
        }

        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward == 0.0 && strafe == 0.0) {
            ridingEntity.motionX = 0.0;
            ridingEntity.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            ridingEntity.motionX = forward * Speed.getValue() * cos + strafe * Speed.getValue() * sin;
            ridingEntity.motionZ = forward * Speed.getValue() * sin - strafe * Speed.getValue() * cos;
            if (ridingEntity instanceof EntityMinecart) {
                EntityMinecart entityMinecart = (EntityMinecart) ridingEntity;
                entityMinecart.setVelocity(forward * Speed.getValue() * cos + strafe * Speed.getValue() * sin, entityMinecart.motionY, forward * Speed.getValue() * sin - strafe * Speed.getValue() * cos);
            }

        }
    });

}