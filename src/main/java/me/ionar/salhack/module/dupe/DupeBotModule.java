package me.ionar.salhack.module.dupe;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import me.ionar.salhack.events.network.EventNetworkPacketEvent;
import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.managers.TickRateManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.exploit.EntityDesyncModule;
import me.ionar.salhack.module.exploit.PacketCancellerModule;
import me.ionar.salhack.module.dupe.DupeFreecam;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public final class DupeBotModule extends Module {
    public final Value<Boolean> TPSScaling = new Value("TpsScaling", new String[]{"TPSScaling"}, "Use TPS scaling on base timers", true);
    public final Value<Integer> PacketToggleDelay = new Value("PacketToggleDelay", new String[]{"PacketToggleDelay"}, "Delay for packet toggling", 12000, 0, 30000, 1000);
    public final Value<Integer> DupeDelay = new Value("DupeDelay", new String[]{"DupeDelay"}, "Time for DupeDelay (TP back to starting point)", 24000, 0, 30000, 1000);
    public final Value<Integer> RemountDelay = new Value("RemountDelay", new String[]{"RemountDelay"}, "Time for remounting after throwing items", 1000, 0, 10000, 1000);
    public final Value<Integer> RestartTimer = new Value("RestartTimer", new String[]{"RestartTimer"}, "Time for restarting after remounting", 1000, 0, 10000, 1000);
    public final Value<Boolean> BypassMode = new Value("BypassMode", new String[]{"BypassMode"}, "BypassMode for 2b2t on a patch day", false);
    public final Value<Boolean> UseEntityDesync = new Value("EntityDesync", new String[]{"EntityDesync"}, "use EntityDesync", true);
    public final Value<Integer> EntityDesyncDelay = new Value("EntityDesyncDelay", new String[]{"EntityDesyncDelay"}, "edsync delay", 300, 0, 10000, 1000);
    public final Value<Integer> DupeRemountDelay = new Value("DupeRemountDelay", new String[]{"DupeRemountDelay"}, "DupeRemountDelay", 300, 0, 10000, 1000);
    public final Value<Integer> InventoryDelay = new Value("InventoryDelay", new String[]{"InventoryDelay"}, "InventoryDelay", 1000, 0, 10000, 1000);
    public final Value<Boolean> LockOriginalToStart = new Value("LockOriginalToStart", new String[]{"Lock"}, "Lock", false);
    public final Value<Boolean> IgnorePosUpdate = new Value("IgnorePosUpdate", new String[]{"IgnorePosUpdate"}, "IgnorePosUpdate", false);
    public final Value<Integer> BypassRemount = new Value("BypassRemount", new String[]{""}, "BypassRemount", 18000, 0, 18000, 1000);
    private Vec3d StartPos;
    private float Pitch;
    private float Yaw;
    private DupeFreecam Freecam;
    private PacketCancellerModule PacketCanceller;
    private EntityDesyncModule EntityDesync;
    private Timer timer;
    private Entity riding;
    private BlockPos button;
    private int ShulkersDuped;
    private Vec3d StartingPosition;
    private boolean RestartDupeNoInv;
    private boolean SetIgnoreStartClip;
    private int StreakCounter;
    private me.ionar.salhack.util.Timer packetTimer;
    @EventHandler
    private Listener<EventPlayerUpdate> OnPlayerUpdate;
    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent;

    public DupeBotModule() {
        super("DupeBot", new String[]{"Duper", "Autodonkeydupe"}, "Automaticly performs the wwe donkey dupe", "NONE", -1, Module.ModuleType.DUPE);
        this.StartPos = Vec3d.ZERO;
        this.ShulkersDuped = 0;
        this.StartingPosition = Vec3d.ZERO;
        this.RestartDupeNoInv = false;
        this.SetIgnoreStartClip = false;
        this.StreakCounter = 0;
        this.packetTimer = new me.ionar.salhack.util.Timer();
        this.OnPlayerUpdate = new Listener((p_Event) -> {
            float seconds = (float)(System.currentTimeMillis() - this.packetTimer.getTime()) / 1000.0F % 60.0F;
            if (this.isEnabled()) {
                if (this.StartPos != Vec3d.ZERO) {
                    if (!this.SetIgnoreStartClip && !(Boolean)this.IgnorePosUpdate.getValue()) {
                        this.mc.player.setPosition(this.StartPos.x, this.StartPos.y, this.StartPos.z);
                    }

                    this.mc.player.rotationPitch = this.Pitch;
                    this.mc.player.rotationYaw = this.Yaw;
                }
            }
        }, new Predicate[0]);
        this.PacketEvent = new Listener((p_Event) -> {
            if (p_Event.getPacket() != null) {
                this.packetTimer.reset();
            }

            if (p_Event.getPacket() instanceof SPacketWindowItems) {
                if (!this.mc.player.isRiding() || !(this.mc.player.getRidingEntity() instanceof AbstractChestHorse)) {
                    return;
                }

                AbstractChestHorse l_Donkey = this.GetNearDonkey();
                this.RestartDupeNoInv = false;
                if (l_Donkey == null) {
                    SalHack.SendMessage("Could not find the donkey near you");
                    this.mc.player.closeScreen();
                    this.HandleDupe();
                    return;
                }

                SalHack.SendMessage("Dumping items from " + this.mc.player.getRidingEntity().getName());
                SPacketWindowItems l_Packet = (SPacketWindowItems)p_Event.getPacket();
                int l_I = 0;

                for(Iterator var5 = l_Packet.getItemStacks().iterator(); var5.hasNext(); ++l_I) {
                    ItemStack l_Stack = (ItemStack)var5.next();
                    if (l_I > 1 && l_I < 17) {
                        this.mc.playerController.windowClick(l_Packet.getWindowId(), l_I, 1, ClickType.THROW, this.mc.player);
                        if (l_Stack.getItem() instanceof ItemShulkerBox) {
                            ++this.ShulkersDuped;
                        }
                    }
                }

                SalHack.SendMessage(ChatFormatting.GREEN + "Done dumping items from " + this.mc.player.getRidingEntity().getName() + "!");
                ++this.StreakCounter;
                this.timer.schedule(new TimerTask() {
                    public void run() {
                        DupeBotModule.this.mc.player.closeScreen();
                        DupeBotModule.this.mc.displayGuiScreen((GuiScreen)null);
                        DupeBotModule.this.Remount();
                    }
                }, (long)(Integer)this.RemountDelay.getValue());
            }

        }, new Predicate[0]);
    }

    public String getMetaData() {
        return "" + this.ShulkersDuped + ChatFormatting.GOLD + " Streak: " + this.StreakCounter;
    }

    public void ToggleOffMods() {
        if (this.Freecam != null && this.Freecam.isEnabled()) {
            this.Freecam.toggle();
        }

        if (this.PacketCanceller != null && this.PacketCanceller.isEnabled()) {
            this.PacketCanceller.toggle();
        }

        if (this.EntityDesync != null && this.EntityDesync.isEnabled()) {
            this.EntityDesync.toggle();
        }

    }

    public void onEnable() {
        super.onEnable();
        if (this.mc.player == null) {
            this.toggle();
        } else if (this.mc.player.getRidingEntity() == null) {
            SalHack.SendMessage(ChatFormatting.RED + "You are not riding an entity!");
            this.toggle();
        } else {
            this.StartingPosition = new Vec3d(this.mc.player.getRidingEntity().posX, this.mc.player.getRidingEntity().posY, this.mc.player.getRidingEntity().posZ);
            this.StartPos = new Vec3d(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ);
            this.Pitch = this.mc.player.rotationPitch;
            this.Yaw = this.mc.player.rotationYaw;
            this.Freecam = (DupeFreecam)ModuleManager.Get().GetMod(DupeFreecam.class);
            this.PacketCanceller = (PacketCancellerModule)ModuleManager.Get().GetMod(PacketCancellerModule.class);
            this.EntityDesync = (EntityDesyncModule)ModuleManager.Get().GetMod(EntityDesyncModule.class);
            this.ToggleOffMods();
            this.button = null;
            this.SetIgnoreStartClip = false;
            SalHack.SendMessage("Last Streak counter was " + this.StreakCounter);
            this.StreakCounter = 0;
            this.HandleDupe();
        }
    }

    public int CalculateNewTime(int p_BaseTime, float p_Tps) {
        return (Boolean)this.TPSScaling.getValue() ? (int)((float)p_BaseTime * (20.0F / p_Tps)) : p_BaseTime;
    }

    public void HandleDupe() {
        SalHack.SendMessage(ChatFormatting.LIGHT_PURPLE + "Starting dupe!");
        if (this.timer != null) {
            this.timer.cancel();
        }

        this.timer = new Timer();
        if ((Boolean)this.LockOriginalToStart.getValue()) {
            this.mc.player.getRidingEntity().setPosition(this.StartingPosition.x, this.StartingPosition.y, this.StartingPosition.z);
        }

        RayTraceResult ray = this.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == Type.BLOCK && this.button == null) {
            BlockPos pos = ray.getBlockPos();
            IBlockState iblockstate = this.mc.world.getBlockState(pos);
            if (iblockstate.getMaterial() != Material.AIR && this.mc.world.getWorldBorder().contains(pos) && (iblockstate.getBlock() == Blocks.WOODEN_BUTTON || iblockstate.getBlock() == Blocks.STONE_BUTTON)) {
                this.button = pos;
            }
        }

        if (this.button == null) {
            SalHack.SendMessage("Button is null!");
        } else {
            if (this.StartPos != Vec3d.ZERO) {
                this.mc.player.setPosition(this.StartPos.x, this.StartPos.y, this.StartPos.z);
            }

            this.mc.playerController.processRightClickBlock(this.mc.player, this.mc.world, this.button, EnumFacing.UP, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND);
            SalHack.SendMessage("Rightclicked!");
            final float l_Tps = TickRateManager.Get().getTickRate();
            SalHack.SendMessage("Tps: " + l_Tps);
            this.riding = this.mc.player.getRidingEntity();
            this.timer.schedule(new TimerTask() {
                public void run() {
                    DupeBotModule.this.Freecam.toggle();
                    DupeBotModule.this.PacketCanceller.toggle();
                    SalHack.SendMessage("Toggled the hax!");
                }
            }, 100L);
            this.timer.schedule(new TimerTask() {
                public void run() {
                    DupeBotModule.this.PacketCanceller.toggle();
                    SalHack.SendMessage("Moving the donkey!");
                    DupeBotModule.this.timer.schedule(new TimerTask() {
                        public void run() {
                            DupeBotModule.this.PacketCanceller.toggle();
                            SalHack.SendMessage("Not moving the donkey!");
                        }
                    }, (long)DupeBotModule.this.CalculateNewTime(1000, l_Tps));
                }
            }, (long)this.CalculateNewTime((Integer)this.PacketToggleDelay.getValue(), l_Tps));
            this.timer.schedule(new TimerTask() {
                public void run() {
                    DupeBotModule.this.Freecam.toggle();
                    if ((Boolean)DupeBotModule.this.BypassMode.getValue()) {
                        DupeBotModule.this.timer.schedule(new TimerTask() {
                            public void run() {
                                if (!(Boolean)DupeBotModule.this.BypassMode.getValue() && (Boolean)DupeBotModule.this.UseEntityDesync.getValue()) {
                                    DupeBotModule.this.PacketCanceller.toggle();
                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                        public void run() {
                                            DupeBotModule.this.EntityDesync.toggle();
                                            SalHack.SendMessage("EntityDesync - ON");
                                            DupeBotModule.this.timer.schedule(new TimerTask() {
                                                public void run() {
                                                    DupeBotModule.this.EntityDesync.toggle();
                                                    SalHack.SendMessage("EntityDesync - OFF");
                                                }
                                            }, (long)DupeBotModule.this.CalculateNewTime(100, l_Tps));
                                        }
                                    }, (long)DupeBotModule.this.CalculateNewTime((Integer)DupeBotModule.this.EntityDesyncDelay.getValue(), l_Tps));
                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                        public void run() {
                                            if (DupeBotModule.this.Freecam.isEnabled()) {
                                                DupeBotModule.this.Freecam.toggle();
                                            }

                                            if (DupeBotModule.this.PacketCanceller.isEnabled()) {
                                                DupeBotModule.this.PacketCanceller.toggle();
                                            }

                                            DupeBotModule.this.timer.schedule(new TimerTask() {
                                                public void run() {
                                                    DupeBotModule.this.RestartDupeNoInv = true;
                                                    DupeBotModule.this.mc.player.sendHorseInventory();
                                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                                        public void run() {
                                                            if (DupeBotModule.this.RestartDupeNoInv) {
                                                                SalHack.SendMessage("Detected Ghost Donkey, retrying");
                                                                DupeBotModule.this.riding = null;
                                                                DupeBotModule.this.Remount();
                                                            }
                                                        }
                                                    }, 2000L);
                                                }
                                            }, (long)(Integer)DupeBotModule.this.InventoryDelay.getValue());
                                        }
                                    }, (long)DupeBotModule.this.CalculateNewTime((Integer)DupeBotModule.this.DupeRemountDelay.getValue() + (Integer)DupeBotModule.this.EntityDesyncDelay.getValue() + 1000, l_Tps));
                                } else if ((Boolean)DupeBotModule.this.BypassMode.getValue()) {
                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                        public void run() {
                                            DupeBotModule.this.PacketCanceller.toggle();
                                        }
                                    }, 500L);
                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                        public void run() {
                                            DupeBotModule.this.PacketCanceller.toggle();
                                        }
                                    }, 600L);
                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                        public void run() {
                                            SalHack.SendMessage("Bypass done");
                                            if (DupeBotModule.this.PacketCanceller.isEnabled()) {
                                                DupeBotModule.this.PacketCanceller.toggle();
                                            }

                                            DupeBotModule.this.SetIgnoreStartClip = false;
                                            if ((Boolean)DupeBotModule.this.UseEntityDesync.getValue()) {
                                                DupeBotModule.this.timer.schedule(new TimerTask() {
                                                    public void run() {
                                                        DupeBotModule.this.EntityDesync.toggle();
                                                        SalHack.SendMessage("EntityDesync - ON");
                                                        DupeBotModule.this.timer.schedule(new TimerTask() {
                                                            public void run() {
                                                                DupeBotModule.this.EntityDesync.toggle();
                                                                SalHack.SendMessage("EntityDesync - OFF");
                                                            }
                                                        }, (long)DupeBotModule.this.CalculateNewTime(100, l_Tps));
                                                    }
                                                }, (long)DupeBotModule.this.CalculateNewTime((Integer)DupeBotModule.this.EntityDesyncDelay.getValue(), l_Tps));
                                            }

                                            DupeBotModule.this.timer.schedule(new TimerTask() {
                                                public void run() {
                                                    if (DupeBotModule.this.StartPos != Vec3d.ZERO) {
                                                        DupeBotModule.this.mc.player.setPosition(DupeBotModule.this.StartPos.x, DupeBotModule.this.StartPos.y, DupeBotModule.this.StartPos.z);
                                                    }

                                                    if (DupeBotModule.this.Freecam.isEnabled()) {
                                                        DupeBotModule.this.Freecam.toggle();
                                                    }

                                                    if (DupeBotModule.this.PacketCanceller.isEnabled()) {
                                                        DupeBotModule.this.PacketCanceller.toggle();
                                                    }

                                                    DupeBotModule.this.timer.schedule(new TimerTask() {
                                                        public void run() {
                                                            DupeBotModule.this.RestartDupeNoInv = true;
                                                            DupeBotModule.this.mc.player.sendHorseInventory();
                                                            SalHack.SendMessage("Sending inventory.");
                                                            DupeBotModule.this.timer.schedule(new TimerTask() {
                                                                public void run() {
                                                                    if (DupeBotModule.this.RestartDupeNoInv) {
                                                                        SalHack.SendMessage("Detected Ghost Donkey, retrying");
                                                                        DupeBotModule.this.riding = null;
                                                                        DupeBotModule.this.Remount();
                                                                    }
                                                                }
                                                            }, 2000L);
                                                        }
                                                    }, (long)(Integer)DupeBotModule.this.InventoryDelay.getValue());
                                                }
                                            }, (long)DupeBotModule.this.CalculateNewTime((Integer)DupeBotModule.this.DupeRemountDelay.getValue() + (Integer)DupeBotModule.this.EntityDesyncDelay.getValue(), l_Tps));
                                        }
                                    }, (long)(Integer)DupeBotModule.this.BypassRemount.getValue());
                                }

                            }
                        }, (long)(Integer)DupeBotModule.this.DupeRemountDelay.getValue());
                    } else {
                        DupeBotModule.this.PacketCanceller.toggle();
                        if (DupeBotModule.this.StartPos != Vec3d.ZERO) {
                            DupeBotModule.this.mc.player.setPosition(DupeBotModule.this.StartPos.x, DupeBotModule.this.StartPos.y, DupeBotModule.this.StartPos.z);
                        }

                        if (DupeBotModule.this.Freecam.isEnabled()) {
                            DupeBotModule.this.Freecam.toggle();
                        }

                        if (DupeBotModule.this.PacketCanceller.isEnabled()) {
                            DupeBotModule.this.PacketCanceller.toggle();
                        }

                        DupeBotModule.this.timer.schedule(new TimerTask() {
                            public void run() {
                                DupeBotModule.this.RestartDupeNoInv = true;
                                DupeBotModule.this.mc.player.sendHorseInventory();
                                SalHack.SendMessage("Sending inventory.");
                                DupeBotModule.this.timer.schedule(new TimerTask() {
                                    public void run() {
                                        if (DupeBotModule.this.RestartDupeNoInv) {
                                            SalHack.SendMessage("Detected Ghost Donkey, retrying");
                                            DupeBotModule.this.riding = null;
                                            DupeBotModule.this.Remount();
                                        }
                                    }
                                }, 2000L);
                            }
                        }, (long)(Integer)DupeBotModule.this.InventoryDelay.getValue());
                    }

                }
            }, (long)this.CalculateNewTime((Integer)this.DupeDelay.getValue(), l_Tps));
        }
    }

    public void onDisable() {
        if (this.timer != null) {
            this.timer.cancel();
        }

        this.timer = null;
        this.StartPos = Vec3d.ZERO;
        this.ToggleOffMods();
    }

    public AbstractChestHorse GetNearDonkey() {
        int l_EntityId = this.riding != null ? this.riding.getEntityId() : 0;
        AbstractChestHorse l_Donkey = (AbstractChestHorse)this.mc.world.loadedEntityList.stream().filter((entity) -> {
            return entity instanceof AbstractChestHorse && entity != this.riding && this.mc.player.getDistance(entity) < 10.0F && entity.getEntityId() != l_EntityId;
        }).map((entity) -> {
            return (AbstractChestHorse)entity;
        }).min(Comparator.comparing((c) -> {
            return this.mc.player.getDistance(c);
        })).orElse((Object)null);
        return l_Donkey;
    }

    public void Remount() {
        final AbstractChestHorse l_Donkey = this.GetNearDonkey();
        if (l_Donkey != null) {
            SalHack.SendMessage(ChatFormatting.GREEN + "Processing remount on " + l_Donkey.getName());
            this.riding = null;
            this.mc.player.connection.sendPacket(new CPacketInput(this.mc.player.moveStrafing, this.mc.player.moveForward, this.mc.player.movementInput.jump, true));
            this.timer.schedule(new TimerTask() {
                public void run() {
                    DupeBotModule.this.mc.playerController.interactWithEntity(DupeBotModule.this.mc.player, l_Donkey, EnumHand.MAIN_HAND);
                }
            }, 111L);
            this.timer.schedule(new TimerTask() {
                public void run() {
                    SalHack.SendMessage("Restarting dupe!");
                    DupeBotModule.this.HandleDupe();
                }
            }, (long)((Integer)this.RestartTimer.getValue() + 111));
        }

    }
}
