package de.legoshi.lccore.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

// REFACTORED
public class ConfigAccessor {

    private static final Map<String, Object> fileLocks = new HashMap<>();

    private final String fileName;
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration fileConfiguration;
    private final Object fileLock;


    public ConfigAccessor(JavaPlugin plugin, String fileName) {
        if (plugin == null) throw new IllegalArgumentException("plugin cannot be null");
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
        this.fileLock = getFileLock(fileName);
    }

    private static synchronized Object getFileLock(String fileName) {
        return fileLocks.computeIfAbsent(fileName, k -> new Object());
    }

    public ConfigAccessor(JavaPlugin plugin, File folder, String fileName) {
        if (plugin == null) throw new IllegalArgumentException("plugin cannot be null");
        this.plugin = plugin;
        this.fileName = fileName;
        if (folder == null) throw new IllegalStateException();
        this.configFile = new File(folder, fileName);
        this.fileLock = getFileLock(fileName);
    }

    public synchronized void reloadConfig() {
        synchronized (fileLock) {
            this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
            InputStream defConfigStream = this.plugin.getResource(this.fileName);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
                this.fileConfiguration.setDefaults(defConfig);
            }
        }
    }

    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) reloadConfig();
        return this.fileConfiguration;
    }

    public synchronized void saveConfig() {
        if (this.fileConfiguration != null && this.configFile != null) {
            synchronized (fileLock) {
                try {
                    if (!this.configFile.exists()) {
                        this.configFile.createNewFile();
                    }
                    if (this.configFile.exists()) {
                        getConfig().save(this.configFile);
                    }
                } catch (IOException var2) {
                    this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, var2);
                }
            }
        }
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
    }
}

