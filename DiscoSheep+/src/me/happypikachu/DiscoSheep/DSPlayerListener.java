package me.happypikachu.DiscoSheep;

import org.bukkit.ChatColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class DSPlayerListener implements Listener {
	private DS plugin;
    public DSPlayerListener(DS plugin) {
            this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
    	if (event.getEntity() instanceof Sheep & !plugin.getConfig().getBoolean("Sheep.shearable") & plugin.discoParty.isOurEntity(event.getEntity())) {
    		event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-shearing"));
    	}
    }
}