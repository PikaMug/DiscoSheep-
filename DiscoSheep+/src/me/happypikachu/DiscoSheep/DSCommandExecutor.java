package me.happypikachu.DiscoSheep;

import java.util.LinkedList;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class DSCommandExecutor implements CommandExecutor {
	private DS plugin;
	public DSCommandExecutor(DS plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ds") || cmd.getName().equalsIgnoreCase("discoparty") || cmd.getName().equalsIgnoreCase("discosheep")) {
            if (args.length == 0) {
            	PluginDescriptionFile pdFile = plugin.getDescription();	
            	sender.sendMessage(ChatColor.RED + "DiscoSheep+ v" + pdFile.getVersion() + ChatColor.GRAY + " " + pdFile.getAuthors().toString());
            	sender.sendMessage(ChatColor.WHITE + pdFile.getDescription());
            	sender.sendMessage(ChatColor.GRAY + "Type" + ChatColor.WHITE + " /ds help " + ChatColor.GRAY + "for commands.");
            	return true;
            }
            
            if (args.length > 0) {
            	if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info")) {
                    if(sender.hasPermission("discosheep.help") || plugin.permit.contains((Player)sender)) {	
                        sender.sendMessage(ChatColor.RED + "DiscoSheep+ Commands:");
                        sender.sendMessage(ChatColor.GRAY + "(Green indicates access to that command.)");
                        sender.sendMessage((sender.hasPermission("discosheep.buy") || sender.hasPermission("discosheep.pay") ? ChatColor.GREEN:"") + "/ds buy: " + ChatColor.GRAY + "Purchase a single use.");
                        sender.sendMessage((sender.hasPermission("discosheep.color") ? ChatColor.GREEN:"") + "/ds color: " + ChatColor.GRAY + "Toggle dynamic rainbow sheep.");
                        sender.sendMessage((sender.hasPermission("discosheep.help") ? ChatColor.GREEN:"") + "/ds help: " + ChatColor.GRAY + "Command reference.");
                        sender.sendMessage((sender.hasPermission("discosheep.party") ? ChatColor.GREEN:"") + "/ds party: " + ChatColor.GRAY + "Start one or more parties.");
                        sender.sendMessage((sender.hasPermission("discosheep.permit") ? ChatColor.GREEN:"") + "/ds permit: " + ChatColor.GRAY + "Grant a player a single use.");
                        sender.sendMessage((sender.hasPermission("discosheep.reload") ? ChatColor.GREEN:"") + "/ds reload: " + ChatColor.GRAY + "Reload the configuration file.");
                        sender.sendMessage((sender.hasPermission("discosheep.stop") ? ChatColor.GREEN:"") + "/ds stop: " + ChatColor.GRAY + "Cancel all ongoing parties.");
                    } else {
                    	sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
                    }
                    return true;
                }
            	if (args[0].equalsIgnoreCase("color") || args[0].equalsIgnoreCase("rainbow")) {
                    if(sender.hasPermission("discosheep.color")) {
                    	plugin.recover();
                        plugin.discoParty.toggleColor();
                    	sender.sendMessage("Rainbow sheep toggled" + (plugin.discoParty.isColorOn() ? "on.":"off."));
                    } else {
                    	sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("stop")) {
                	if(sender.hasPermission("discosheep.stop")) {
                		if(plugin.discoParty.flagPartyEnabled) {
                			sender.sendMessage(plugin.getCustomConfig().getString("stop"));                                              
                		} else {
                			sender.sendMessage(plugin.getCustomConfig().getString("no-party"));                                            
                		}
                		plugin.stopParty();
                	} else {
                		sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
                	}
                	return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                	if(sender.hasPermission("discosheep.reload")) {
                		try {
                			plugin.reloadConfig();
                			plugin.reloadCustomConfig();
                			sender.sendMessage(ChatColor.GREEN + plugin.getCustomConfig().getString("reload"));
                		} catch (Exception ex) {
                			sender.sendMessage(ChatColor.RED + "An error occurred: Failed to reload configs.");
                		}
                	} else {
                		sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
                	}
                	return true;
                }
                if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("pay")) {
                    if((sender.hasPermission("discosheep.buy") || sender.hasPermission("discosheep.pay")) & (plugin.getConfig().getBoolean("Economy.enable-vault"))){
                    	if (sender instanceof Player) {
                    		if (plugin.econ != null) {
                    			Double charge = plugin.getConfig().getDouble("Economy.cost-per-use");
                    	    	EconomyResponse r = plugin.econ.withdrawPlayer(sender.getName(), charge);
                				if (r.transactionSuccess()) {
                					plugin.permit.add((Player)sender);
                					sender.sendMessage("You have been charged " + plugin.econ.format(charge) + " to use DiscoSheep+ once!");
                				} else {
                					sender.sendMessage(String.format(ChatColor.RED + "An error occurred: %s.", r.errorMessage));
                				}
                    		}
                    	} else {
                    		sender.sendMessage(plugin.getCustomConfig().getString("deny-console"));
                    	}
                    } else if (!(plugin.getConfig().getBoolean("Economy.enable-vault"))){
                    	sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-vault"));
                    } else {
                    	sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("permit") || args[0].equalsIgnoreCase("grant")) {
            		if (args.length > 1) {
            			if(sender.hasPermission("discosheep.permit")) {
            				Player p = Bukkit.getServer().getPlayer(args[1]);
                        	if (p != null) {
                        		plugin.permit.add(p);
                            	sender.sendMessage("You gave " + p.getDisplayName() + " permission to use DiscoSheep+ once!");
                            	p.sendMessage(ChatColor.GREEN + plugin.getCustomConfig().getString("permit"));
                            	sender.sendMessage(ChatColor.GRAY + "Type" + ChatColor.WHITE + " /ds help " + ChatColor.GRAY + "for useful tips.");
                        	} else {
                        		sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
                        	}
            			}
            		} else {
            			sender.sendMessage(plugin.getCustomConfig().getString("no-target"));
            		}
            		return true;
        		}
            	if (args[0].equalsIgnoreCase("party")) {
            		if(!(sender.hasPermission("discosheep.party") || plugin.permit.contains((Player)sender))) {
            			//Sender is not allowed to host a party. Sender receives error message.
            			sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-permission"));
            			return true;
            		}
            		
            		Player[] onlinePlayer = plugin.getServer().getOnlinePlayers();
            		LinkedList<Player> getPartyPlayers = new LinkedList<Player>();
                   
            		// N = numbers
            		// D = default
            		// We use default to compare if it was any changes. We don't want to send denied use message if its not changed
                    int timeN = plugin.getConfig().getInt("Party.default-time");
                    int sheepsN = plugin.getConfig().getInt("Sheep.default-amount");
                    int sheepsD = sheepsN;
                    int creepersN = plugin.getConfig().getInt("Creeper.default-amount");
                    int creepersD = creepersN;
                    int ghastsN = plugin.getConfig().getInt("Ghast.default-amount");
                    int ghastsD = ghastsN;
                    int spawnDistance = plugin.getConfig().getInt("Party.default-distance");
                   
                    //Check players
                    for(String s: args) {
                    	String[] temp = s.split(":");
                    	if(temp.length == 2) {
                    		if(temp[0].equalsIgnoreCase("p") || temp[0].equalsIgnoreCase("player") || temp[0].equalsIgnoreCase("players")){
                    			if(sender.hasPermission("discosheep.party.one")) {
                    				for (Player p : onlinePlayer) {
                    					if(p.getName().equalsIgnoreCase(temp[1])) {
                    						if (plugin.getConfig().getBoolean("Worlds." + p.getWorld().getName())) {
                    							getPartyPlayers.add(p);
                    						} else {
                    							sender.sendMessage(ChatColor.RED + "Plugin is disabled in " + p.getName() + "'s world.");
                    						}
                    					}
                    				}
                    			} else {
                    				sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("deny-single-party"));
                    				return true;
                    			}
                    		}
                    	}
                    }
                    
                    //If no players were specified, throw party for everyone
                    if (getPartyPlayers.size() == 0) {
                    	if(sender.hasPermission("discosheep.party.many")) {
                    		if (onlinePlayer.length != 0) {
                    			for(Player p: onlinePlayer) {
                    				if (plugin.getConfig().getBoolean("Worlds." + p.getWorld().getName())) {
                    					getPartyPlayers.add(p);
                    				}
                    			}
                    		} else {
                    			sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("no-players"));
                    		}
                    	} else {
                    		sender.sendMessage(ChatColor.RED + plugin.getCustomConfig().getString("deny-multiple-party"));
                    		return true;
                    	}
                    }
                   
                    //Check time
                    for(String s: args){
                    	String[] temp = s.split(":");
                        if(temp.length == 2){
                        	if(temp[0].equalsIgnoreCase("t") || temp[0].equalsIgnoreCase("time") || temp[0].equalsIgnoreCase("sec") || temp[0].equalsIgnoreCase("seconds")){
                        		try {
                        			timeN = Integer.parseInt(temp[1]);
                                } catch (NumberFormatException e) {
                                    plugin.errParseInt(sender, temp[1], timeN);
                                }
                            }
                        }
                    }
                    //Check sheeps
                    for(String s: args){
                            String[] temp = s.split(":");
                            if(temp.length == 2){
                                    if(temp[0].equalsIgnoreCase("s") || temp[0].equalsIgnoreCase("sheep") || temp[0].equalsIgnoreCase("sheeps")){
                                            try {
                                                    sheepsN = Integer.parseInt(temp[1]);
                                            } catch (NumberFormatException e) {
                                            	plugin.errParseInt(sender, temp[1], sheepsN);
                                            }
                                    }
                            }
                    }
                   
                    //Check creepers
                    for(String s: args){
                            String[] temp = s.split(":");
                            if(temp.length == 2){
                                    if(temp[0].equalsIgnoreCase("c") || temp[0].equalsIgnoreCase("creeper") || temp[0].equalsIgnoreCase("creepers")){
                                            try {
                                                    creepersN = Integer.parseInt(temp[1]);
                                            } catch (NumberFormatException e) {
                                            	plugin.errParseInt(sender, temp[1], creepersN);
                                            }
                                    }
                            }
                    }
                   
                    //Check ghasts
                    for(String s: args){
                            String[] temp = s.split(":");
                            if(temp.length == 2){
                                    if(temp[0].equalsIgnoreCase("g") || temp[0].equalsIgnoreCase("ghast") || temp[0].equalsIgnoreCase("ghasts")){
                                            try {
                                                    ghastsN = Integer.parseInt(temp[1]);
                                            } catch (NumberFormatException e) {
                                            	plugin.errParseInt(sender, temp[1], ghastsN);
                                            }
                                    }
                            }
                    }
                   
                    //Check spawn
                    for(String s: args){
                            String[] temp = s.split(":");
                            if(temp.length == 2){
                                    if(temp[0].equalsIgnoreCase("d") || temp[0].equalsIgnoreCase("distance") || temp[0].equalsIgnoreCase("spawn")){
                                            try {
                                                    spawnDistance = Integer.parseInt(temp[1]);
                                            } catch (NumberFormatException e) {
                                            	plugin.errParseInt(sender, temp[1], spawnDistance);
                                            }
                                    }
                            }
                    }
                   
                    int max;
                    //Max time
                    max = plugin.getConfig().getInt("Party.max-time");
                    if(max < timeN){
                            sender.sendMessage("Max time limit exceeded, time set to max: " + max);
                            timeN = max;
                    }
                    
                    //Max sheeps and permission to spawn sheeps
                    max = plugin.getConfig().getInt("Sheep.max-amount");
                    if(sheepsN > 0 && (sender.hasPermission("discosheep.sheep") || plugin.permit.contains((Player)sender))){
                            if(max < sheepsN){
                                    sender.sendMessage("Max sheep limit exceeded, sheeps set to max: " + max);
                                    sheepsN = max;
                            }
                    }else{
                            if(sheepsN != sheepsD){
                                    // We use permit to send denied message to user
                            		sender.sendMessage(ChatColor.RED + "You do not have permission to spawn Sheep.");
                            }
                            //If the user has not done any changes to sheeps we silently set sheeps to 0
                            sheepsN = 0;
                    }
                   
                    //Max creepers and permission to spawn creepers
                    max = plugin.getConfig().getInt("Creeper.max-amount");
                    if(creepersN > 0 && (sender.hasPermission("discosheep.creeper") || plugin.permit.contains((Player)sender))){
                            if(max < creepersN){
                                    sender.sendMessage("Max creeper limit exceeded, creepers set to max: " + max);
                                    creepersN = max;
                            }
                    }else{
                            if(creepersN != creepersD){
                                    // We use permit to send denied message to user
                            	    sender.sendMessage(ChatColor.RED + "You do not have permission to spawn Creepers.");
                            }
                            //If the user has not done any changes to creepers we silently set creepers to 0
                            creepersN = 0;
                    }
                   
                    //Max ghasts and permission to spawn ghasts
                    max = plugin.getConfig().getInt("Ghast.max-amount");
                    if(ghastsN > 0 && (sender.hasPermission("discosheep.ghast"))){
                            if(max < ghastsN){
                                    sender.sendMessage("Max ghast limit exceeded, ghasts set to max: " + max);
                                    ghastsN = max;
                            }
                    }else{
                            if(ghastsN != ghastsD){
                                    // We use permit to send denied message to user
                            	    sender.sendMessage(ChatColor.RED + "You do not have permission to spawn Ghast.");
                            }
                            //If the user has not done any changes to ghasts we silently set ghasts to 0
                            ghastsN = 0;
                    }
                   
                    //Max spawn distance check
                    max = plugin.getConfig().getInt("Party.max-distance");
                    if(max < spawnDistance){
                            sender.sendMessage("Max spawn distance limit exceeded, spawn distance set to max: " + max);
                            spawnDistance = max;
                    }              
                   
                    Player[] partyAt = new Player[getPartyPlayers.size()];
                    int i = 0;
                    for(Player p: getPartyPlayers){
                            partyAt[i] = p;
                            i++;
                    }
                   
                    plugin.startParty(partyAt, timeN, sheepsN, creepersN, ghastsN, spawnDistance);
                   
                    sender.sendMessage(ChatColor.YELLOW + plugin.getCustomConfig().getString("party"));
                    if (sender instanceof Player) {
                    	if (plugin.permit.contains((Player)sender)) {
                    		plugin.permit.remove((Player)sender);
                    		sender.sendMessage(ChatColor.GRAY + plugin.getCustomConfig().getString("permit-use"));
                    	}
                    }
                    return true;
            	}
            	//If all else fails
            	PluginDescriptionFile pdFile = plugin.getDescription();	
            	sender.sendMessage(ChatColor.RED + "DiscoSheep+ v" + pdFile.getVersion() + ChatColor.GRAY + " " + pdFile.getAuthors().toString());
            	sender.sendMessage(ChatColor.WHITE + pdFile.getDescription());
            	sender.sendMessage(ChatColor.GRAY + "Type" + ChatColor.WHITE + " /ds help " + ChatColor.GRAY + "for commands.");
            	return true;
            }
    	}
        return false;
	}
}