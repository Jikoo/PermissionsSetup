package com.github.jikoo.permissionssetup;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PermissionsSetup extends JavaPlugin {

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		new PostEnableSetup().runTask(this);

	}

	private class PostEnableSetup extends BukkitRunnable {

		public void run() {
			if (getConfig().isConfigurationSection("plugins")) {
				ConfigurationSection plugins = getConfig().getConfigurationSection("plugins");
				for (String section : plugins.getKeys(false)) {

					if (!plugins.isConfigurationSection(section)) {
						continue;
					}

					ConfigurationSection pluginSection = plugins.getConfigurationSection(section);

					// Ensure a valid format is created
					if (!pluginSection.isString("format")) {
						getLogger().warning("No permission format set for plugin " + section + ", skipping!");
						continue;
					}
					String format = pluginSection.getString("format");
					try {
						String.format(format, "test");
					} catch (IllegalFormatException e) {
						getLogger().warning("Invalid format set for plugin " + section + ", skipping!");
						continue;
					}

					Plugin plugin = getServer().getPluginManager().getPlugin(section);

					// Is plugin enabled? Null check is handled by instanceof.
					if (plugin instanceof JavaPlugin) {
						addCommandPermissions((JavaPlugin) plugin, format,
								pluginSection.getStringList("parents"));
					}
				}
			}

			if (getConfig().isConfigurationSection("commands")) {
				ConfigurationSection commands = getConfig().getConfigurationSection("commands");
				for (String section : commands.getKeys(false)) {

					if (!commands.isConfigurationSection(section)) {
						continue;
					}

					ConfigurationSection commandSection = commands.getConfigurationSection(section);

					if (!commandSection.isString("permission")) {
						getLogger().warning("No permission format set for plugin " + section + ", skipping!");
						continue;
					}

					addCommandPermissions(section, commandSection.getString("permission"),
							commandSection.getStringList("parents"));
				}
			}
		}

	}

	private void addCommandPermissions(JavaPlugin plugin, String format, List<String> parents) {
		Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();
		if (commands == null || commands.isEmpty()) {
			getLogger().warning(plugin.getName() + " does not have any commands registered in plugin.yml!");
			return;
		}

		for (String command : commands.keySet()) {
			String permission = String.format(format, command);
			// Note: Bukkit#getPluginCommand will return a different plugin's command if overridden.
			// This method will always get the correct command.
			plugin.getCommand(command).setPermission(permission);

			Permission child;
			try {
				child = new Permission(permission, PermissionDefault.OP);
				getServer().getPluginManager().addPermission(child);
			} catch (IllegalArgumentException e) {
				child = this.getServer().getPluginManager().getPermission(permission);
			}
			for (String parent : parents) {
				child.addParent(parent, true);
			}
		}

		for (String wildcard : parents) {
			getServer().getPluginManager().getPermission(wildcard).recalculatePermissibles();
		}
	}

	private void addCommandPermissions(String command, String permission, List<String> parents) {
		PluginCommand cmd = getServer().getPluginCommand(command);
		if (cmd == null) {
			getLogger().warning(command + " is not a registered plugin command!");
			return;
		}
		cmd.setPermission(permission);

		Permission child;
		try {
			child = new Permission(permission, PermissionDefault.OP);
			getServer().getPluginManager().addPermission(child);
		} catch (IllegalArgumentException e) {
			child = this.getServer().getPluginManager().getPermission(permission);
		}
		for (String parent : parents) {
			child.addParent(parent, true);
		}
	}

}
