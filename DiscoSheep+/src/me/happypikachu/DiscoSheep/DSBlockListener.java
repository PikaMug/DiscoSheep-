package me.happypikachu.DiscoSheep;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

public class DSBlockListener implements Listener {
	private DS plugin;
    public DSBlockListener(DS plugin) {
    	this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
    	event.setCancelled(plugin.discoParty.isOurEntity(event.getBlock()));
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
    	event.setCancelled(plugin.discoParty.isOurEntity(event.getBlock()));
    }
}