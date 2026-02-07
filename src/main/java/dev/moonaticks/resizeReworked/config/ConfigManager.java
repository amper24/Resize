package dev.moonaticks.resizeReworked.config;

import dev.moonaticks.resizeReworked.ResizeReworked;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final ResizeReworked plugin;
    private double defaultMinScale;
    private double defaultMaxScale;
    private double extendedMinScale;
    private double extendedMaxScale;
    private int cooldownSeconds;
    private int resizeSteps;
    private String language;
    private String prefix;
    
    public ConfigManager(ResizeReworked plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        defaultMinScale = config.getDouble("default_min_scale", 0.8);
        defaultMaxScale = config.getDouble("default_max_scale", 1.15);
        extendedMinScale = config.getDouble("extended_min_scale", 0.0625);
        extendedMaxScale = config.getDouble("extended_max_scale", 16.0);
        cooldownSeconds = config.getInt("cooldown_seconds", 30);
        resizeSteps = config.getInt("resize_steps", 20);
        language = config.getString("language", "en_en");
        prefix = config.getString("prefix", "§d[§5Resize§d] ");
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
    
    // Геттеры
    public double getDefaultMinScale() {
        return defaultMinScale;
    }
    
    public double getDefaultMaxScale() {
        return defaultMaxScale;
    }
    
    public double getExtendedMinScale() {
        return extendedMinScale;
    }
    
    public double getExtendedMaxScale() {
        return extendedMaxScale;
    }
    
    public int getCooldownSeconds() {
        return cooldownSeconds;
    }
    
    public int getResizeSteps() {
        return resizeSteps;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public String getPrefix() {
        return prefix;
    }
}
