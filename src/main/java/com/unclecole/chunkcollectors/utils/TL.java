package com.unclecole.chunkcollectors.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public enum TL {
	NO_PERMISSION("messages.no-permission", "&cYou don't have the permission to do that."),
	INVALID_ARGUMENTS("messages.invalid-arguments", "&cInvalid Arguments: <command>"),
	INSUFFICENT_FUNDS("messages.insufficient-funds", "&4&lERROR! &fNot enough money! (%cost%)"),
	SOLD_ITEM("messages.sold-item", "&aShop > &fYou sold &a%amount% x %item% &ffor &a$%price%"),
	ALREADY_MAX_LEVEL("messages.already-max-level", "&cAlready max level"),
	GAVE_COLLECTORS("messages.gave-collectors", "&fYou've given %player% %amount% collectors!"),
	RECEIVED_COLLECTORS("messages.recieved-collectors", "&fYou received %amount% collectors!"),
	UPGRADED("messages.upgraded", "&a&lSUCCESS! &fUpgraded &a%type% &fto level &a%level%&f!"),
	PLACED_COLLECTOR("messages.placed-collector", "&a&lSUCCESS! &fYou successfully placed a collector!"),
	ALREADY_COLLECTOR_IN_CHUNK("messages.already-collector-in-chunk", "&cCollector already in chunk!"),
	MUST_BE_PLACED_ON_ISLAND("messages.must-be-placed-on-island", "&cMust be placed on your island!"),
	ACTIVATED_COLLECTOR_RADIUS("messages.activated_collector_radius", "&fYou successfully activated collector radius!"),
	DEACTIVATED_COLLECTOR_RADIUS("messages.deactivated_collector_radius", "&cYou successfully deactivated collector radius!");

	private final String path;

	private String def;
	private static ConfigFile config;

	TL(String path, String start) {
		this.path = path;
		this.def = start;
	}

	public String getDefault() {
		return this.def;
	}

	public String getPath() {
		return this.path;
	}

	public void setDefault(String message) {
		this.def = message;
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			sender.sendMessage(PlaceholderAPI.setPlaceholders(player, C.color(getDefault())));
		} else {
			sender.sendMessage(C.strip(getDefault()));
		}
	}

	public static void loadMessages(ConfigFile configFile) {
		config = configFile;
		FileConfiguration data = configFile.getConfig();
		for (TL message : values()) {
			if (!data.contains(message.getPath())) {
				data.set(message.getPath(), message.getDefault());
			}
		}
		configFile.save();
	}


	public void send(CommandSender sender, PlaceHolder... placeHolders) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			sender.sendMessage(PlaceholderAPI.setPlaceholders(player, C.color(getDefault(), placeHolders)));
		} else {
			sender.sendMessage(C.strip(getDefault(), placeHolders));
		}
	}

	public static void message(CommandSender sender, String message) {
		sender.sendMessage(C.color(message));
	}

	public static void message(CommandSender sender, String message, PlaceHolder... placeHolders) {
		sender.sendMessage(C.color(message, placeHolders));
	}

	public static void message(CommandSender sender, List<String> message) {
		message.forEach(m -> sender.sendMessage(C.color(m)));
	}

	public static void message(CommandSender sender, List<String> message, PlaceHolder... placeHolders) {
		message.forEach(m -> sender.sendMessage(C.color(m, placeHolders)));
	}

	public static void log(Level lvl, String message) {
		Bukkit.getLogger().log(lvl, message);
	}
}
