package com.github.thecaptainfluffy.mc.paper.plugin.lovanish;

import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands.LovanishAddOpCommandExecutor;
import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands.LovanishCommandExecutor;
import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands.LovanishOpBlacklistCommandExecutor;
import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.commands.LovanishRemoveOpCommandExecutor;
import com.github.thecaptainfluffy.mc.paper.plugin.lovanish.listener.LovanishListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.ess3.api.IEssentials;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Lovanish extends JavaPlugin implements Listener {

   public static final String OPS_BLACKLIST = "ops-blacklist.json";
   private File pluginFolder;

   private Map<String, JsonElement> fileAndResource;

   public Lovanish() {
	  super();
   }

   @Override
   public void onEnable() {
	  fileAndResource = new HashMap<>();
	  pluginFolder = getDataFolder();
	  // Create lovanish folder
	  if (!pluginFolder.exists()) {
		 getLogger().info("Create Lovanish plugin folder");
		 pluginFolder.mkdirs();
	  }

	  createLovanishResource(Lovanish.OPS_BLACKLIST, "[]");

	  // Get dependent plugins
	  IEssentials ess = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");

	  // Register command and listener
	  getCommand("lovanish").setExecutor(new LovanishCommandExecutor(this, ess));
	  getCommand("lovanish-add-blacklist").setExecutor(new LovanishAddOpCommandExecutor(this, ess));
	  getCommand("lovanish-remove-blacklist").setExecutor(new LovanishRemoveOpCommandExecutor(this, ess));
	  getCommand("lovanish-blacklist").setExecutor(new LovanishOpBlacklistCommandExecutor(this, ess));
	  getServer().getPluginManager().registerEvents(this, this);
	  getServer().getPluginManager().registerEvents(new LovanishListener(this, ess), this);
   }

   @Override
   public void onDisable() {
	  saveLovanishResources();
   }

   @EventHandler
   public void onAutoSave(WorldSaveEvent event) {
	  // Your code to be executed when the server autosaves
	  // For example, you can call a method or execute a task
	  saveLovanishResources();
   }

   /**
	* Create new file to store config if it doesn't exist
	*
	* @param fileName: the filename the config is called
	* @param content: What content should be saved in the filename
	*/
   public void createLovanishResource(String fileName, String content) {
	  File file = new File(pluginFolder, fileName);
	  if (!file.exists()) {
		 try {
			if (file.createNewFile()) {
			   FileWriter write = new FileWriter(file);
			   write.write(content);
			   write.close();
			   getLogger().info("Create the file " + fileName + " in Lovanish plugin folder");
			}
		 } catch (IOException e) {
			getLogger().severe(e.getMessage());
		 }
	  }
   }

   /**
	* Retrieve necessary file.
	*
	* @param fileName: filename of the config file
	* @return JsonElement
	*/
   public JsonElement getLovanishResource(String fileName) {
	  if(fileAndResource.containsKey(fileName)) {
		 return fileAndResource.get(fileName);
	  }

	  pluginFolder = getDataFolder();
	  File file = new File(pluginFolder, fileName);

	  try (FileReader fileReader = new FileReader(file)) {
		 // Check if the file exists
		 if (!file.exists()) {
			createLovanishResource(fileName, "");
			getLogger().warning("The file " + fileName + " does not exist.");
			return null;
		 }

		 JsonElement element = JsonParser.parseReader(fileReader);
		 fileAndResource.put(fileName, element);
		 fileReader.close();

		 // Parse the JSON array
		 return element;
	  } catch (Exception e) {
		 getLogger().severe(e.getMessage());
	  }
	  return null;
   }

   public void saveTempLovanishResource(String fileName, JsonElement content) {
	  if(fileAndResource.containsKey(fileName)) {
		 fileAndResource.put(fileName, content);
	  }
   }

   public void saveLovanishResources() {
	  for(Map.Entry<String, JsonElement> entry : fileAndResource.entrySet()) {
		 getLogger().info("Update file " + entry.getKey());
		 if (entry.getValue() instanceof JsonArray) {
			saveLovanishResource(entry.getKey(), entry.getValue().getAsJsonArray().toString());
			getLogger().info(entry.getValue().getAsJsonArray().toString());
		 } else {
			saveLovanishResource(entry.getKey(), entry.getValue().toString());
			getLogger().info(entry.getValue().toString());
		 }
	  }
   }

   private void saveLovanishResource(String fileName, String content) {
	  File file = new File(pluginFolder, fileName);
	  try {
		 FileWriter write = new FileWriter(file);
		 write.write(content);
		 write.close();
		 getLogger().info("Create the file " + fileName + " in Lovanish plugin folder");
	  } catch (IOException e) {
		 getLogger().severe(e.getMessage());
	  }
   }
}
