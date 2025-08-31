package io.github.mcengine.extension.library.economy.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.economy.extension.library.IMCEngineEconomyLibrary;

import io.github.mcengine.extension.library.economy.example.command.EconomyLibraryCommand;
import io.github.mcengine.extension.library.economy.example.listener.EconomyLibraryListener;
import io.github.mcengine.extension.library.economy.example.tabcompleter.EconomyLibraryTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Economy Library example module.
 * <p>
 * Registers the {@code /economylibraryexample} command and related event listeners.
 */
public class ExampleEconomyLibrary implements IMCEngineEconomyLibrary {

    /**
     * Custom extension logger for this module, with contextual labeling.
     */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Economy Library example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "Library", "EconomyExampleLibrary");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EconomyLibraryListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /economylibraryexample command
            Command economyLibraryExampleCommand = new Command("economylibraryexample") {

                /**
                 * Handles command execution for /economylibraryexample.
                 */
                private final EconomyLibraryCommand handler = new EconomyLibraryCommand();

                /**
                 * Handles tab-completion for /economylibraryexample.
                 */
                private final EconomyLibraryTabCompleter completer = new EconomyLibraryTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            economyLibraryExampleCommand.setDescription("Economy Library example command.");
            economyLibraryExampleCommand.setUsage("/economylibraryexample");

            // Dynamically register the /economylibraryexample command
            commandMap.register(plugin.getName().toLowerCase(), economyLibraryExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEconomyLibrary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Economy Library example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-economy-library-example");
    }
}
