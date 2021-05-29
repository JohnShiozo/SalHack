package me.ionar.salhack.module.movement;

import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.main.Wrapper;

import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

//thanks ciruu 

public final class InstantBurrow extends Module {

    public final Value<Float> Speed = new Value<Float>("Offset", new String[]
    { "" }, "Offset to use", 7.0f, 0.0f, 20.0F, 7.0f);
    public final Value<Boolean> rotate = new Value<Boolean>("Rotate", false);
    public final Value<Boolean> sneak = new Value<Boolean>("sneak", false);

    private BlockPos originalPos;
    private int oldSlot = -1;

    public InstantBurrow() {
        super("InstantBurrow", new String[]
        { "Burrow" }, "Burrows inside a block.", "NONE", 0xDADB24, ModuleType.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // Save our original pos
        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        // If we can't place in our actual post then toggle and return
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) ||
                intersectsWithEntity(this.originalPos)) {
            toggle();
            return;
        }

        // Save our item slot
        oldSlot = mc.player.inventory.currentItem;
    }

    @EventHandler
    private final Listener<EventClientTick> onTick = new Listener<>(event -> {
        // If we don't have obsidian in hotbar toggle and return
        if (BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            Wrapper.sendMessage("Can't find obsidian in hotbar!");
            toggle();
            return;
        }

        // Change to obsidian slot
        BurrowUtil.switchToSlot(BurrowUtil.findHotbarBlock(BlockObsidian.class), false);

        // Fake jump
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        
        // Sneak option.
        boolean sneaking = mc.player.isSneaking(); 
        if (sneak.getValue()) {
            if (sneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, START_SNEAKING));
            }
        }

        // Place block
        BurrowUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);

        // Rubberband
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));

        // SwitchBack
        BurrowUtil.switchToSlot(oldSlot, false);

        // AutoDisable
        toggle();
    });

    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }
}
