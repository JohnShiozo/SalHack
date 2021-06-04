package me.ionar.salhack.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import java.util.function.Predicate;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.irc.IrcLine;
import me.ionar.salhack.irc.IrcManager;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.text.TextFormatting;

public class IRCModule extends Module {
    public static final Value<IRCModule.Colors> Color;
    public static boolean shouldUpdate;
    @EventHandler
    private final Listener<EventClientTick> OnTick = new Listener(Event -> {
        if (shouldUpdate && SalHack.irc.isConnected()) {
            Iterator var1 = SalHack.irc.getUnreadLines().iterator();

            while(var1.hasNext()) {
                IrcLine ircl = (IrcLine)var1.next();
                if (ircl.getLine().contains("48865")) {
                    return;
                }

                SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.GREEN + ircl.getSender() + ": " + getChatColor() + ircl.getLine());
                ircl.setRead(true);
            }
        }

    }, new Predicate[0]);

    public static String getChatColor() {
        String returnValue;
        switch((IRCModule.Colors)Color.getValue()) {
        case Yellow:
            returnValue = ChatFormatting.YELLOW + "";
            break;
        case Aqua:
            returnValue = ChatFormatting.AQUA + "";
            break;
        case Magenta:
            returnValue = ChatFormatting.LIGHT_PURPLE + "";
            break;
        default:
            throw new IllegalStateException("Unexpected value: " + Color.getValue());
        }

        return returnValue;
    }

    public IRCModule() {
        super("IRC", new String[]{"IRC"}, "Allows you to talk to other ZoeHack users", "NONE", 14361680, Module.ModuleType.MISC);
    }

    public void onEnable() {
        if (Wrapper.GetMC().world != null) {
            (new Thread("enable IRC") {
                public void run() {
                    try {
                        SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.GRAY + "Connecting to IRC");
                        SalHack.irc = new IrcManager(Wrapper.GetMC().getSession().getUsername());
                        SalHack.irc.connect();
                        SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.GREEN + "Connected to IRC use @ + message to chat");
                        SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.GOLD + SalHack.irc.getUsers("#zoe-hack").length + " Players online");
                        IRCModule.shouldUpdate = true;
                    } catch (Exception var2) {
                    }

                }
            }).start();
        } else {
            SalHack.irc = new IrcManager(Wrapper.GetMC().getSession().getUsername());
            SalHack.irc.connect();
            shouldUpdate = true;
        }

        super.onEnable();
    }

    public void onDisable() {
        if (SalHack.irc.isConnected()) {
            SalHack.irc.disconnect();
            SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.RED + "Disconnected from IRC");
            shouldUpdate = false;
        }

        super.onDisable();
    }

    static {
        Color = new Value("Color:", new String[]{"BM"}, "Color to use", IRCModule.Colors.Aqua);
        shouldUpdate = false;
    }

    public static enum Colors {
        Yellow,
        Aqua,
        Magenta;
    }
}
