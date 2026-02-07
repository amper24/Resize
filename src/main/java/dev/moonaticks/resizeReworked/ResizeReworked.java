package dev.moonaticks.resizeReworked;

import dev.moonaticks.resizeReworked.commands.ResizeCommand;
import dev.moonaticks.resizeReworked.config.ConfigManager;
import dev.moonaticks.resizeReworked.config.LanguageManager;
import dev.moonaticks.resizeReworked.managers.ResizeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ResizeReworked extends JavaPlugin {
    
    private static ResizeReworked instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private ResizeManager resizeManager;
    
    @Override
    public void onEnable() {
        instance = this;

        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);
        this.resizeManager = new ResizeManager(this);
        
        // Загрузка конфигурации
        configManager.loadConfig();
        languageManager.loadLanguage();
        
        // Регистрация команд
        getCommand("resize").setExecutor(new ResizeCommand(this));
        getCommand("resize").setTabCompleter(new ResizeCommand(this));
        
        
        getLogger().info("ResizeReworked успешно включен!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ResizeReworked выключен!");
    }
    
    public static ResizeReworked getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
    
    public ResizeManager getResizeManager() {
        return resizeManager;
    }
}
