package com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands;

import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.Lovanish;
import com.google.gson.*;
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

public class LovanishAddOpCommandExecutor implements TabExecutor {

   private final Lovanish plugin;
   private final IEssentials ess;

   public LovanishAddOpCommandExecutor(Lovanish plugin, IEssentials essentials) {
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

            for (String nickname: args) {
               IUser addUser = ess.getUser(nickname);
               if (addUser != null && !opsBlackList.contains(nickname) && addUser.getBase().isOp()) {
                  opsBlackList.add(nickname);
                  player.sendMessage(player.getName() + " have added " + nickname + " to the blacklist of lovanish");
               } else {
                  player.sendMessage(nickname + " doesn't exist");
               }
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
         if (player.isOp()) {
            // Return a list of tab-completion suggestions based on args or any other criteria
            List<String> suggestions = new ArrayList<>();
            suggestions.add("lovanish-add-op");
            // Add more suggestions based on your needs

            return suggestions;
         }
      }

      // Return an empty list to disable tab-completion for non-OP players or non-players
      return new ArrayList<>();
   }
}
