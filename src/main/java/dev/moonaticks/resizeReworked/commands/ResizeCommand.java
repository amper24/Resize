package dev.moonaticks.resizeReworked.commands;

import dev.moonaticks.resizeReworked.ResizeReworked;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResizeCommand implements CommandExecutor, TabCompleter {
    
    private final ResizeReworked plugin;
    
    public ResizeCommand(ResizeReworked plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Обработка команды reload
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("resizeplugin.reload")) {
            plugin.getConfigManager().reloadConfig();
            plugin.getLanguageManager().reloadLanguage();
            sender.sendMessage(plugin.getLanguageManager().formatMessageComponent(
                plugin.getLanguageManager().getMessageComponent("config_reloaded")
            ));
            return true;
        }
        
        Player targetPlayer;
        double scale;
        
        // Определяем игрока, которому нужно изменить размер
        if (args.length >= 2 && sender.hasPermission("resizeplugin.resize.others")) {
            targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getLanguageManager().formatMessageComponent(
                    plugin.getLanguageManager().getMessageComponent("player_not_found", "player", args[1])
                ));
                return true;
            }
            // Преобразуем значение масштаба из первого аргумента
            try {
                scale = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                return sendMessage(sender, "invalid_number");
            }
        } else if (args.length == 1) {
            // Если команда выполнена без указания игрока, применяем размер к самому отправителю
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.getLanguageManager().formatMessageComponent(
                    plugin.getLanguageManager().getMessageComponent("only_player")
                ));
                return true;
            }
            targetPlayer = player;
            
            // Преобразуем значение масштаба из первого аргумента
            try {
                scale = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                return sendMessage(player, "invalid_number");
            }
        } else {
            return sendMessage(sender, "usage");
        }
        
        // Устанавливаем пределы изменения масштаба в зависимости от прав отправителя
        boolean hasExtendedPermission = sender.hasPermission("resizeplugin.resize.extended");
        double minScale = plugin.getResizeManager().getMinScale(hasExtendedPermission);
        double maxScale = plugin.getResizeManager().getMaxScale(hasExtendedPermission);
        
        if (!plugin.getResizeManager().isValidScale(scale, hasExtendedPermission)) {
            return sendMessage(sender, "scale_range", "min", String.valueOf(minScale), "max", String.valueOf(maxScale));
        }
        
        // Проверка кулдауна
        if (plugin.getResizeManager().hasCooldown(targetPlayer)) {
            long remainingTime = plugin.getResizeManager().getRemainingCooldown(targetPlayer);
            sender.sendMessage(plugin.getLanguageManager().formatMessageComponent(
                plugin.getLanguageManager().getMessageComponent("cooldown", "time", String.valueOf(remainingTime))
            ));
            return true;
        }
        
        // Обновляем время последнего использования команды и применяем масштаб к игроку
        plugin.getResizeManager().updateLastUsage(targetPlayer);
        plugin.getResizeManager().smoothlyResizePlayer(targetPlayer, scale);
        
        return sendMessage(sender, "resized", "scale", String.valueOf(scale));
    }
    
    private boolean sendMessage(CommandSender sender, String key, String... placeholders) {
        sender.sendMessage(plugin.getLanguageManager().formatMessageComponent(
            plugin.getLanguageManager().getMessageComponent(key, placeholders)
        ));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("resizeplugin.reload") && "reload".startsWith(args[0].toLowerCase())) {
                return List.of("reload");
            }
            
            return sender.hasPermission("resizeplugin.resize.extended")
                    ? List.of("0.8", "1.0", "1.15", "0.0625", "16.0")
                    : List.of("0.8", "1.0", "1.15");
        } else if (args.length == 2 && sender.hasPermission("resizeplugin.resize.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
