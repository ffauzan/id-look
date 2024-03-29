package uk.haku.idlook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import emu.grasscutter.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import uk.haku.idlook.commands.*;
import uk.haku.idlook.IdLookPlugin;
import uk.haku.idlook.objects.PluginConfig;

/**
 * The Grasscutter plugin template.
 * This is the main class for the plugin.
 */
public final class IdLookPlugin extends Plugin {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /* Turn the plugin into a singleton. */
    private static IdLookPlugin instance;

    /**
     * Gets the plugin instance.
     * 
     * @return A plugin singleton.
     */
    public static IdLookPlugin getInstance() {
        return instance;
    }

    /* The plugin's configuration instance. */
    private PluginConfig configuration;

    /* Player language preference Map */
    private Map<String, String> playerLang;

    /**
     * This method is called immediately after the plugin is first loaded into
     * system memory.
     */
    @Override
    public void onLoad() {
        // Set the plugin instance.
        instance = this;

        // Get the configuration file.
        var configFile = new File(this.getDataFolder(), "config.json");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile())
                    throw new IOException("Failed to create config file.");
                Files.write(configFile.toPath(), gson.toJson(new PluginConfig()).getBytes());
            } catch (IOException ignored) {
                this.getLogger().error("Unable to save configuration file.");
            }
        }

        try { // Load configuration file.
            this.configuration = gson.fromJson(new FileReader(configFile), PluginConfig.class);
        } catch (IOException ignored) {
            this.getLogger().error("Unable to load configuration file.");
            this.configuration = new PluginConfig();
        }

        // Initiate player languange map
        this.playerLang = new HashMap<String, String>();

        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been loaded.");
    }

    /**
     * This method is called before the servers are started, or when the plugin
     * enables.
     */
    @Override
    public void onEnable() {
        // Register commands.
        this.getHandle().registerCommand(new LookCommand());

        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been enabled.");
    }

    /**
     * This method is called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been disabled.");
    }

    /**
     * Gets the plugin's configuration.
     * 
     * @return A plugin config instance.
     */
    public PluginConfig getConfiguration() {
        return this.configuration;
    }

    /* Get player language map */
    public Map<String, String> getPlayerLangMap() {
        return this.playerLang;
    }

    /* Remove player language from map */
    public void removePlayerLang(String accountId) {
        this.playerLang.remove(accountId);
    }

    /* Add player language to map */
    public void addPlayerLang(String accountId, String lang) {
        this.playerLang.put(accountId, lang);
    }

    /* Get player language from map */
    public String getPlayerLang(String accountId) {
        return this.playerLang.get(accountId);
    }
}
