package me.ionar.salhack.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {
    public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0D, -2.0D, 0.0D)};
    public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D)};
    public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)};
    public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D)};
    public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D)};
    public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0D, 3.0D, 0.0D)};

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            Wrapper.GetMC().player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            Wrapper.GetMC().playerController.attackEntity(Wrapper.GetMC().player, entity);
        }

        if (swingArm) {
            Wrapper.GetMC().player.swingArm(EnumHand.MAIN_HAND);
        }

    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(Wrapper.GetMC().getRenderManager().renderPosX, Wrapper.GetMC().getRenderManager().renderPosY, Wrapper.GetMC().getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
        return (new Vec3d(vec.x, vec.y, vec.z)).subtract(Wrapper.GetMC().getRenderManager().renderPosX, Wrapper.GetMC().getRenderManager().renderPosY, Wrapper.GetMC().getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
        return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, (double)partialTicks, (double)partialTicks, (double)partialTicks);
    }

    public static boolean isPassive(Entity entity) {
        return (!(entity instanceof EntityWolf) || !((EntityWolf)entity).isAngry()) && (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid || entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getRevengeTarget() == null);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && Wrapper.GetMC().player != null) {
            Wrapper.GetMC().player.connection.sendPacket(new CPacketEntityAction(Wrapper.GetMC().player, Action.STOP_SNEAKING));
        }

        return false;
    }

    public static boolean isSafe(Entity entity) {
        return isSafe(entity, 0, false);
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie)entity).isArmsRaised() || ((EntityPigZombie)entity).isAngry()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf)entity).isAngry() && !Wrapper.GetMC().player.equals(((EntityWolf)entity).getOwner());
            }

            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman)entity).isScreaming();
            }
        }

        return isHostileMob(entity);
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isFriendlyMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || isNeutralMob(entity) && !isMobAggressive(entity);
    }

    public static boolean isHostileMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> vec3ds = new ArrayList();
        Vec3d[] var4 = getOffsets(height, floor);
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Vec3d vector = var4[var6];
            BlockPos targetPos = (new BlockPos(pos)).add(vector.x, vector.y, vector.z);
            Block block = Wrapper.GetMC().world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static boolean isInHole(Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        BlockPos[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = Wrapper.GetMC().world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        BlockPos[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = Wrapper.GetMC().world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        BlockPos[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BlockPos pos = var2[var4];
            IBlockState touchingState = Wrapper.GetMC().world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }

        return true;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return (Vec3d[])list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return (Vec3d[])list.toArray(array);
    }

    public static double getDst(Vec3d vec) {
        return Wrapper.GetMC().player.getPositionVector().distanceTo(vec);
    }

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }

    public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
    }

    public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        List<Vec3d> vec3ds = new ArrayList();
        if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
            vec3ds.addAll(getUnsafeBlocks(player, 2, false));
        }

        for(int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
            Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
            BlockPos targetPos = (new BlockPos(player.getPositionVector())).add(vector.x, vector.y, vector.z);
            Block block = Wrapper.GetMC().world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }

        return vec3ds;
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null) {
            return false;
        } else {
            double y = entity.posY + 0.01D;

            for(int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
                for(int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                    BlockPos pos = new BlockPos(x, (int)y, z);
                    if (Wrapper.GetMC().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isDrivenByPlayer(Entity entityIn) {
        return Wrapper.GetMC().player != null && entityIn != null && entityIn.equals(Wrapper.GetMC().player.getRidingEntity());
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isAboveWater(Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(Entity entity, boolean packet) {
        if (entity == null) {
            return false;
        } else {
            double y = entity.posY - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));

            for(int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
                for(int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                    BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                    if (Wrapper.GetMC().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        List<Vec3d> placeTargets = new ArrayList();
        Iterator var10;
        Vec3d vec3d;
        if (extension == 1) {
            placeTargets.addAll(targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
        } else {
            int extend = 1;

            for(var10 = PhobosMathUtil.getBlockBlocks(player).iterator(); var10.hasNext(); ++extend) {
                vec3d = (Vec3d)var10.next();
                if (extend > extension) {
                    break;
                }

                placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
            }
        }

        List<Vec3d> removeList = new ArrayList();
        var10 = placeTargets.iterator();

        while(var10.hasNext()) {
            vec3d = (Vec3d)var10.next();
            BlockPos pos = new BlockPos(vec3d);
            if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
                removeList.add(vec3d);
            }
        }

        var10 = removeList.iterator();

        while(var10.hasNext()) {
            vec3d = (Vec3d)var10.next();
            placeTargets.remove(vec3d);
        }

        return placeTargets;
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        List<Vec3d> placeTargets = new ArrayList();
        if (antiDrop) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
        }

        if (platform) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
        }

        if (legs) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
        }

        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
        if (antiStep) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
        } else {
            List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                Iterator var9 = vec3ds.iterator();

                label34:
                while(var9.hasNext()) {
                    Vec3d vector = (Vec3d)var9.next();
                    BlockPos position = (new BlockPos(vec3d)).add(vector.x, vector.y, vector.z);
                    switch(BlockUtil.isPositionPlaceable(position, raytrace)) {
                    case -1:
                    case 1:
                    case 2:
                        break;
                    case 0:
                    default:
                        break label34;
                    case 3:
                        placeTargets.add(vec3d.add(vector));
                        break label34;
                    }
                }
            }
        }

        if (antiScaffold) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }

        return placeTargets;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        List<Vec3d> offsets = new ArrayList();
        offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
        offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
        offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
        offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
        if (floor) {
            offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
        }

        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[])offsets.toArray(array);
    }

    public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[])offsets.toArray(array);
    }

    public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        List<Vec3d> offsets = new ArrayList(getOffsetList(1, false));
        offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
        if (antiScaffold) {
            offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
        }

        if (antiStep) {
            offsets.addAll(getOffsetList(2, false));
        }

        if (legs) {
            offsets.addAll(getOffsetList(0, false));
        }

        if (platform) {
            offsets.addAll(getOffsetList(-1, false));
            offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
        }

        if (antiDrop) {
            offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
        }

        return offsets;
    }

    public static Vec3d[] getHeightOffsets(int min, int max) {
        List<Vec3d> offsets = new ArrayList();

        for(int i = min; i <= max; ++i) {
            offsets.add(new Vec3d(0.0D, (double)i, 0.0D));
        }

        Vec3d[] array = new Vec3d[offsets.size()];
        return (Vec3d[])offsets.toArray(array);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(PhobosMathUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0F;
    }

    public static boolean isDead(Entity entity) {
        return !isAlive(entity);
    }

    public static float getHealth(Entity entity) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        } else {
            return 0.0F;
        }
    }

    public static float getHealth(Entity entity, boolean absorption) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.getHealth() + (absorption ? livingBase.getAbsorptionAmount() : 0.0F);
        } else {
            return 0.0F;
        }
    }

    public static boolean canEntityFeetBeSeen(Entity entityIn) {
        return Wrapper.GetMC().world.rayTraceBlocks(new Vec3d(Wrapper.GetMC().player.posX, Wrapper.GetMC().player.posX + (double)Wrapper.GetMC().player.getEyeHeight(), Wrapper.GetMC().player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || isDead(entity) || entity.equals(Wrapper.GetMC().player) || entity instanceof EntityPlayer && SalHack.GetFriendManager().IsFriend(entity) || Wrapper.GetMC().player.getDistanceSq(entity) > PhobosMathUtil.square(range);
    }

    public static boolean isValid(Entity entity, double range) {
        return !isntValid(entity, range);
    }

    public static boolean holdingWeapon(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873D;
        if (Wrapper.GetMC().player.isPotionActive((Potion)Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0D + 0.2D * (double)(((PotionEffect)Objects.requireNonNull(Wrapper.GetMC().player.getActivePotionEffect((Potion)Objects.requireNonNull(Potion.getPotionById(1))))).getAmplifier() + 1);
        }

        return maxModifier;
    }

    public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
        if (entity != null) {
            entity.motionX *= multiplier;
            entity.motionZ *= multiplier;
        }

    }

    public static boolean isEntityMoving(Entity entity) {
        if (entity == null) {
            return false;
        } else if (entity instanceof EntityPlayer) {
            return Wrapper.GetMC().gameSettings.keyBindForward.isKeyDown() || Wrapper.GetMC().gameSettings.keyBindBack.isKeyDown() || Wrapper.GetMC().gameSettings.keyBindLeft.isKeyDown() || Wrapper.GetMC().gameSettings.keyBindRight.isKeyDown();
        } else {
            return entity.motionX != 0.0D || entity.motionY != 0.0D || entity.motionZ != 0.0D;
        }
    }

    public static double getEntitySpeed(Entity entity) {
        if (entity != null) {
            double distTraveledLastTickX = entity.posX - entity.prevPosX;
            double distTraveledLastTickZ = entity.posZ - entity.prevPosZ;
            double speed = (double)MathHelper.sqrt(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
            return speed * 20.0D;
        } else {
            return 0.0D;
        }
    }

    public static boolean holding32k(EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(ItemStack stack) {
        if (stack == null) {
            return false;
        } else if (stack.getTagCompound() == null) {
            return false;
        } else {
            NBTTagList enchants = (NBTTagList)stack.getTagCompound().getTag("ench");
            if (enchants == null) {
                return false;
            } else {
                for(int i = 0; i < enchants.tagCount(); ++i) {
                    NBTTagCompound enchant = enchants.getCompoundTagAt(i);
                    if (enchant.getInteger("id") == 16) {
                        int lvl = enchant.getInteger("lvl");
                        if (lvl >= 42) {
                            return true;
                        }
                        break;
                    }
                }

                return false;
            }
        }
    }

    public static boolean simpleIs32k(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) >= 1000;
    }

    public static void moveEntityStrafe(double speed, Entity entity) {
        if (entity != null) {
            MovementInput movementInput = Wrapper.GetMC().player.movementInput;
            double forward = (double)movementInput.moveForward;
            double strafe = (double)movementInput.moveStrafe;
            float yaw = Wrapper.GetMC().player.rotationYaw;
            if (forward == 0.0D && strafe == 0.0D) {
                entity.motionX = 0.0D;
                entity.motionZ = 0.0D;
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (float)(forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (float)(forward > 0.0D ? 45 : -45);
                    }

                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }

                entity.motionX = forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)));
                entity.motionZ = forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)));
            }
        }

    }

    public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
        return !shouldCheck || Wrapper.GetMC().player.canEntityBeSeen(entity);
    }

    public static boolean isMoving() {
        return (double)Wrapper.GetMC().player.moveForward != 0.0D || (double)Wrapper.GetMC().player.moveStrafing != 0.0D;
    }

    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        Iterator var3 = Wrapper.GetMC().world.playerEntities.iterator();

        while(var3.hasNext()) {
            EntityPlayer player = (EntityPlayer)var3.next();
            if (!isntValid(player, distance)) {
                if (closest == null) {
                    closest = player;
                } else if (!(Wrapper.GetMC().player.getDistanceSq(player) >= Wrapper.GetMC().player.getDistanceSq(closest))) {
                    closest = player;
                }
            }
        }

        return closest;
    }

    public static boolean checkCollide() {
        return !Wrapper.GetMC().player.isSneaking() && (Wrapper.GetMC().player.getRidingEntity() == null || Wrapper.GetMC().player.getRidingEntity().fallDistance < 3.0F) && Wrapper.GetMC().player.fallDistance < 3.0F;
    }

    public static boolean isInLiquid() {
        if (Wrapper.GetMC().player.fallDistance >= 3.0F) {
            return false;
        } else {
            boolean inLiquid = false;
            AxisAlignedBB bb = Wrapper.GetMC().player.getRidingEntity() != null ? Wrapper.GetMC().player.getRidingEntity().getEntityBoundingBox() : Wrapper.GetMC().player.getEntityBoundingBox();
            int y = (int)bb.minY;

            for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
                for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                    Block block = Wrapper.GetMC().world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        inLiquid = true;
                    }
                }
            }

            return inLiquid;
        }
    }

    public static boolean isOnLiquid(double offset) {
        if (Wrapper.GetMC().player.fallDistance >= 3.0F) {
            return false;
        } else {
            AxisAlignedBB bb = Wrapper.GetMC().player.getRidingEntity() != null ? Wrapper.GetMC().player.getRidingEntity().getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D) : Wrapper.GetMC().player.getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D);
            boolean onLiquid = false;
            int y = (int)bb.minY;

            for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); ++x) {
                for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); ++z) {
                    Block block = Wrapper.GetMC().world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != Blocks.AIR) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }

                        onLiquid = true;
                    }
                }
            }

            return onLiquid;
        }
    }

    public static boolean isAboveLiquid(Entity entity) {
        if (entity == null) {
            return false;
        } else {
            double n = entity.posY + 0.01D;

            for(int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
                for(int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                    if (Wrapper.GetMC().world.getBlockState(new BlockPos(i, (int)n, j)).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos(Wrapper.GetMC().player.getRidingEntity() != null ? Wrapper.GetMC().player.getRidingEntity().posX : Wrapper.GetMC().player.posX, Wrapper.GetMC().player.getRidingEntity() != null ? Wrapper.GetMC().player.getRidingEntity().posY : Wrapper.GetMC().player.posY, Wrapper.GetMC().player.getRidingEntity() != null ? Wrapper.GetMC().player.getRidingEntity().posZ : Wrapper.GetMC().player.posZ);
    }

    public static boolean checkForLiquid(Entity entity, boolean b) {
        if (entity == null) {
            return false;
        } else {
            double posY = entity.posY;
            double n;
            if (b) {
                n = 0.03D;
            } else if (entity instanceof EntityPlayer) {
                n = 0.2D;
            } else {
                n = 0.5D;
            }

            double n2 = posY - n;

            for(int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
                for(int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                    if (Wrapper.GetMC().world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isOnLiquid() {
        double y = Wrapper.GetMC().player.posY - 0.03D;

        for(int x = MathHelper.floor(Wrapper.GetMC().player.posX); x < MathHelper.ceil(Wrapper.GetMC().player.posX); ++x) {
            for(int z = MathHelper.floor(Wrapper.GetMC().player.posZ); z < MathHelper.ceil(Wrapper.GetMC().player.posZ); ++z) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (Wrapper.GetMC().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }

        return false;
    }

    public static double[] forward(double speed) {
        float forward = Wrapper.GetMC().player.movementInput.moveForward;
        float side = Wrapper.GetMC().player.movementInput.moveStrafe;
        float yaw = Wrapper.GetMC().player.prevRotationYaw + (Wrapper.GetMC().player.rotationYaw - Wrapper.GetMC().player.prevRotationYaw) * Wrapper.GetMC().getRenderPartialTicks();
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

    public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
        return entity.posY >= (double)blockPos.getY();
    }
}
