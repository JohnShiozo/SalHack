package me.ionar.salhack.module.movement;

import java.util.function.Predicate;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class Anchor extends Module {
    public final Value<Boolean> Pull = new Value("Pull", new String[]{""}, "", true);
    int holeblocks;
    public static boolean AnchorING;
    private Vec3d Center;
    @EventHandler
    private Listener<EventClientTick> OnTick;

    public Anchor() {
        super("Anchor", new String[]{"Anchor"}, "Stops all movement if player is above a hole", "NONE", 12723419, Module.ModuleType.MOVEMENT);
        this.Center = Vec3d.ZERO;
        this.OnTick = new Listener((event) -> {
            if (!this.isBlockHole(this.getPlayerPos().down(1)) && !this.isBlockHole(this.getPlayerPos().down(2)) && !this.isBlockHole(this.getPlayerPos().down(3)) && !this.isBlockHole(this.getPlayerPos().down(4))) {
                AnchorING = false;
            } else {
                AnchorING = true;
                if (!(Boolean)this.Pull.getValue()) {
                    this.mc.player.motionX = 0.0D;
                    this.mc.player.motionZ = 0.0D;
                } else {
                    this.Center = this.GetCenter(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ);
                    double XDiff = Math.abs(this.Center.x - this.mc.player.posX);
                    double ZDiff = Math.abs(this.Center.z - this.mc.player.posZ);
                    if (XDiff <= 0.1D && ZDiff <= 0.1D) {
                        this.Center = Vec3d.ZERO;
                    } else {
                        double MotionX = this.Center.x - this.mc.player.posX;
                        double MotionZ = this.Center.z - this.mc.player.posZ;
                        this.mc.player.motionX = MotionX / 2.0D;
                        this.mc.player.motionZ = MotionZ / 2.0D;
                    }
                }
            }

        }, new Predicate[0]);
    }

    public boolean isBlockHole(BlockPos blockpos) {
        this.holeblocks = 0;
        if (this.mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || this.mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || this.mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || this.mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || this.mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }

        if (this.mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || this.mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
            ++this.holeblocks;
        }

        return this.holeblocks >= 9;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;
        return new Vec3d(x, y, z);
    }

    public void onDisable() {
        super.onDisable();
        AnchorING = false;
        this.holeblocks = 0;
    }

    public void onEnable() {
        super.onEnable();
        if (this.mc.player == null) {
            this.toggle();
        }
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(this.mc.player.posX), Math.floor(this.mc.player.posY), Math.floor(this.mc.player.posZ));
    }
}
