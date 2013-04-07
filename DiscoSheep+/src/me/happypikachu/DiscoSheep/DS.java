package me.happypikachu.DiscoSheep;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.Timer;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DS extends JavaPlugin implements ActionListener {
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
	private DSCommandExecutor cmdExecutor = new DSCommandExecutor(this);
	protected DSParty discoParty = new DSParty(this);
	protected Timer timer;
	public Set<Player> permit = new HashSet<Player>();
	public Economy econ = null;
	
	@Override
	public void onEnable() {
		//Copy config.yml and update header
        saveDefaultConfig();
        getConfig().options().header("DiscoSheep+ v" + getDescription().getVersion() + " Configuration" + 
				"\nby HappyPikachu -aka- FlyingPikachu" +
				"\n" + 
			    "\nIf you experience a problem with this config when starting" +
			    "\nyour server, make sure that you're using spaces and not tabs." + 
			    "\nCheck that all apostrophes are escaped. For example, \"can't\"" + 
			    "\nbecomes \"can\\'t\"." +
        		"\n");
        getConfig().options().copyHeader(true);
        for (World world: getServer().getWorlds()) {
        	getConfig().addDefault("Worlds." + world.getName(), true);
		}
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        //Copy string.yml
        saveDefaultCustomConfig();
        
    	getCommand("ds").setExecutor(cmdExecutor);
        getServer().getPluginManager().registerEvents(new DSBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new DSEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new DSPlayerListener(this), this);
        
        timer = new Timer(getConfig().getInt("Party.default-time"), this);
        timer.stop();
        
        if (setupEconomy() & getConfig().getBoolean("Economy.enable-vault")) {
            getLogger().info("Using Vault for economy is enabled.");
        } else if (setupEconomy()) {
            getLogger().info("Using Vault for economy is disabled.");
        }
    }
    
    @Override
    public void onDisable() {
    	endParty();
        getServer().getScheduler().cancelTasks(this);
    }
    
    /**
     * Checks for and registers Vault as economy service provider.
     */
    private boolean setupEconomy() {
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
    		return false;
    	}
    	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    	if (rsp == null) {
    		return false;
    	}
    	econ = rsp.getProvider();
    	return econ != null;
    }
    
    /**
     * 
     * Distinguishes unit conversion errors.
     */
    public void errParseInt(CommandSender sender, String string, int def) {
    	sender.sendMessage(ChatColor.RED + "An error occurred: Could not convert " + string + " to a number. Using default " + def);
    }
    
    /**
     * Spawns objects and starts party.
     */
	
	public void startParty(Player[] players, int partyTime, int sheeps, int creepers, int ghasts, int spawnRange){
		timer.stop();
		//recover();
		timer.setInitialDelay(partyTime * 1000);
	    timer.start();
    	discoParty.enableParty(players, sheeps, creepers, ghasts, spawnRange);
    	discoParty.startParty();
    }
    
    /**
     * Ends party.
     */
    void endParty() {
    	timer.stop();
    	discoParty.stopParty();
    }
    
    /**
    * Called when timer wants to stop party.
    */
    @Override
    public void actionPerformed(ActionEvent arg0) {
    	endParty();
    }
    
    /**
     * Loads strings.yml config.
     */
    public void reloadCustomConfig() {
        if (customConfigFile == null) {
        customConfigFile = new File(getDataFolder(), "strings.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
     
        InputStream defConfigStream = this.getResource("strings.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }
    
    /**
     * Gets strings.yml config.
     */
    public FileConfiguration getCustomConfig() {
        if (customConfig == null) {
            this.reloadCustomConfig();
        }
        return customConfig;
    }
    
    /**
     * Saves current strings.yml config.
     */
    public void saveCustomConfig() {
        if (customConfig == null || customConfigFile == null) {
        return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to" + customConfigFile, ex);
        }
    }
    
    /**
     * Saves default strings.yml config.
     */
    public void saveDefaultCustomConfig() {
    	reloadCustomConfig();
        if (!customConfigFile.exists()) {            
             saveResource("strings.yml", false);
         }
    }
}