package dev.moonaticks.resizeReworked.managers;

import dev.moonaticks.resizeReworked.ResizeReworked;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResizeManager {
    
    private final ResizeReworked plugin;
    private final Map<UUID, Long> lastUsageTime = new ConcurrentHashMap<>();
    
    public ResizeManager(ResizeReworked plugin) {
        this.plugin = plugin;
    }
    
    public boolean hasCooldown(Player player) {
        if (player.hasPermission("resizeplugin.bypass.cooldown")) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastUsed = lastUsageTime.getOrDefault(player.getUniqueId(), 0L);
        long timeSinceLastUse = (currentTime - lastUsed) / 1000;
        
        return timeSinceLastUse < plugin.getConfigManager().getCooldownSeconds();
    }
    
    public long getRemainingCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = lastUsageTime.getOrDefault(player.getUniqueId(), 0L);
        long timeSinceLastUse = (currentTime - lastUsed) / 1000;
        
        return Math.max(0, plugin.getConfigManager().getCooldownSeconds() - timeSinceLastUse);
    }
    
    public void updateLastUsage(Player player) {
        lastUsageTime.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public void smoothlyResizePlayer(Player player, double targetScale) {
        double initialScale = player.getAttribute(Attribute.SCALE).getBaseValue();
        double step = (targetScale - initialScale) / plugin.getConfigManager().getResizeSteps();
        
        new BukkitRunnable() {
            int count = 0;
            
            @Override
            public void run() {
                if (count++ >= plugin.getConfigManager().getResizeSteps()) {
                    player.getAttribute(Attribute.SCALE).setBaseValue(targetScale);
                    cancel();
                } else {
                    player.getAttribute(Attribute.SCALE).setBaseValue(initialScale + step * count);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    public boolean isValidScale(double scale, boolean hasExtendedPermission) {
        if (hasExtendedPermission) {
            return scale >= plugin.getConfigManager().getExtendedMinScale() && 
                   scale <= plugin.getConfigManager().getExtendedMaxScale();
        } else {
            return scale >= plugin.getConfigManager().getDefaultMinScale() && 
                   scale <= plugin.getConfigManager().getDefaultMaxScale();
        }
    }
    
    public double getMinScale(boolean hasExtendedPermission) {
        return hasExtendedPermission ? 
               plugin.getConfigManager().getExtendedMinScale() : 
               plugin.getConfigManager().getDefaultMinScale();
    }
    
    public double getMaxScale(boolean hasExtendedPermission) {
        return hasExtendedPermission ? 
               plugin.getConfigManager().getExtendedMaxScale() : 
               plugin.getConfigManager().getDefaultMaxScale();
    }
}
