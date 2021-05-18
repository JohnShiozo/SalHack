package me.ionar.salhack.module.combat;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.entity.EntityUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWebModule extends Module {
    public static final Value<Boolean> rotate = new Value("Rotate", new String[]{"R"}, "Rotate when placing.", true);
    public static final Value<Boolean> spoofRotations = new Value("SpoofRotations", new String[]{"SR"}, "Spoof rotations.", true);
    public static final Value<Boolean> spoofHotbar = new Value("SpoofHotbar", new String[]{"SH"}, "Spoof the hotbar.", false);
    public static final Value<Integer> range = new Value("Range", new String[]{"RNG"}, "Range of block placement.", 5, 0, 10, 1);
    public static final Value<Integer> bpt = new Value("BlocksPerTick", new String[]{"BPT"}, "Blocks placed per tick.", 8, 1, 15, 1);
    private final Vec3d[] offsetList = new Vec3d[]{new Vec3d(0.0D, 1.0D, 0.0D), new Vec3d(0.0D, 0.0D, 0.0D)};
    private boolean slowModeSwitch = false;
    private int playerHotbarSlot = -1;
    private EntityPlayer closestTarget;
    private int lastHotbarSlot = -1;
    private int offsetStep = 0;
    int blocksPlaced;
    @EventHandler
    private Listener<EventClientTick> onUpdate = new Listener((event) -> {
        if (this.closestTarget != null) {
            if (this.slowModeSwitch) {
                this.slowModeSwitch = false;
            } else {
                for(int i = 0; i < (int)Math.floor(Double.valueOf(((Integer)bpt.getValue()).doubleValue())); ++i) {
                    if (this.offsetStep >= this.offsetList.length) {
                        this.endLoop();
                        return;
                    }

                    Vec3d offset = this.offsetList[this.offsetStep];
                    this.placeBlock((new BlockPos(this.closestTarget.getPositionVector())).down().add(offset.x, offset.y, offset.z));
                    ++this.offsetStep;
                }

                this.slowModeSwitch = true;
            }
        }
    }, new Predicate[0]);

    public AutoWebModule() {
        super("AutoWeb", new String[]{"AW"}, "Traps players with cobweb.", "NONE", 2411227, Module.ModuleType.COMBAT);
    }

    public String getMetaData() {
        return this.closestTarget != null ? this.closestTarget.getName() : null;
    }

    private void placeBlock(BlockPos blockPos) {
        if (this.mc.player.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            if (BlockInteractionHelper.checkForNeighbours(blockPos)) {
                this.placeBlockExecute(blockPos);
            }
        }
    }

    public void placeBlockExecute(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(this.mc.player.posX, this.mc.player.posY + (double)this.mc.player.getEyeHeight(), this.mc.player.posZ);
        EnumFacing[] var3 = EnumFacing.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing side = var3[var5];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (BlockInteractionHelper.canBeClicked(neighbor)) {
                Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
                if (!(eyesPos.squareDistanceTo(hitVec) > 18.0625D)) {
                    if ((Boolean)spoofRotations.getValue()) {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }

                    boolean needSneak = false;
                    Block blockBelow = this.mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(blockBelow) || BlockInteractionHelper.shulkerList.contains(blockBelow)) {
                        needSneak = true;
                    }

                    if (needSneak) {
                        this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, Action.START_SNEAKING));
                    }

                    int obiSlot = this.findObiInHotbar();
                    if (obiSlot == -1) {
                        if (this.isEnabled()) {
                            this.toggle();
                        }

                        return;
                    }

                    if (this.lastHotbarSlot != obiSlot) {
                        if ((Boolean)spoofHotbar.getValue()) {
                            this.mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                        } else {
                            this.mc.player.inventory.currentItem = obiSlot;
                        }

                        this.lastHotbarSlot = obiSlot;
                    }

                    this.mc.playerController.processRightClickBlock(this.mc.player, this.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    this.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (needSneak) {
                        this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, Action.STOP_SNEAKING));
                    }

                    return;
                }
            }
        }

    }

    private int findObiInHotbar() {
        int slot = -1;

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = this.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockWeb) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }

    private void findTarget() {
        List<EntityPlayer> playerList = this.mc.player.world.playerEntities;
        Iterator var2 = playerList.iterator();

        while(var2.hasNext()) {
            EntityPlayer target = (EntityPlayer)var2.next();
            if (target != this.mc.player && !FriendManager.Get().IsFriend(target.getName()) && EntityUtil.isLiving(target) && !(target.getHealth() <= 0.0F)) {
                double currentDistance = (double)this.mc.player.getDistance(target);
                if (!(currentDistance > Double.valueOf(((Integer)range.getValue()).doubleValue()))) {
                    if (this.closestTarget == null) {
                        this.closestTarget = target;
                    } else if (!(currentDistance >= (double)this.mc.player.getDistance(this.closestTarget))) {
                        this.closestTarget = target;
                    }
                }
            }
        }

    }

    private void endLoop() {
        this.offsetStep = 0;
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            this.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            this.mc.player.inventory.currentItem = this.playerHotbarSlot;
            this.lastHotbarSlot = this.playerHotbarSlot;
        }

        this.findTarget();
    }

    public void onEnable() {
        if (this.mc.player == null && this.isEnabled()) {
            this.toggle();
        } else {
            this.playerHotbarSlot = this.mc.player.inventory.currentItem;
            this.lastHotbarSlot = -1;
            this.findTarget();
        }
    }

    public void onDisable() {
        if (this.mc.player != null) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                if ((Boolean)spoofHotbar.getValue()) {
                    this.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
                } else {
                    this.mc.player.inventory.currentItem = this.playerHotbarSlot;
                }
            }

            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
        }
    }
}
