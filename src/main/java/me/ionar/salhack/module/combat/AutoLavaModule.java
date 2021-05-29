package me.ionar.salhack.module.combat;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.render.ChatColor;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Iterator;
import java.util.List;

public class AutoLavaModule extends Module {
    public static final Value<Integer> range = new Value("Range", new String[]{"RNG"}, "Range of block placement.", 5, 0, 10, 1);

    public AutoLavaModule() {
        super("AutoLava", new String[]{"ALAVA"}, "It places lava on the head of the target.", "NONE", 2411227, Module.ModuleType.COMBAT);
    }

    private EntityPlayer findTarget() {
        List<EntityPlayer> playerList = this.mc.player.world.playerEntities;
        EntityPlayer closestTarget = null;
        Iterator var3 = playerList.iterator();

        while(var3.hasNext()) {
            EntityPlayer target = (EntityPlayer)var3.next();
            if (target != this.mc.player && !FriendManager.Get().IsFriend(target.getName()) && EntityUtil.isLiving(target) && !(target.getHealth() <= 0.0F)) {
                double currentDistance = (double)this.mc.player.getDistance(target);
                if (!(currentDistance > Double.valueOf(((Integer)range.getValue()).doubleValue())) && closestTarget != null && !(currentDistance >= (double)this.mc.player.getDistance(closestTarget))) {
                    closestTarget = target;
                }
            }
        }

        return closestTarget;
    }

    public void onEnable() {
        EntityPlayer target = this.findTarget();
        if (target == null) {
            SalHack.SendMessage(ChatColor.RED + "There isn't any close target.");
            this.toggle();
        }
    }
}
