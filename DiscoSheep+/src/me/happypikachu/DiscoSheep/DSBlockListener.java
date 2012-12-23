package me.happypikachu.DiscoSheep;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

public class DSBlockListener implements Listener {
	private DS plugin;
    public DSBlockListener(DS plugin) {
    	this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	event.setCancelled(plugin.discoParty.isOurEntity(event.getBlock()));
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
    	event.setCancelled(plugin.discoParty.isOurEntity(event.getBlock()));
    }
}