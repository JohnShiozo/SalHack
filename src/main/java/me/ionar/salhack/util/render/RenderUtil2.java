package me.ionar.salhack.util.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class RenderUtil {
    private static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

    public static void updateModelViewProjectionMatrix() {
        GL11.glGetFloat(2982, MODELVIEW);
        GL11.glGetFloat(2983, PROJECTION);
        GL11.glGetInteger(2978, VIEWPORT);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        GLUProjection.getInstance().updateMatrices(VIEWPORT, MODELVIEW, PROJECTION, (double)((float)res.getScaledWidth() / (float)Minecraft.getMinecraft().displayWidth), (double)((float)res.getScaledHeight() / (float)Minecraft.getMinecraft().displayHeight));
    }

    public static void DrawPolygon(double x, double y, int radius, int sides, int color) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(6, DefaultVertexFormats.POSITION);
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        bufferbuilder.pos(x, y, 0.0D).endVertex();
        double TWICE_PI = 6.283185307179586D;

        for(int i = 0; i <= sides; ++i) {
            double angle = 6.283185307179586D * (double)i / (double)sides + Math.toRadians(180.0D);
            bufferbuilder.pos(x + Math.sin(angle) * (double)radius, y + Math.cos(angle) * (double)radius, 0.0D).endVertex();
        }

        tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(float x, float y, float w, float h, int color, float alpha) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTriangle(float x, float y, float size, float theta, int color) {
        GL11.glTranslated((double)x, (double)y, 0.0D);
        GL11.glRotatef(180.0F + theta, 0.0F, 0.0F, 1.0F);
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(6);
        GL11.glVertex2d(0.0D, (double)(1.0F * size));
        GL11.glVertex2d((double)(1.0F * size), (double)(-(1.0F * size)));
        GL11.glVertex2d((double)(-(1.0F * size)), (double)(-(1.0F * size)));
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glRotatef(-180.0F - theta, 0.0F, 0.0F, 1.0F);
        GL11.glTranslated((double)(-x), (double)(-y), 0.0D);
    }

    public static void drawOutlineRect(float x, float y, float w, float h, float thickness, int c) {
        drawRect(x, y, x - thickness, h, c);
        drawRect(w + thickness, y, w, h, c);
        drawRect(x, y, w, y - thickness, c);
        drawRect(x, h + thickness, w, h, c);
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float)(hex >> 16 & 255) / 255.0F;
        float green = (float)(hex >> 8 & 255) / 255.0F;
        float blue = (float)(hex & 255) / 255.0F;
        float alpha = (float)(hex >> 24 & 255) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x1, (double)y1, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawLine3D(float x, float y, float z, float x1, float y1, float z1, float thickness, int hex) {
        float red = (float)(hex >> 16 & 255) / 255.0F;
        float green = (float)(hex >> 8 & 255) / 255.0F;
        float blue = (float)(hex & 255) / 255.0F;
        float alpha = (float)(hex >> 24 & 255) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GlStateManager.disableDepth();
        GL11.glEnable(34383);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)y, (double)z).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x1, (double)y1, (double)z1).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.enableDepth();
        GL11.glDisable(34383);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, 0.0F).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        drawBoundingBox(bb, width, red, green, blue, alpha);
    }

    public static void drawPlane(double x, double y, double z, AxisAlignedBB bb, float width, int color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        drawPlane(bb, width, color);
        GL11.glPopMatrix();
    }

    public static void drawPlane(AxisAlignedBB axisalignedbb, float width, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.glLineWidth(width);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        drawPlane(axisalignedbb, color);
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawPlane(AxisAlignedBB boundingBox, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        double minX = boundingBox.minX;
        double minY = boundingBox.minY;
        double minZ = boundingBox.minZ;
        double maxX = boundingBox.maxX;
        double maxY = boundingBox.maxY;
        double maxZ = boundingBox.maxZ;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(minX, minY, maxZ).color(red, green, blue, 0.0F).endVertex();
        bufferbuilder.pos(maxZ, minY, minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
        GL11.glScissor((int)(x * (float)sr.getScaleFactor()), (int)((float)Minecraft.getMinecraft().displayHeight - y1 * (float)sr.getScaleFactor()), (int)((x1 - x) * (float)sr.getScaleFactor()), (int)((y1 - y) * (float)sr.getScaleFactor()));
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667F;
        GlStateManager.translate((double)x - Minecraft.getMinecraft().getRenderManager().renderPosX, (double)y - Minecraft.getMinecraft().getRenderManager().renderPosY, (double)z - Minecraft.getMinecraft().getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int)player.getDistance((double)x, (double)y, (double)z);
        float scaleDistance = (float)distance / 2.0F / (2.0F + (2.0F - scale));
        if (scaleDistance < 1.0F) {
            scaleDistance = 1.0F;
        }

        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void drawTexture(float x, float y, float textureX, float textureY, float width, float height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)(textureX * f), (double)((textureY + height) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((textureX + width) * f), (double)((textureY + height) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)((textureX + width) * f), (double)(textureY * f1)).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).tex((double)(textureX * f), (double)(textureY * f1)).endVertex();
        tessellator.draw();
    }

    public static void drawTexture(float x, float y, float width, float height, float u, float v, float t, float s) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(4, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)t, (double)v).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).tex((double)u, (double)v).endVertex();
        bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)u, (double)s).endVertex();
        bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)u, (double)s).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)t, (double)s).endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)t, (double)v).endVertex();
        tessellator.draw();
    }

    public static final void DrawNodusBetterRect(double x, double y, double x1, double y1, int color2, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        drawRect((float)((int)x), (float)((int)y), (float)((int)x1), (float)((int)y1), color);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawRect((float)((int)x * 2 - 1), (float)((int)y * 2), (float)((int)x * 2), (float)((int)y1 * 2 - 1), color2);
        drawRect((float)((int)x * 2), (float)((int)y * 2 - 1), (float)((int)x1 * 2), (float)((int)y * 2), color2);
        drawRect((float)((int)x1 * 2), (float)((int)y * 2), (float)((int)x1 * 2 + 1), (float)((int)y1 * 2 - 1), color2);
        drawRect((float)((int)x * 2), (float)((int)y1 * 2 - 1), (float)((int)x1 * 2), (float)((int)y1 * 2), color2);
        GL11.glDisable(3042);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static final void DrawNodusRect(float par0, float par1, float par2, float par3, int par4) {
        float var5;
        if (par0 < par2) {
            var5 = par0;
            par0 = par2;
            par2 = var5;
        }

        if (par1 < par3) {
            var5 = par1;
            par1 = par3;
            par3 = var5;
        }

        float var10 = (float)(par4 >> 24 & 255) / 255.0F;
        float var6 = (float)(par4 >> 16 & 255) / 255.0F;
        float var7 = (float)(par4 >> 8 & 255) / 255.0F;
        float var8 = (float)(par4 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(var6, var7, var8, var10);
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)par0, (double)par3, 0.0D).color(var6, var7, var8, var10).endVertex();
        bufferbuilder.pos((double)par2, (double)par3, 0.0D).color(var6, var7, var8, var10).endVertex();
        bufferbuilder.pos((double)par2, (double)par1, 0.0D).color(var6, var7, var8, var10).endVertex();
        bufferbuilder.pos((double)par0, (double)par1, 0.0D).color(var6, var7, var8, var10).endVertex();
        tessellator.draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public static float drawStringWithShadow(String p_Name, float p_X, float p_Y, int p_Color) {
        HudModule l_Hud = (HudModule)ModuleManager.Get().GetMod(HudModule.class);
        return l_Hud != null && !(Boolean)l_Hud.CustomFont.getValue() ? (float)Wrapper.GetMC().fontRenderer.drawStringWithShadow(p_Name, p_X, p_Y, p_Color) : SalHack.GetFontManager().DrawStringWithShadow(p_Name, p_X, p_Y, p_Color);
    }

    public static float getStringWidth(String p_Name) {
        HudModule l_Hud = (HudModule)ModuleManager.Get().GetMod(HudModule.class);
        return l_Hud != null && !(Boolean)l_Hud.CustomFont.getValue() ? (float)Wrapper.GetMC().fontRenderer.getStringWidth(p_Name) : SalHack.GetFontManager().GetStringWidth(p_Name);
    }

    public static float getStringHeight(String name) {
        HudModule l_Hud = (HudModule)ModuleManager.Get().GetMod(HudModule.class);
        return l_Hud != null && !(Boolean)l_Hud.CustomFont.getValue() ? (float)Wrapper.GetMC().fontRenderer.FONT_HEIGHT : SalHack.GetFontManager().GetStringHeight(name);
    }

    public static void drawSplitString(String p_Name, int p_X, int p_Y, int p_K, int p_Color) {
        Minecraft.getMinecraft().fontRenderer.drawSplitString(p_Name, p_X, p_Y, p_K, p_Color);
    }

    public static void drawBorderedRect(int x, int y, int x1, int y1, int color, float lineWidth, int color1) {
        drawRect((float)x, (float)y, (float)x1, (float)y1, color);
        setupOverlayRendering();
        disableDefaults();
        GL11.glColor4d(getRedFromHex(color1), getGreenFromHex(color1), getBlueFromHex(color1), getAlphaFromHex(color1));
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(1);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x1, (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x1, (double)y);
        GL11.glVertex2d((double)x, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glEnd();
        enableDefaults();
    }

    public static void drawRect(int x, int y, int x1, int y1, int color, int p_CustomAlpha) {
        setupOverlayRendering();
        disableDefaults();
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), p_CustomAlpha > 0 ? (double)p_CustomAlpha : getAlphaFromHex(color));
        GL11.glBegin(7);
        GL11.glVertex2i(x1, y);
        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x, y1);
        GL11.glVertex2i(x1, y1);
        GL11.glEnd();
        enableDefaults();
    }

    public static void drawRoundedRect(int x, int y, int x1, int y1, int radius, int color, int p_CustomAlpha) {
        disableDefaults();
        float newX = (float)Math.abs(x + radius);
        float newY = (float)Math.abs(y + radius);
        float newX1 = (float)Math.abs(x1 - radius);
        float newY1 = (float)Math.abs(y1 - radius);
        drawRect(newX, newY, newX1, newY1, color);
        drawRect((float)x, newY, newX, newY1, color);
        drawRect(newX1, newY, (float)x1, newY1, color);
        drawRect(newX, (float)y, newX1, newY, color);
        drawRect(newX, newY1, newX1, (float)y1, color);
        drawQuarterCircle((int)newX, (int)newY, radius, 0, color, p_CustomAlpha);
        drawQuarterCircle((int)newX1, (int)newY, radius, 1, color, p_CustomAlpha);
        drawQuarterCircle((int)newX, (int)newY1, radius, 2, color, p_CustomAlpha);
        drawQuarterCircle((int)newX1, (int)newY1, radius, 3, color, p_CustomAlpha);
        enableDefaults();
    }

    public static void drawLine2D(int x, int y, int x1, int y1, int color, float lineWidth) {
        setupOverlayRendering();
        disableDefaults();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), getAlphaFromHex(color));
        GL11.glBegin(1);
        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x1, y1);
        GL11.glEnd();
        GL11.glDisable(2848);
        enableDefaults();
    }

    public static void drawBorderedCircle(int x, int y, int radius, int color, float lineWidth, int color1) {
        drawCircle(x, y, radius, color);
        drawUnfilledCircle(x, y, radius, lineWidth, color1);
    }

    public static void drawUnfilledCircle(int x, int y, int radius, float lineWidth, int color) {
        setupOverlayRendering();
        disableDefaults();
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), getAlphaFromHex(color));
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glBegin(2);

        for(int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)radius, (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)radius);
        }

        GL11.glEnd();
        GL11.glDisable(2848);
        enableDefaults();
    }

    public static void drawCircle(int x, int y, int radius, int color) {
        setupOverlayRendering();
        disableDefaults();
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), getAlphaFromHex(color));
        GL11.glBegin(9);

        for(int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)radius, (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)radius);
        }

        GL11.glEnd();
        enableDefaults();
    }

    public static void drawQuarterCircle(int x, int y, int radius, int mode, int color, int p_CustomAlpha) {
        disableDefaults();
        GL11.glColor4d(getRedFromHex(color), getGreenFromHex(color), getBlueFromHex(color), p_CustomAlpha > 0 ? (double)p_CustomAlpha : getAlphaFromHex(color));
        GL11.glBegin(9);
        GL11.glVertex2d((double)x, (double)y);
        int i;
        if (mode == 0) {
            for(i = 0; i <= 90; ++i) {
                GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)(radius * -1), (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)(radius * -1));
            }
        } else if (mode == 1) {
            for(i = 90; i <= 180; ++i) {
                GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)radius, (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)radius);
            }
        } else if (mode == 2) {
            for(i = 90; i <= 180; ++i) {
                GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)(radius * -1), (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)(radius * -1));
            }
        } else if (mode == 3) {
            for(i = 0; i <= 90; ++i) {
                GL11.glVertex2d((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)radius, (double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)radius);
            }
        }

        GL11.glEnd();
        enableDefaults();
    }

    public static double getAlphaFromHex(int color) {
        return (double)((float)(color >> 24 & 255) / 255.0F);
    }

    public static double getRedFromHex(int color) {
        return (double)((float)(color >> 16 & 255) / 255.0F);
    }

    public static double getGreenFromHex(int color) {
        return (double)((float)(color >> 8 & 255) / 255.0F);
    }

    public static double getBlueFromHex(int color) {
        return (double)((float)(color & 255) / 255.0F);
    }

    public static int getScreenWidth() {
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(2978, viewport);
        return Math.round((float)viewport.get(2));
    }

    public static int getScreenHeight() {
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(2978, viewport);
        return Math.round((float)viewport.get(3));
    }

    public static void setupGradient() {
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
    }

    public static void unsetupGradient() {
        GL11.glShadeModel(7424);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    public static void setupOverlayRendering() {
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)getScreenWidth(), (double)getScreenHeight(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public static void disableDefaults() {
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
    }

    public static void enableDefaults() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
    }

    public static void disableLighting() {
        GL11.glDisable(2896);
    }

    public static String trimStringToWidth(String substring, int width) {
        return Minecraft.getMinecraft().fontRenderer.trimStringToWidth(substring, width);
    }

    public static String trimStringToWidth(String text, int j, boolean b) {
        return Minecraft.getMinecraft().fontRenderer.trimStringToWidth(text, j, b);
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, Color c) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        ts.draw();
    }

    public static void drawBox(AxisAlignedBB boundingBox) {
        assert boundingBox != null;

        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
        GlStateManager.glBegin(7);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ);
        GlStateManager.glVertex3f((float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glVertex3f((float)boundingBox.maxX, (float)boundingBox.minY, (float)boundingBox.maxZ);
        GlStateManager.glEnd();
    }

    public static void drawOutlinedBox(AxisAlignedBB bb) {
        GL11.glBegin(1);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glEnd();
    }

    public static void drawESPOutline(AxisAlignedBB bb, float red, float green, float blue, float alpha, float width) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(width);
        GL11.glColor4f(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
        drawOutlinedBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawESP(AxisAlignedBB bb, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
        drawBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
