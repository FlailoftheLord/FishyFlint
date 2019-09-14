package me.flail.fishyflint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FishyFlint extends JavaPlugin implements Listener {

	public boolean useOnPlayers = false;
	public int durabilityCost = 1;
	public int burnDuration = 8;

	public Set<UUID> cooldowns = new HashSet<>();

	@Override
	public void onEnable() {
		saveDefaultConfig();
		updateSettings();

		getCommand("fishyflint").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	void updateSettings() {
		useOnPlayers = getConfig().getBoolean("AllowOnPlayers", false);
		durabilityCost = getConfig().getInt("DurabilityCost", 1);
		burnDuration = getConfig().getInt("BurnDuration", 8);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("fishyflint")) {
			return true;
		}

		if (!sender.hasPermission("fishyflint.admin")) {
			sender.sendMessage(chat("&cYou don't have permission for this command!"));
			return true;
		}

		switch (args.length) {
		case 0:
			sender.sendMessage(chat("&cUsage&8: &7/" + label + " [reload|info]"));

			break;
		case 1:
			switch (args[0].toLowerCase()) {
			case "info":
				sender.sendMessage(chat("&3&lFishy&6&lFlint &7v" + getDescription().getVersion() + "&8, api-version&8: &7"
						+ getDescription().getAPIVersion() + " &2by " + getDescription().getAuthors().get(0)));
				sender.sendMessage(chat(
						"&a&lCommand&8: &7/fishyflint &8[&7reload &8| &7info&8]  &8(&7/"
								+ getCommand("fishyflint").getAliases().get(0) + "&8)"));
				sender.sendMessage(chat("&a&lPermissions&8:"));
				sender.sendMessage(chat("  &7fishyflint.use &8- &7permission to use flint on entities."));
				sender.sendMessage(chat("  &7fishyflint.admin &8- &7allows you to use the &o/fishyflint&r &7command"));

				break;
			case "reload":
				reloadConfig();
				updateSettings();

				sender.sendMessage(chat("&a&lReloaded &3Fishy&6Flint &asettings."));
				break;

			}

		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length < 2) {
			list.add("reload");
			list.add("info");
		}

		for (String s : list.toArray(new String[] {})) {
			if (!s.startsWith(args[args.length - 1].toLowerCase())) {

				list.remove(s);
			}
		}

		return list;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerInteract(PlayerInteractEntityEvent event) {
		if (event.getPlayer().hasPermission("fishyflint.use")) {

			new FlintListener(event.getPlayer(), event.getRightClicked()).check();
		}

	}

	String chat(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
