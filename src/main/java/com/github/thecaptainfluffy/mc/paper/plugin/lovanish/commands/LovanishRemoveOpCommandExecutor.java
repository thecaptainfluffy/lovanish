package com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands;

import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.Lovanish;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LovanishRemoveOpCommandExecutor implements TabExecutor {

   private final Lovanish plugin;
   private final IEssentials ess;

   public LovanishRemoveOpCommandExecutor(Lovanish plugin, IEssentials essentials) {
      this.plugin = plugin;
      this.ess = essentials;
   }

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player) {
         Player player = (Player)sender;

         if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /lovanish-remove-blacklist <player>");
            return true;
         }

         if (player.hasPermission("lovanish.removeblacklist")) {
            List<String> opsBlackList = new ArrayList<>(plugin.getLovanishResource(Lovanish.OPS_BLACKLIST)
                .getAsJsonArray().asList().stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toList()));

            for (String nickname: args) {
               IUser removeUser = ess.getUser(nickname);
               if (removeUser == null) {
                  player.sendMessage(ChatColor.RED + nickname + " does not exist.");
                  continue;
               }
               if (!opsBlackList.contains(nickname)) {
                  player.sendMessage(ChatColor.YELLOW + nickname + " is already on the blacklist.");
                  continue;
               }
               opsBlackList.remove(nickname);
               player.sendMessage(ChatColor.GREEN + player.getName() + " removed " + nickname + " from the Lovanish blacklist");
            }
            plugin.saveTempLovanishResource(Lovanish.OPS_BLACKLIST, JsonParser.parseString(opsBlackList.toString()).getAsJsonArray());
            return true;
         }
      }
      return false;
   }

   @Override
   public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player) {
         // Only provide tab-completion if the sender is a player
         Player player = (Player) sender;

         // Check player permissions or any other condition for tab-completion
         if (player.hasPermission("lovanish.removeblacklist")) {
            List<String> suggestions = new ArrayList<>(plugin.getLovanishResource(Lovanish.OPS_BLACKLIST)
                    .getAsJsonArray().asList().stream()
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toList()));
            return suggestions;
         }
      }

      // Return an empty list to disable tab-completion for non-OP players or non-players
      return new ArrayList<>();
   }
}
