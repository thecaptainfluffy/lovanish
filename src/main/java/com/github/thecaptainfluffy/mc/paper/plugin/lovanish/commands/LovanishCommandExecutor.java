package com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands;

import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.Lovanish;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LovanishCommandExecutor implements TabExecutor {

   private final Lovanish plugin;
   private final IEssentials ess;

   public LovanishCommandExecutor(Lovanish plugin, IEssentials essentials) {
      this.plugin = plugin;
      this.ess = essentials;
   }

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         IUser user = ess.getUser(player);

         if (player.hasPermission("lovanish.use")) {
            // Store the before state
            boolean isVanished = user.isVanished();

            // Fetch the ops blacklist
            List<String> opsBlackList = plugin.getLovanishResource(Lovanish.OPS_BLACKLIST)
                .getAsJsonArray().asList().stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());

            // Handle what should happen when user change vanish state
            if (isVanished) {
               player.playerListName(Component.text(player.getName()));
               user.setVanished(false);
               for (String blockOp : opsBlackList) {
                  ess.getUser(blockOp).getBase().showPlayer(plugin, player);
               }
               player.removeMetadata("vanishedAchievementDisabled", plugin);
               Bukkit.getServer().broadcast(Component.text(player.getName() + " joined the game", Style.style(TextColor.color(255, 255, 102))));
            } else {
               player.playerListName(Component.text("(V) " + player.getName(), Style.style(TextColor.color(216,191,216))));
               user.setVanished(true);
               for (String blockOp : opsBlackList) {
                  ess.getUser(blockOp).getBase().hidePlayer(plugin, player);
               }
               Bukkit.getServer().broadcast(Component.text(player.getName() + " left the game", Style.style(TextColor.color(255, 255, 102))));

            }
            return true;
         }
      }
      return false;
   }

   @Override
   public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      return new ArrayList<>();
   }
}
