package me.ionar.salhack.irc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.IOException;
import me.ionar.salhack.util.CommandUtil;
import me.ionar.salhack.irc.pircBot.IrcException;
import me.ionar.salhack.irc.pircBot.NickAlreadyInUseException;
import me.ionar.salhack.irc.pircBot.PircBot;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.misc.IRCModule;
import net.minecraft.util.text.TextFormatting;

public class IrcManager extends PircBot {
    private final String IRC_HostName = "irc.freenode.net";
    private final int IRC_HostPort = 6667;
    private final String IRC_ChannelName = "#negroware";
    private static String username;

    public IrcManager(String username) {
        try {
            String firstname = username.substring(0, 1);
            int i = Integer.parseInt(firstname);
            username = "MC" + username;
        } catch (NumberFormatException var4) {
        }

        IrcManager.username = username;
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        String[] parts = message.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String[] args = CommandUtil.removeElement(parts, 0);
        if (message.contains("say")) {
            if (args[0] != null) {
                message = message.replace("say ", "");
                Wrapper.GetMC().player.sendChatMessage(message);
            }
        } else if (message.contains("announce")) {
            if (args[0] != null) {
                message = message.replace("announce ", "");
                SalHack.SendMessage(ChatFormatting.DARK_RED + "[ANNOUNCMENT] " + ChatFormatting.RED + message);
            }
        } else {
            SalHack.SendMessage(TextFormatting.BLUE + "[IRC] " + TextFormatting.GREEN + sender + ": " + IRCModule.getChatColor() + message);
        }
    }

    public void connect() {
        this.setAutoNickChange(true);
        this.setName(username);
        this.changeNick(username);

        try {
            this.connect("irc.freenode.net", 6667);
        } catch (NickAlreadyInUseException var2) {
            var2.printStackTrace();
        } catch (IOException var3) {
            var3.printStackTrace();
        } catch (IrcException var4) {
            var4.printStackTrace();
        }

        this.joinChannel("#negroware");
    }
}
