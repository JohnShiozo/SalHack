// Decompiled with: FernFlower
// Class Version: 8
package me.ionar.salhack.util;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class BlockUtil {
    public static final List<Block> blackList;
    public static final List<Block> shulkerList;
    public static List<Block> unSolidBlocks;

    public static List<BlockPos> getBlockSphere(float breakRange, Class<?> clazz) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos(Wrapper.GetMC().player), breakRange, (int)breakRange, false, true, 0).stream().filter((pos) -> {
            return clazz.isInstance(Wrapper.GetMC().world.getBlockState(pos).getBlock());
        }).collect(Collectors.toList()));
        return positions;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        List<EnumFacing> facings = new ArrayList();
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbour = pos.offset(side);
            if (Wrapper.GetMC().world.getBlockState(neighbour).getBlock().canCollideCheck(Wrapper.GetMC().world.getBlockState(neighbour), false)) {
                IBlockState blockState = Wrapper.GetMC().world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }

        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = (EnumFacing)iterator.next();
            return facing;
        } else {
            return null;
        }
    }

    public static EnumFacing getRayTraceFacing(BlockPos pos) {
        RayTraceResult result = Wrapper.GetMC().world.rayTraceBlocks(new Vec3d(Wrapper.GetMC().player.posX, Wrapper.GetMC().player.posY + (double)Wrapper.GetMC().player.getEyeHeight(), Wrapper.GetMC().player.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getX() - 0.5D, (double)pos.getX() + 0.5D));
        return result != null && result.sideHit != null ? result.sideHit : EnumFacing.UP;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = Wrapper.GetMC().world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        } else if (!rayTracePlaceCheck(pos, rayTrace, 0.0F)) {
            return -1;
        } else {
            Iterator var4;
            if (entityCheck) {
                var4 = Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).iterator();

                while(var4.hasNext()) {
                    Entity entity = (Entity)var4.next();
                    if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                        return 1;
                    }
                }
            }

            var4 = getPossibleSides(pos).iterator();

            EnumFacing side;
            do {
                if (!var4.hasNext()) {
                    return 2;
                }

                side = (EnumFacing)var4.next();
            } while(!canBeClicked(pos.offset(side)));

            return 3;
        }
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - (double)pos.getX());
            float f2 = (float)(vec.y - (double)pos.getY());
            float f3 = (float)(vec.z - (double)pos.getZ());
            Wrapper.GetMC().player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        } else {
            Wrapper.GetMC().playerController.processRightClickBlock(Wrapper.GetMC().player, Wrapper.GetMC().world, pos, direction, vec, hand);
        }

        Wrapper.GetMC().player.swingArm(EnumHand.MAIN_HAND);
        Wrapper.GetMC().rightClickDelayTimer = 4;
    }

    public static void rightClickBlockLegit(BlockPos pos, float range, boolean rotate, EnumHand hand, AtomicDouble Yaw, AtomicDouble Pitch, AtomicBoolean rotating) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        Vec3d posVec = (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D);
        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
        EnumFacing[] var11 = EnumFacing.values();
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            EnumFacing side = var11[var13];
            Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
            double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);
            if (distanceSqHitVec <= PhobosMathUtil.square((double)range) && distanceSqHitVec < distanceSqPosVec && Wrapper.GetMC().world.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                if (rotate) {
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    Yaw.set((double)rotations[0]);
                    Pitch.set((double)rotations[1]);
                    rotating.set(true);
                }

                Wrapper.GetMC().playerController.processRightClickBlock(Wrapper.GetMC().player, Wrapper.GetMC().world, pos, side, hitVec, hand);
                Wrapper.GetMC().player.swingArm(hand);
                Wrapper.GetMC().rightClickDelayTimer = 4;
                break;
            }
        }

    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();
            Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
            Block neighbourBlock = Wrapper.GetMC().world.getBlockState(neighbour).getBlock();
            if (!Wrapper.GetMC().player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
                Wrapper.GetMC().player.connection.sendPacket(new CPacketEntityAction(Wrapper.GetMC().player, Action.START_SNEAKING));
                Wrapper.GetMC().player.setSneaking(true);
                sneaking = true;
            }

            if (rotate) {
                RotationUtil.faceVector(hitVec, true);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            Wrapper.GetMC().player.swingArm(EnumHand.MAIN_HAND);
            Wrapper.GetMC().rightClickDelayTimer = 4;
            return sneaking || isSneaking;
        }
    }

    public static boolean placeBlockSmartRotate(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();
            Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
            Block neighbourBlock = Wrapper.GetMC().world.getBlockState(neighbour).getBlock();
            if (!Wrapper.GetMC().player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
                Wrapper.GetMC().player.connection.sendPacket(new CPacketEntityAction(Wrapper.GetMC().player, Action.START_SNEAKING));
                sneaking = true;
            }

            if (rotate) {
                SalHack.rotationManager.lookAtVec3d(hitVec);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            Wrapper.GetMC().player.swingArm(EnumHand.MAIN_HAND);
            Wrapper.GetMC().rightClickDelayTimer = 4;
            return sneaking || isSneaking;
        }
    }

    public static void placeBlockStopSneaking(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = placeBlockSmartRotate(pos, hand, rotate, packet, isSneaking);
        if (!isSneaking && sneaking) {
            Wrapper.GetMC().player.connection.sendPacket(new CPacketEntityAction(Wrapper.GetMC().player, Action.STOP_SNEAKING));
        }

    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.x, vec3d.y - 1.0D, vec3d.z), new Vec3d(vec3d.x != 0.0D ? vec3d.x * 2.0D : vec3d.x, vec3d.y, vec3d.x != 0.0D ? vec3d.z : vec3d.z * 2.0D), new Vec3d(vec3d.x == 0.0D ? vec3d.x + 1.0D : vec3d.x, vec3d.y, vec3d.x == 0.0D ? vec3d.z : vec3d.z + 1.0D), new Vec3d(vec3d.x == 0.0D ? vec3d.x - 1.0D : vec3d.x, vec3d.y, vec3d.x == 0.0D ? vec3d.z : vec3d.z - 1.0D), new Vec3d(vec3d.x, vec3d.y + 1.0D, vec3d.z)};
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
            for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
                for(int y = sphere ? cy - (int)r : cy; (float)y < (sphere ? (float)cy + r : (float)(cy + h)); ++y) {
                    double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
                    if (dist < (double)(r * r) && (!hollow || dist >= (double)((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        try {
            return (Wrapper.GetMC().world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || Wrapper.GetMC().world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && Wrapper.GetMC().world.getBlockState(boost).getBlock() == Blocks.AIR && Wrapper.GetMC().world.getBlockState(boost2).getBlock() == Blocks.AIR && Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        } catch (Exception var4) {
            return false;
        }
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll((Collection)getSphere(EntityUtil.getPlayerPos(Wrapper.GetMC().player), placeRange, (int)placeRange, false, true, 0).stream().filter((pos) -> {
            return canPlaceCrystal(pos, specialEntityCheck);
        }).collect(Collectors.toList()));
        return positions;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        try {
            if (Wrapper.GetMC().world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && Wrapper.GetMC().world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            } else if (Wrapper.GetMC().world.getBlockState(boost).getBlock() == Blocks.AIR && Wrapper.GetMC().world.getBlockState(boost2).getBlock() == Blocks.AIR) {
                if (specialEntityCheck) {
                    Iterator var4 = Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).iterator();

                    Entity entity;
                    do {
                        if (!var4.hasNext()) {
                            var4 = Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).iterator();

                            do {
                                if (!var4.hasNext()) {
                                    return true;
                                }

                                entity = (Entity)var4.next();
                            } while(entity instanceof EntityEnderCrystal);

                            return false;
                        }

                        entity = (Entity)var4.next();
                    } while(entity instanceof EntityEnderCrystal);

                    return false;
                } else {
                    return Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && Wrapper.GetMC().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
            } else {
                return false;
            }
        } catch (Exception var6) {
            return false;
        }
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return Wrapper.GetMC().world.getBlockState(pos);
    }

    public static boolean isBlockAboveEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY + 2.0D, entity.posZ);
            return isBlockSolid(pos);
        } else {
            return false;
        }
    }

    public static void debugPos(String message, BlockPos pos) {
        SalHack.SendMessage(message + pos.getX() + "x, " + pos.getY() + "y, " + pos.getZ() + "z");
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand) {
        RayTraceResult result = Wrapper.GetMC().world.rayTraceBlocks(new Vec3d(Wrapper.GetMC().player.posX, Wrapper.GetMC().player.posY + (double)Wrapper.GetMC().player.getEyeHeight(), Wrapper.GetMC().player.posZ), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() - 0.5D, (double)pos.getZ() + 0.5D));
        EnumFacing facing = result != null && result.sideHit != null ? result.sideHit : EnumFacing.UP;
        Wrapper.GetMC().player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];

        for(int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }

        return list;
    }

    public static Vec3d posToVec3d(BlockPos pos) {
        return new Vec3d(pos);
    }

    public static BlockPos vec3dToPos(Vec3d vec3d) {
        return new BlockPos(vec3d);
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && (double)pos.getZ() - Wrapper.GetMC().player.getPositionVector().z < 0.0D) {
            return false;
        } else if (dirnumber == 1 && (double)pos.getX() - Wrapper.GetMC().player.getPositionVector().x > 0.0D) {
            return false;
        } else {
            return dirnumber == 2 && (double)pos.getZ() - Wrapper.GetMC().player.getPositionVector().z > 0.0D ? false : dirnumber != 3 || (double)pos.getX() - Wrapper.GetMC().player.getPositionVector().x >= 0.0D;
        }
    }

    public static boolean isBlockBelowEntitySolid(Entity entity) {
        if (entity != null) {
            BlockPos pos = new BlockPos(entity.posX, entity.posY - 1.0D, entity.posZ);
            return isBlockSolid(pos);
        } else {
            return false;
        }
    }

    public static boolean isBlockSolid(BlockPos pos) {
        return !isBlockUnSolid(pos);
    }

    public static boolean isBlockUnSolid(BlockPos pos) {
        return isBlockUnSolid(Wrapper.GetMC().world.getBlockState(pos).getBlock());
    }

    public static boolean isBlockUnSolid(Block block) {
        return unSolidBlocks.contains(block);
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];

        for(int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }

        return output;
    }

    public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
        return convertVec3ds(entity.getPositionVector(), input);
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = Wrapper.GetMC().world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Wrapper.GetMC().world, pos) != -1.0F;
    }

    public static boolean isValidBlock(BlockPos pos) {
        Block block = Wrapper.GetMC().world.getBlockState(pos).getBlock();
        return !(block instanceof BlockLiquid) && block.getMaterial((IBlockState)null) != Material.AIR;
    }

    public static boolean isScaffoldPos(BlockPos pos) {
        return Wrapper.GetMC().world.isAirBlock(pos) || Wrapper.GetMC().world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER || Wrapper.GetMC().world.getBlockState(pos).getBlock() == Blocks.TALLGRASS || Wrapper.GetMC().world.getBlockState(pos).getBlock() instanceof BlockLiquid;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || Wrapper.GetMC().world.rayTraceBlocks(new Vec3d(Wrapper.GetMC().player.posX, Wrapper.GetMC().player.posY + (double)Wrapper.GetMC().player.getEyeHeight(), Wrapper.GetMC().player.posZ), new Vec3d((double)pos.getX(), (double)((float)pos.getY() + height), (double)pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }

    static {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);
    }
}
