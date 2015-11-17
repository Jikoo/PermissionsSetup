package com.github.jikoo.permissionssetup;

import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionsSetup extends JavaPlugin {

	@Override
	public void onEnable() {
		saveDefaultConfig();

		ConfigurationSection plugins = this.getConfig().getConfigurationSection("plugins");
		for (String section : plugins.getKeys(false)) {

			ConfigurationSection pluginSection = plugins.getConfigurationSection(section);

			// Ensure a valid format is created
			if (!pluginSection.isString("format")) {
				this.getLogger().warning("No permission format set for plugin " + section + ", skipping!");
				continue;
			}
			String format = pluginSection.getString("format");
			try {
				String.format(format, "test");
			} catch (IllegalFormatException e) {
				this.getLogger().warning("Invalid format set for plugin " + section + ", skipping!");
				continue;
			}

			Plugin plugin = getServer().getPluginManager().getPlugin(section);

			// Is plugin enabled? Null check is handled by instanceof.
			if (!(plugin instanceof JavaPlugin)) {
				continue;
			}

			addCommandPermissions((JavaPlugin) plugin, format, pluginSection.getStringList("wildcards"));
		}
	}

	private void addCommandPermissions(JavaPlugin plugin, String format, List<String> wildcards) {
		Collection<String> commands = plugin.getDescription().getCommands().keySet();

		if (commands.isEmpty()) {
			return;
		}

		for (String command : commands) {
			String perm = String.format(format, command);
			// Note: Bukkit#getPluginCommand will return a different plugin's command if overridden.
			// This method will always get the correct command.
			plugin.getCommand(command).setPermission(perm);

			for (String wildcard : wildcards) {
				Permission child;
				try {
					child = new Permission(perm, PermissionDefault.FALSE);
					getServer().getPluginManager().addPermission(child);
				} catch (IllegalArgumentException e) {
					child = getServer().getPluginManager().getPermission(perm);
				}
				child.addParent(wildcard, true);
			}
		}

		for (String wildcard : wildcards) {
			getServer().getPluginManager().getPermission(wildcard).recalculatePermissibles();
		}
	}

}
