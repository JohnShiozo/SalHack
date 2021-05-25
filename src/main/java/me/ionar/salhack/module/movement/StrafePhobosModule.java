package me.ionar.salhack.module.movement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.MinecraftEvent;
import me.ionar.salhack.events.player.EventPlayerMotionUpdate;
import me.ionar.salhack.events.player.EventPlayerMove;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.EntityUtil;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StrafePhobosModule extends Module {
    public static final Value<StrafePhobosModule.Mode> mode;
    public static final Value<Boolean> limiter;
    public static final Value<Boolean> limiter2;
    public static final Value<Integer> specialMoveSpeed;
    public static final Value<Integer> potionSpeed;
    public static final Value<Integer> potionSpeed2;
    public static final Value<Integer> acceleration;
    public static final Value<Boolean> potion;
    public static final Value<Boolean> step;
    private int stage = 1;
    private double moveSpeed;
    private double lastDist;
    private int cooldownHops = 0;

    public StrafePhobosModule() {
        super("StrafePhobos", new String[]{"strafephobos"}, "Phobos Strafe ported to Salhack(doesnt work atm)", "NONE", -1, Module.ModuleType.MOVEMENT);
    }

    public void onEnable() {
        this.moveSpeed = getBaseMoveSpeed();
        SalHackMod.EVENT_BUS.subscribe((Listenable)this);
    }

    public void onDisable() {
        this.moveSpeed = 0.0D;
        this.stage = 2;
        SalHackMod.EVENT_BUS.unsubscribe((Listenable)this);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(EventPlayerMotionUpdate event) {
        if (this.isEnabled()) {
            if (event.getEra() == MinecraftEvent.Era.POST) {
                this.lastDist = Math.sqrt((Wrapper.GetMC().player.posX - Wrapper.GetMC().player.prevPosX) * (Wrapper.GetMC().player.posX - Wrapper.GetMC().player.prevPosX) + (Wrapper.GetMC().player.posZ - Wrapper.GetMC().player.prevPosZ) * (Wrapper.GetMC().player.posZ - Wrapper.GetMC().player.prevPosZ));
            }
        }
    }

    @SubscribeEvent
    public void onMove(EventPlayerMove event) {
        if (this.isEnabled()) {
            if (mode.getValue() == StrafePhobosModule.Mode.NCP) {
                this.doNCP(event);
            } else if (mode.getValue() == StrafePhobosModule.Mode.BHOP) {
                float moveForward = Wrapper.GetMC().player.movementInput.moveForward;
                float moveStrafe = Wrapper.GetMC().player.movementInput.moveStrafe;
                float rotationYaw = Wrapper.GetMC().player.rotationYaw;
                if ((Boolean)limiter2.getValue() && Wrapper.GetMC().player.onGround) {
                    this.stage = 2;
                }

                if ((Boolean)limiter.getValue() && round(Wrapper.GetMC().player.posY - (double)((int)Wrapper.GetMC().player.posY), 3) == round(0.138D, 3)) {
                    EntityPlayerSP player = Wrapper.GetMC().player;
                    player.motionY -= 0.13D;
                    event.Y -= 0.13D;
                    EntityPlayerSP player2 = Wrapper.GetMC().player;
                    player2.posY -= 0.13D;
                }

                double motionX;
                if (this.stage == 1 && EntityUtil.isMoving()) {
                    this.stage = 2;
                    this.moveSpeed = (double)this.getMultiplier() * getBaseMoveSpeed() - 0.01D;
                } else if (this.stage == 2) {
                    this.stage = 3;
                    if (EntityUtil.isMoving()) {
                        event.Y = Wrapper.GetMC().player.motionY = 0.4D;
                        if (this.cooldownHops > 0) {
                            --this.cooldownHops;
                        }

                        this.moveSpeed *= (double)(Integer)acceleration.getValue() / 1000.0D;
                    }
                } else if (this.stage == 3) {
                    this.stage = 4;
                    motionX = 0.66D * (this.lastDist - getBaseMoveSpeed());
                    this.moveSpeed = this.lastDist - motionX;
                } else {
                    if (Wrapper.GetMC().world.getCollisionBoxes(Wrapper.GetMC().player, Wrapper.GetMC().player.getEntityBoundingBox().offset(0.0D, Wrapper.GetMC().player.motionY, 0.0D)).size() > 0 || Wrapper.GetMC().player.collidedVertically) {
                        this.stage = 1;
                    }

                    this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
                }

                this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
                if (moveForward == 0.0F && moveStrafe == 0.0F) {
                    event.X = 0.0D;
                    event.Z = 0.0D;
                    this.moveSpeed = 0.0D;
                } else if (moveForward != 0.0F) {
                    if (moveStrafe >= 1.0F) {
                        rotationYaw += moveForward > 0.0F ? -45.0F : 45.0F;
                        moveStrafe = 0.0F;
                    } else if (moveStrafe <= -1.0F) {
                        rotationYaw += moveForward > 0.0F ? 45.0F : -45.0F;
                        moveStrafe = 0.0F;
                    }

                    if (moveForward > 0.0F) {
                        moveForward = 1.0F;
                    } else if (moveForward < 0.0F) {
                        moveForward = -1.0F;
                    }
                }

                motionX = Math.cos(Math.toRadians((double)(rotationYaw + 90.0F)));
                double motionZ = Math.sin(Math.toRadians((double)(rotationYaw + 90.0F)));
                if (this.cooldownHops == 0) {
                    event.X = (double)moveForward * this.moveSpeed * motionX + (double)moveStrafe * this.moveSpeed * motionZ;
                    event.Z = (double)moveForward * this.moveSpeed * motionZ - (double)moveStrafe * this.moveSpeed * motionX;
                }

                if ((Boolean)step.getValue()) {
                    Wrapper.GetMC().player.stepHeight = 0.6F;
                }

                if (moveForward == 0.0F && moveStrafe == 0.0F) {
                    event.X = 0.0D;
                    event.Z = 0.0D;
                }
            }

        }
    }

    private void doNCP(EventPlayerMove event) {
        if (this.isEnabled()) {
            if (!(Boolean)limiter.getValue() && Wrapper.GetMC().player.onGround) {
                this.stage = 2;
            }

            double forward;
            switch(this.stage) {
            case 0:
                ++this.stage;
                this.lastDist = 0.0D;
                break;
            case 1:
            default:
                if (((Boolean)limiter2.getValue() && Wrapper.GetMC().world.getCollisionBoxes(Wrapper.GetMC().player, Wrapper.GetMC().player.getEntityBoundingBox().offset(0.0D, Wrapper.GetMC().player.motionY, 0.0D)).size() > 0 || Wrapper.GetMC().player.collidedVertically) && this.stage > 0) {
                    this.stage = Wrapper.GetMC().player.moveForward == 0.0F && Wrapper.GetMC().player.moveStrafing == 0.0F ? 0 : 1;
                }

                this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
                break;
            case 2:
                forward = 0.40123128D;
                if ((Wrapper.GetMC().player.moveForward != 0.0F || Wrapper.GetMC().player.moveStrafing != 0.0F) && Wrapper.GetMC().player.onGround) {
                    if (Wrapper.GetMC().player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        forward += (double)((float)(Wrapper.GetMC().player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                    }

                    event.Y = Wrapper.GetMC().player.motionY = forward;
                    this.moveSpeed *= 2.149D;
                }
                break;
            case 3:
                this.moveSpeed = this.lastDist - 0.76D * (this.lastDist - getBaseMoveSpeed());
            }

            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            forward = (double)Wrapper.GetMC().player.movementInput.moveForward;
            double strafe = (double)Wrapper.GetMC().player.movementInput.moveStrafe;
            double yaw = (double)Wrapper.GetMC().player.rotationYaw;
            if (forward == 0.0D && strafe == 0.0D) {
                event.X = 0.0D;
                event.Z = 0.0D;
            } else if (forward != 0.0D && strafe != 0.0D) {
                forward *= Math.sin(0.7853981633974483D);
                strafe *= Math.cos(0.7853981633974483D);
            }

            event.X = (forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99D;
            event.Z = (forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99D;
            ++this.stage;
        }
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272D;
        if (Wrapper.GetMC().player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = ((PotionEffect)Objects.requireNonNull(Wrapper.GetMC().player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double)amplifier;
        }

        return baseSpeed;
    }

    private float getMultiplier() {
        float baseSpeed = (float)(Integer)specialMoveSpeed.getValue();
        if ((Boolean)potion.getValue() && Wrapper.GetMC().player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = ((PotionEffect)Objects.requireNonNull(Wrapper.GetMC().player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier() + 1;
            if (amplifier >= 2) {
                baseSpeed = (float)(Integer)potionSpeed2.getValue();
            } else {
                baseSpeed = (float)(Integer)potionSpeed.getValue();
            }
        }

        return baseSpeed / 100.0F;
    }

    public String getMetaData() {
        return mode.getValue() == StrafePhobosModule.Mode.NONE ? null : ((StrafePhobosModule.Mode)mode.getValue()).toString();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bigDecimal = (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP);
            return bigDecimal.doubleValue();
        }
    }

    static {
        mode = new Value("Mode", new String[]{"mode"}, "The current mode for Strafe.", StrafePhobosModule.Mode.NCP);
        limiter = new Value("SetGround", new String[]{"setground"}, "SetGround.", true);
        limiter2 = new Value("Bhop", new String[]{"bhop"}, "Bhop.", false);
        specialMoveSpeed = new Value("Speed", new String[]{"speed"}, "Speed.", 100, 0, 150, 10);
        potionSpeed = new Value("Speed1", new String[]{"speed1"}, "Potion Speed 1.", 130, 0, 150, 10);
        potionSpeed2 = new Value("Speed2", new String[]{"speed2"}, "Potion Speed 2.", 125, 0, 150, 10);
        acceleration = new Value("Acceleration", new String[]{"accel"}, "Acceleration.", 2149, 1000, 2500, 100);
        potion = new Value("Potion", new String[]{"potion"}, "Potion.", false);
        step = new Value("SetStep", new String[]{"setstep"}, "bhop->setstep", true);
    }

    public static enum Mode {
        NONE,
        NCP,
        BHOP;
    }
}
