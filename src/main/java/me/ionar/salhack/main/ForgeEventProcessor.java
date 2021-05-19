package me.ionar.salhack.main;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.events.render.EventRenderGetFOVModifier;
import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

public class ForgeEventProcessor {
    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!event.isCanceled()) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            GlStateManager.disableDepth();
            GlStateManager.glLineWidth(1.0F);
            SalHackMod.EVENT_BUS.post(new RenderEvent(event.getPartialTicks()));
            GlStateManager.glLineWidth(1.0F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
        }
    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onChat(ClientChatEvent event) {
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (Wrapper.GetMC().player != null) {
            SalHackMod.EVENT_BUS.post(new EventClientTick());
        }
    }

    @SubscribeEvent
    public void onTick2(ClientTickEvent event) {
        if (Wrapper.GetMC().player != null) {
            SalHackMod.EVENT_BUS.post(event);
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!event.isCanceled()) {
            SalHackMod.EVENT_BUS.post(event);
        }
    }

    @SubscribeEvent(
        priority = EventPriority.NORMAL,
        receiveCanceled = true
    )
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            ModuleManager.Get().OnKeyPress(Keyboard.getKeyName(Keyboard.getEventKey()));
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPlayerDrawn(Pre event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPlayerDrawn(Post event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onChunkLoaded(Load event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onEventMouse(MouseInputEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onChunkUnLoaded(Unload event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLivingEntityUseItemEventTick(Start entityUseItemEvent) {
        SalHackMod.EVENT_BUS.post(entityUseItemEvent);
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent entityJoinWorldEvent) {
        SalHackMod.EVENT_BUS.post(entityJoinWorldEvent);
    }

    @SubscribeEvent
    public void onPlayerPush(PlayerSPPushOutOfBlocksEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLeftClickBlock(LeftClickBlock event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent entityEvent) {
        SalHackMod.EVENT_BUS.post(entityEvent);
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void getFOVModifier(FOVModifier p_Event) {
        EventRenderGetFOVModifier l_Event = new EventRenderGetFOVModifier((float)p_Event.getRenderPartialTicks(), true);
        SalHackMod.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) {
            p_Event.setFOV(l_Event.GetFOV());
        }

    }

    @SubscribeEvent
    public void OnWorldChange(WorldEvent p_Event) {
        SalHackMod.EVENT_BUS.post(p_Event);
    }
}
