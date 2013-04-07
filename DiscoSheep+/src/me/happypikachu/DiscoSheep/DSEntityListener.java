package me.happypikachu.DiscoSheep;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Sheep;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class DSEntityListener implements Listener {
        private DS plugin;
        public DSEntityListener(DS plugin) {
                this.plugin = plugin;
        }
        
        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
        	if (event.getEntity() instanceof Creeper & plugin.getConfig().getBoolean("Creeper.invincible")) {
        		event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));
        	}
        	if (event.getEntity() instanceof Ghast & plugin.getConfig().getBoolean("Ghast.invincible")) {
        		event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));
        	}
        	if (event.getEntity() instanceof Sheep & plugin.getConfig().getBoolean("Sheep.invincible")) {
        		event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));
        	}
        }
        
        @EventHandler
        public void onEntityDeath(EntityDeathEvent event) {
        	if (event.getEntity() instanceof Creeper & plugin.getConfig().getBoolean("Creeper.drop-Items")) {
        		if (plugin.discoParty.isOurEntity(event.getEntity())) {
        			event.getDrops().clear();
        		}
        	}
        	if (event.getEntity() instanceof Ghast & plugin.getConfig().getBoolean("Ghast.drop-Items")) {
        		if (plugin.discoParty.isOurEntity(event.getEntity())) {
        			event.getDrops().clear();
        		}
        	}
        	if (event.getEntity() instanceof Sheep & plugin.getConfig().getBoolean("Sheep.drop-Items")) {
        		if (plugin.discoParty.isOurEntity(event.getEntity())) {
        			event.getDrops().clear();
        		}
        	}
        }
        @EventHandler
        public void onEntityTarget(EntityTargetEvent event) {
        	if (event.getEntity() instanceof Creeper & !plugin.getConfig().getBoolean("Creeper.attack")) {
        		event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));
        	}
        	if (event.getEntity() instanceof Ghast & !plugin.getConfig().getBoolean("Ghast.attack")) {
        		event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));
        	}
        }
        
        @EventHandler
        public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (!plugin.getConfig().getBoolean("Creeper.attack")) {
        	event.setCancelled(plugin.discoParty.isOurEntity(event.getEntity()));                      
        }       
    }
}