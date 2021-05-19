// Decompiled with: CFR 0.151
// Class Version: 8
package me.ionar.salhack.module.render;

import java.util.List;
import java.util.function.Predicate;
import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.ChunkUtils;
import me.ionar.salhack.util.render.RenderUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class BaseTracerModule
extends Module {
    public static Value<Boolean> tracePortal = new Value<Boolean>("TracePortal", new String[]{"TP"}, "Traces portals.", true);
    public static Value<Boolean> traceChests = new Value<Boolean>("TraceChests", new String[]{"TC"}, "Traces chests.", true);
    @EventHandler
    private Listener<RenderEvent> onRender = new Listener<RenderEvent>(event -> {
        if (this.mc.getRenderManager() == null || this.mc.getRenderManager().options == null) {
            return;
        }
        ChunkProviderClient chunkProvider = this.mc.world.getChunkProvider();
        List chunks = ChunkUtils.stealAndGetField(chunkProvider, List.class);
        for (int currentChunk = 0; currentChunk < chunks.size(); ++currentChunk) {
            Chunk c = (Chunk)chunks.get(currentChunk);
            for (int xx = 0; xx < 16; ++xx) {
                for (int zz = 0; zz < 16; ++zz) {
                    for (int yy = 0; yy < 256; ++yy) {
                        Block block = c.getBlockState(xx, yy, zz).getBlock();
                        if (this.getColor(block) == -1) continue;
                        this.renderBlock((RenderEvent)event, block, xx, yy, zz);
                    }
                }
            }
        }
    }, new Predicate[0]);

    public BaseTracerModule() {
        super("BaseTracers", new String[]{"BaseT"}, "Traces a trajectory to a portal/chest.", "NONE", -1, Module.ModuleType.RENDER);
    }

    public void renderBlock(RenderEvent event, Block block, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        boolean bobbing = this.mc.gameSettings.viewBobbing;
        this.mc.gameSettings.viewBobbing = false;
        this.mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
        Vec3d forward = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(Minecraft.getMinecraft().player.rotationPitch))).rotateYaw(-((float)Math.toRadians(Minecraft.getMinecraft().player.rotationYaw)));
        RenderUtil.drawLine3D((float)forward.x, (float)forward.y + this.mc.player.getEyeHeight(), (float)forward.z, pos.getX(), pos.getY(), pos.getZ(), 0.5f, this.getColor(block));
        this.mc.gameSettings.viewBobbing = bobbing;
        this.mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
    }

    public int getColor(Block block) {
        if (block instanceof BlockPortal && tracePortal.getValue().booleanValue()) {
            return 8535429;
        }
        if (block instanceof BlockChest && traceChests.getValue().booleanValue()) {
            return 16703770;
        }
        return -1;
    }
}
