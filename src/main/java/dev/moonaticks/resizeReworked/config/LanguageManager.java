package dev.moonaticks.resizeReworked.config;

import dev.moonaticks.resizeReworked.ResizeReworked;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    
    private final ResizeReworked plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public LanguageManager(ResizeReworked plugin) {
        this.plugin = plugin;
    }
    
    public void loadLanguage() {
        createLanguageFiles();
        loadMessages();
    }
    
    private void createLanguageFiles() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        
        createLanguageFile(langDir, "en_en.yml");
        createLanguageFile(langDir, "ru_ru.yml");
    }
    
    private void createLanguageFile(File langDir, String fileName) {
        File langFile = new File(langDir, fileName);
        if (!langFile.exists()) {
            try (InputStream in = plugin.getResource("lang/" + fileName)) {
                if (in != null) {
                    Files.copy(in, langFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    plugin.getLogger().warning("Ресурс для языка " + fileName + " не найден.");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Не удалось создать файл языка: " + fileName);
            }
        }
    }
    
    private void loadMessages() {
        messages.clear();
        String language = plugin.getConfigManager().getLanguage();
        File langFile = new File(plugin.getDataFolder(), "lang/" + language + ".yml");
        
        if (langFile.exists()) {
            try {
                FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
                if (langConfig.contains("messages")) {
                    for (String key : langConfig.getConfigurationSection("messages").getKeys(false)) {
                        String message = langConfig.getString("messages." + key);
                        if (message != null) {
                            messages.put(key, message);
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Ошибка при загрузке языкового файла: " + language);
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().warning("Языковой файл не найден, используется язык по умолчанию.");
        }
    }
    
    public String getMessage(String key, String... placeholders) {
        String message = messages.getOrDefault(key, "§4[Ошибка: сообщение не найдено]");
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        return message;
    }
    
    public Component getMessageComponent(String key, String... placeholders) {
        String message = getMessage(key, placeholders);
        return miniMessage.deserialize(message);
    }
    
    public String formatMessage(String message) {
        return plugin.getConfigManager().getPrefix() + message;
    }
    
    public Component formatMessageComponent(String message) {
        String prefix = plugin.getConfigManager().getPrefix();
        Component prefixComponent = miniMessage.deserialize(prefix);
        Component messageComponent = miniMessage.deserialize(message);
        return prefixComponent.append(messageComponent);
    }
    
    public Component formatMessageComponent(Component messageComponent) {
        String prefix = plugin.getConfigManager().getPrefix();
        Component prefixComponent = miniMessage.deserialize(prefix);
        return prefixComponent.append(messageComponent);
    }
    
    public void reloadLanguage() {
        loadMessages();
    }
}
