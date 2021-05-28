package me.ionar.salhack.module.movement;

import java.util.function.Predicate;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.events.packet.PacketEvent;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatFly extends Module {
    public final Value<Double> speed = new Value("Speed", new String[]{""}, "", 3.0D, 1.0D, 30.0D, 1.0D);
    public final Value<Double> verticalSpeed = new Value("VerticalSpeed", new String[]{""}, "", 3.0D, 1.0D, 10.0D, 1.0D);
    public final Value<Boolean> noKick = new Value("No-Kick", new String[]{""}, "", true);
    public final Value<Boolean> packet = new Value("Packet", new String[]{""}, "", true);
    public final Value<Integer> packets = new Value("Packets", new String[]{""}, "", 3, 1, 5, 1);
    public Value<Integer> interact = new Value("Delay", new String[]{""}, "", 2, 1, 20, 1);
    public static BoatFly INSTANCE;
    private EntityBoat target;
    private int teleportID;
    @EventHandler
    private Listener<PacketEvent.Send> HorseNoFall = new Listener((event) -> {
        if (event.getPacket() instanceof EntityHorse) {
            ((EntityHorse)event.getPacket()).onGround = true;
        }

    }, new Predicate[0]);
    @EventHandler
    private Listener<EventClientTick> OnTick = new Listener((event) -> {
        if (Wrapper.GetMC().player != null) {
            if (Wrapper.GetMC().world != null && Wrapper.GetMC().player.getRidingEntity() != null) {
                if (Wrapper.GetMC().player.getRidingEntity() instanceof EntityBoat) {
                    this.target = (EntityBoat)Wrapper.GetMC().player.getRidingEntity();
                }

                Wrapper.GetMC().player.getRidingEntity().setNoGravity(true);
                Wrapper.GetMC().player.getRidingEntity().motionY = 0.0D;
                if (Wrapper.GetMC().gameSettings.keyBindJump.isKeyDown()) {
                    Wrapper.GetMC().player.getRidingEntity().onGround = false;
                    Wrapper.GetMC().player.getRidingEntity().motionY = (Double)this.verticalSpeed.getValue() / 10.0D;
                }

                if (Wrapper.GetMC().gameSettings.keyBindSprint.isKeyDown()) {
                    Wrapper.GetMC().player.getRidingEntity().onGround = false;
                    Wrapper.GetMC().player.getRidingEntity().motionY = -((Double)this.verticalSpeed.getValue() / 10.0D);
                }

                double[] normalDir = this.directionSpeed((Double)this.speed.getValue() / 2.0D);
                if (Wrapper.GetMC().player.movementInput.moveStrafe == 0.0F && Wrapper.GetMC().player.movementInput.moveForward == 0.0F) {
                    Wrapper.GetMC().player.getRidingEntity().motionX = 0.0D;
                    Wrapper.GetMC().player.getRidingEntity().motionZ = 0.0D;
                } else {
                    Wrapper.GetMC().player.getRidingEntity().motionX = normalDir[0];
                    Wrapper.GetMC().player.getRidingEntity().motionZ = normalDir[1];
                }

                if ((Boolean)this.noKick.getValue()) {
                    if (Wrapper.GetMC().gameSettings.keyBindJump.isKeyDown()) {
                        if (Wrapper.GetMC().player.ticksExisted % 8 < 2) {
                            Wrapper.GetMC().player.getRidingEntity().motionY = -0.03999999910593033D;
                        }
                    } else if (Wrapper.GetMC().player.ticksExisted % 8 < 4) {
                        Wrapper.GetMC().player.getRidingEntity().motionY = -0.07999999821186066D;
                    }
                }

                this.handlePackets(Wrapper.GetMC().player.getRidingEntity().motionX, Wrapper.GetMC().player.getRidingEntity().motionY, Wrapper.GetMC().player.getRidingEntity().motionZ);
            }
        }
    }, new Predicate[0]);

    public BoatFly() {
        super("BoatFly", new String[]{"BoatFly"}, "Allows you to fly around in a boat", "NONE", 2400219, Module.ModuleType.MOVEMENT);
    }

    public void handlePackets(double x, double y, double z) {
        if ((Boolean)this.packet.getValue()) {
            Vec3d vec = new Vec3d(x, y, z);
            if (Wrapper.GetMC().player.getRidingEntity() == null) {
                return;
            }

            Vec3d position = Wrapper.GetMC().player.getRidingEntity().getPositionVector().add(vec);
            Wrapper.GetMC().player.getRidingEntity().setPosition(position.x, position.y, position.z);
            Wrapper.GetMC().player.connection.sendPacket(new CPacketVehicleMove(Wrapper.GetMC().player.getRidingEntity()));

            for(int i = 0; i < (Integer)this.packets.getValue(); ++i) {
                Wrapper.GetMC().player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportID++));
            }
        }

    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketVehicleMove && Wrapper.GetMC().player.isRiding() && Wrapper.GetMC().player.ticksExisted % (Integer)this.interact.getValue() == 0) {
            Wrapper.GetMC().playerController.interactWithEntity(Wrapper.GetMC().player, Wrapper.GetMC().player.getRidingEntity(), EnumHand.OFF_HAND);
        }

        if ((event.getPacket() instanceof Rotation || event.getPacket() instanceof CPacketInput) && Wrapper.GetMC().player.isRiding()) {
            event.cancel();
        }

    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && Wrapper.GetMC().player.isRiding()) {
            event.cancel();
        }

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).getTeleportId();
        }

    }

    private double[] directionSpeed(double speed) {
        float forward = Wrapper.GetMC().player.movementInput.moveForward;
        float side = Wrapper.GetMC().player.movementInput.moveStrafe;
        float yaw = Wrapper.GetMC().player.prevRotationYaw + (Wrapper.GetMC().player.rotationYaw - Wrapper.GetMC().player.prevRotationYaw) * this.mc.getRenderPartialTicks();
        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float)(forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float)(forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }
}
