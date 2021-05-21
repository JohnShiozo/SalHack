package me.ionar.salhack.command.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import baritone.api.BaritoneAPI;

public class BaritoneCommand extends Command
{
  public BaritoneCommand()
  {
    super("b", "Baritone API Pog");
  }
  @Override
  public void
  execute(String command, String[] args) throws Exception
  {
    BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute(String.join(" ", args));
  }
}