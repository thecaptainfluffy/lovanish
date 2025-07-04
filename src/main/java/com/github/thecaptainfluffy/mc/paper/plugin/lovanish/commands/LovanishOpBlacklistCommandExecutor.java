package com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands;

import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.Lovanish;
import com.google.gson.JsonElement;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LovanishOpBlacklistCommandExecutor implements TabExecutor {

   private final Lovanish plugin;
   private final IEssentials ess;

   public LovanishOpBlacklistCommandExecutor(Lovanish plugin, IEssentials essentials) {
      this.plugin = plugin;
      this.ess = essentials;
   }

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         IUser user = ess.getUser(player);

         if (player.isOp()) {
            List<String> opsBlackList = new ArrayList<>(plugin.getLovanishResource(Lovanish.OPS_BLACKLIST)
                .getAsJsonArray().asList().stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toList()));

            player.sendMessage("List of blacklisted op users from vanish plugin");
            for (String nickname: opsBlackList) {
               player.sendMessage(nickname);
            }
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
         if (player.isOp()) {
            // Return a list of tab-completion suggestions based on args or any other criteria
            List<String> suggestions = new ArrayList<>();
            suggestions.add("lovanish-list");
            // Add more suggestions based on your needs

            return suggestions;
         }
      }

      // Return an empty list to disable tab-completion for non-OP players or non-players
      return new ArrayList<>();
   }
}
