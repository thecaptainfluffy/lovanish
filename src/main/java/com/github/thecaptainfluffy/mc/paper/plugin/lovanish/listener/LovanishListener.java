package com.github.thecaptainfluffy.mc.paper.plugin.lovanish.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.Lovanish;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LovanishListener implements Listener {

   private final Lovanish plugin;
   private final IEssentials ess;

   public LovanishListener(Lovanish plugin, IEssentials ess) {
	  this.plugin = plugin;
	  this.ess = ess;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
	  // Your code to handle the jPlayer join event
	  List<String> opsBlackList = new ArrayList<>(plugin.getLovanishResource(Lovanish.OPS_BLACKLIST)
		  .getAsJsonArray().asList().stream()
		  .map(JsonElement::getAsString)
		  .collect(Collectors.toList()));
	  Player jPlayer = event.getPlayer();
	  IUser jUser = ess.getUser(jPlayer);
	  if (jPlayer.isOp() && jUser.isVanished()) {
		 event.joinMessage(null);
		 for (IUser user : ess.getOnlineUsers()) {
			if (!user.getBase().isOp() || opsBlackList.contains(user.getName())) {
			   user.getBase().hidePlayer(plugin, jPlayer);
			}
		 }
	  }
	  if (jPlayer.isOp() && opsBlackList.contains(jPlayer.getName())) {
		 for(IUser user : ess.getOnlineUsers()) {
			if (user.getBase().isOp() && user.isVanished()) {
			   jPlayer.hidePlayer(plugin, user.getBase());
			}
		 }
	  } else if (!jPlayer.isOp()) {
		 for(IUser user : ess.getOnlineUsers()) {
			if (user.getBase().isOp() && user.isVanished()) {
			   jPlayer.hidePlayer(plugin, user.getBase());
			}
		 }
	  }
   }

   @EventHandler
   public void onPlayerAdvancementCriterionGrant(PlayerAdvancementCriterionGrantEvent event) {
	  Player player = event.getPlayer();
	  IUser user = ess.getUser(player);
	  boolean hasAchievement = false;

	  // Check if the player is in the vanished state
	  if (user.isVanished()) {
		 String advancementId = event.getAdvancement().getKey().toString();

		 for (MetadataValue value : player.getMetadata("vanishedAchievementDisabled")) {
			if (value.asString().equals(advancementId)) {
			   hasAchievement = true;
			}
		 }
		 plugin.getLogger().info("Achivement is disabled");
		 event.setCancelled(true); // Cancel the advancement granting process
		 if (!hasAchievement) {
			player.sendMessage("Achievements disabled while in vanish.");

			// Set metadata to indicate that the message has been sent
			player.setMetadata("vanishedAchievementDisabled", new FixedMetadataValue(plugin, advancementId));
		 }
	  }
   }

}
