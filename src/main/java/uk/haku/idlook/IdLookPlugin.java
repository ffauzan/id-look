package uk.haku.idlook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.utils.Utils;
import uk.haku.idlook.commands.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;


import java.util.*;

import static emu.grasscutter.Configuration.*;


import com.google.common.reflect.TypeToken;

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
     * @return A plugin singleton.
     */
    public static IdLookPlugin getInstance() {
        return instance;
    }
    
    /* The plugin's configuration instance. */
    private PluginConfig configuration;

    /* Item text map */
    private Map<Long, String> itemTextMap;

    
    /**
     * This method is called immediately after the plugin is first loaded into system memory.
     */
    @Override public void onLoad() {
        // Set the plugin instance.
        instance = this;
        
        // Get the configuration file.
        var configFile = new File(this.getDataFolder(), "config.json");
        if(!configFile.exists()) {
            try {
                if(!configFile.createNewFile())
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

        // Initialize the item text map.
        final String textMapFile = "TextMap/TextMap" + DOCUMENT_LANGUAGE + ".json";
        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(
                Utils.toFilePath(RESOURCE(textMapFile))), StandardCharsets.UTF_8)) {
            this.itemTextMap = Grasscutter.getGsonFactory()
                    .fromJson(fileReader, new TypeToken<Map<Long, String>>() {
                    }.getType());
        } catch (IOException e) {
            Grasscutter.getLogger().warn("Resource does not exist: " + textMapFile);
            this.itemTextMap = new HashMap<>();
        }
        
        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been loaded.");
    }

    /**
     * This method is called before the servers are started, or when the plugin enables.
     */
    @Override public void onEnable() {
        // Register commands.
        this.getHandle().registerCommand(new LookCommand());

        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been loaded.");
    }

    /**
     * This method is called when the plugin is disabled.
     */
    @Override public void onDisable() {
        // Log a plugin status message.
        this.getLogger().info("The IdLook plugin has been loaded.");
    }

    /**
     * Gets the plugin's configuration.
     * @return A plugin config instance.
     */
    public PluginConfig getConfiguration() {
        return this.configuration;
    }

    /**
     * Gets the item text map.
     */
    public Map<Long, String> getItemTextMap() {
        return this.itemTextMap;
    }
}
