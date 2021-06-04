package me.ionar.salhack.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.Unpooled;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommandUtil {
   public static String PREFIX = ".";
   public static TileEntityShulkerBox sb;

   @SubscribeEvent
   public void onChat(ClientChatEvent event) {
      String command = event.getMessage().toString().toLowerCase();
      System.out.println(command);
      Wrapper.GetMC().ingameGUI.getChatGUI().addToSentMessages(command);
      if (command.startsWith(PREFIX)) {
         String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
         String[] args = removeElement(parts, 0);
         String[] partss = event.getMessage().toString().split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
         String[] argss = removeElement(partss, 0);
         event.setCanceled(true);
         if (command.contains(PREFIX + "help")) {
            System.out.println(command + " is help command");
            SalHack.SendMessage(ChatFormatting.AQUA + "------- Zoe Hack -------\n" + ChatFormatting.GREEN + PREFIX + "help\n" + ChatFormatting.GREEN + PREFIX + "prefix <character>\n" + ChatFormatting.GREEN + PREFIX + "friend <add/remove> <player>\n" + ChatFormatting.GREEN + PREFIX + "say <message>\n" + ChatFormatting.GREEN + PREFIX + "shrug\n" + ChatFormatting.GREEN + PREFIX + "heart\n" + ChatFormatting.GREEN + PREFIX + "fakemsg <player> <message>\n" + ChatFormatting.GREEN + PREFIX + "vanish\n" + ChatFormatting.GREEN + PREFIX + "dupebook\n" + ChatFormatting.GREEN + PREFIX + "toggle <module>\n" + ChatFormatting.AQUA + "------------------------");
         } else if (command.contains(PREFIX + "friend")) {
            if (args[0] == null || args[1] == null) {
               SalHack.SendMessage(ChatFormatting.RED + PREFIX + "friend <add/remove> <player>");
               return;
            }

            if (args[0].equals("add")) {
               if (!FriendManager.Get().IsFriend(args[1])) {
                  FriendManager.Get().AddFriend(args[1]);
                  SalHack.SendMessage(ChatFormatting.GREEN + "Added " + args[1] + " as a friend.");
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + args[1] + " is already a friend.");
               }
            }

            if (args[0].equals("remove")) {
               if (FriendManager.Get().IsFriend(args[1])) {
                  FriendManager.Get().RemoveFriend(args[1]);
                  SalHack.SendMessage(ChatFormatting.RED + "Removed " + args[1] + " as a friend.");
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + args[1] + " is not a friend.");
               }
            }
         } else {
            String[] var8;
            int var9;
            int var10;
            String arg;
            StringBuilder message;
            if (command.contains(PREFIX + "say")) {
               message = new StringBuilder();
               var8 = args;
               var9 = args.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  arg = var8[var10];
                  if (arg != null) {
                     message.append(" ").append(arg);
                  }
               }

               Wrapper.GetMC().player.sendChatMessage(message.toString());
            } else if (command.contains(PREFIX + "shrug")) {
               Wrapper.GetMC().player.sendChatMessage("¯\\_(ツ)_/¯");
            } else if (command.contains(PREFIX + "heart")) {
               Wrapper.GetMC().player.sendChatMessage("❤");
            } else if (command.contains(PREFIX + "dupebook")) {
               ItemStack heldItem = Wrapper.GetMC().player.inventory.getCurrentItem();
               if (heldItem.getItem() instanceof ItemWritableBook) {
                  IntStream characterGenerator = (new Random()).ints(128, 1112063).map((i) -> {
                     return i < 55296 ? i : i + 2048;
                  });
                  NBTTagList pages = new NBTTagList();
                  String joinedPages = (String)characterGenerator.limit(10500L).mapToObj((i) -> {
                     return String.valueOf((char)i);
                  }).collect(Collectors.joining());

                  for(int page = 0; page < 50; ++page) {
                     pages.appendTag(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
                  }

                  if (heldItem.hasTagCompound()) {
                     assert heldItem.getTagCompound() != null;

                     heldItem.getTagCompound().setTag("pages", pages);
                     heldItem.getTagCompound().setTag("title", new NBTTagString(""));
                     heldItem.getTagCompound().setTag("author", new NBTTagString(Wrapper.GetMC().player.getName()));
                  } else {
                     heldItem.setTagInfo("pages", pages);
                     heldItem.setTagInfo("title", new NBTTagString(""));
                     heldItem.setTagInfo("author", new NBTTagString(Wrapper.GetMC().player.getName()));
                  }

                  PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                  buf.writeItemStack(heldItem);
                  Wrapper.GetMC().player.connection.sendPacket(new CPacketCustomPayload("MC|BEdit", buf));
                  SalHack.SendMessage(ChatFormatting.GREEN + "Dupe book generated.");
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + "You must be holding a writable book.");
               }
            } else if (command.contains(PREFIX + "fakemsg")) {
               if (args[0] == null || args[1] == null) {
                  SalHack.SendMessage(ChatFormatting.RED + PREFIX + "fakemsg <player> <message>");
                  return;
               }

               message = new StringBuilder();
               var8 = argss;
               var9 = argss.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  arg = var8[var10];
                  if (arg != null) {
                     message.append(" ").append(arg);
                  }
               }

               if (args[1] != null) {
                  SalHack.SendMessage("<" + argss[0] + "> " + message);
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + PREFIX + "fakemsg <player> <message>");
               }
            } else if (command.contains(PREFIX + "vanish")) {
               Entity vehicle = null;
               if (Wrapper.GetMC().player.getRidingEntity() != null && vehicle == null) {
                  vehicle = Wrapper.GetMC().player.getRidingEntity();
                  Wrapper.GetMC().player.dismountRidingEntity();
                  Wrapper.GetMC().world.removeEntityFromWorld(vehicle.getEntityId());
                  SalHack.SendMessage(ChatFormatting.GREEN + "Vehicle " + vehicle.getName() + " removed.");
               } else if (vehicle != null) {
                  vehicle.isDead = false;
                  Wrapper.GetMC().world.addEntityToWorld(vehicle.getEntityId(), vehicle);
                  Wrapper.GetMC().player.startRiding(vehicle, true);
                  SalHack.SendMessage(ChatFormatting.RED + "Vehicle " + vehicle.getName() + " created.");
                  vehicle = null;
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + "No Vehicle.");
               }
            } else if (command.contains(PREFIX + "toggle")) {
               if (args[0] == null) {
                  SalHack.SendMessage(ChatFormatting.RED + PREFIX + "toggle <module>");
                  return;
               }

               SalHack.SendMessage(ChatFormatting.GREEN + "Toggled " + args[0]);
               Module l_Mod = ModuleManager.Get().GetModLike(args[0]);
               if (l_Mod != null) {
                  l_Mod.toggle();
                  SalHack.SendMessage(String.format("%sToggled %s", l_Mod.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED, l_Mod.GetArrayListDisplayName()));
               } else {
                  SalHack.SendMessage(String.format("%sCould not find the module named %s", ChatFormatting.RED, args[1]));
               }
            } else if (command.contains(PREFIX + "prefix")) {
               System.out.println(command + " is prefix command and " + args[0] + " is the prefix to be");
               if (args[0] == null) {
                  SalHack.SendMessage(ChatFormatting.RED + "Please specify a new prefix!");
                  return;
               }

               if (args[0] != null) {
                  PREFIX = args[0];
                  SalHack.SendMessage(ChatFormatting.GREEN + "Prefix set to " + ChatFormatting.BLUE + PREFIX);
               } else {
                  SalHack.SendMessage(ChatFormatting.RED + "Please specify a new prefix!");
               }
            } else {
               SalHack.SendMessage(ChatFormatting.RED + "Unknown command! " + ChatFormatting.GOLD + PREFIX + "help for help");
            }
         }

      }
   }

   public static String[] removeElement(String[] input, int indexToDelete) {
      List result = new LinkedList();

      for(int i = 0; i < input.length; ++i) {
         if (i != indexToDelete) {
            result.add(input[i]);
         }
      }

      return (String[])((String[])result.toArray(input));
   }
}
