package me.happypikachu.DiscoSheep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
/**
* Discoparty includes a list of all players and their party teams when discoparty was started
* Also include some setting on how many sheeps to be spawned, beatspeed and block distance for sheeps to spawn
*
*/
public class DSParty {
        private DS plugin;
        protected boolean running;
        protected boolean flagGenerateTeam = false;
        protected boolean flagCleanUpTeams = false;
        protected boolean flagColorEnabled = false;
        protected boolean flagPartyEnabled = false;
        private boolean isCreatingTeam = false;
        LinkedList<DSTeam> toDelete = new LinkedList<DSTeam>(); //Store teams that needs to be removed
        BukkitTask task;
        public DSParty(DS plugin) {
            this.plugin = plugin;
            running = false;
        }
       
        //We will use entity.getEntityId() as our key to store creatures in hash
        protected static HashMap<Integer, Entity> creaturesHash = new HashMap<Integer, Entity>();
       
        //We will use toString from block location as our key to store creatures in hash
        protected static HashMap<Location, Block> blockHash = new HashMap<Location, Block>();
       
        //Values to set before generateTeams(); is called
        public Player[] playersToGetParty = null;
        public int sheepsN = 10;
        public int creepersN = 0;
        public int ghastsN = 0;
        public int spawnRange = 5;

        protected LinkedList<DSTeam> discoTeams = new LinkedList<DSTeam>();
        private int beatspeed = 475; //Millisec between beats
       
        public static final DyeColor dyeColors[] = {
        	DyeColor.WHITE,
        	DyeColor.ORANGE,
        	DyeColor.MAGENTA,
        	DyeColor.LIGHT_BLUE,
        	DyeColor.YELLOW,
        	DyeColor.LIME,
        	DyeColor.PINK,
        	DyeColor.GRAY,
        	DyeColor.SILVER,
        	DyeColor.CYAN,
        	DyeColor.PURPLE,
        	DyeColor.BLUE,
        	DyeColor.BROWN,
        	DyeColor.GREEN,
        	DyeColor.RED,
        	DyeColor.BLACK
         };
       
        /**
         * Cleans up all party teams and remove them.
         */
        public void cleanUp() {
        	try {
        		for(DSTeam team: discoTeams){
        			team.cleanUp();
        		}
        			discoTeams.clear();
                } catch (Exception e) {
                	plugin.getLogger().severe("Failed to clean up all teams. Some blocks and sheeps may still exist.");
                }
        }
        
        /**
         * Turn on an off all sheeps randomly changing colors
         */
        public void toggleColor(){
            flagColorEnabled = !flagColorEnabled;
        }
        
        /**
         * Returns true if sheeps are changing colors.
         * @return
		 */
        public boolean isColorOn(){
        	return flagColorEnabled;
        }
        
        /**
         * Sets flags so thread begins running party
         */
        public void enableParty(Player[] players, int sheeps, int creepers, int ghasts, int spawn){
        	//In case party is already generating
        	for (int i = 0; flagGenerateTeam && i < 4; i++) {
        		if(i == 3){
        			plugin.getLogger().severe("Had trouble creating new team. Ending thread.");
        			running = false;
        			return;
        			}
                }
        	playersToGetParty = players;
        	sheepsN = sheeps;
        	creepersN = creepers;
        	ghastsN = ghasts;
        	spawnRange = spawn;
        	flagPartyEnabled = true;
        	flagGenerateTeam = true;
        	running = true;
        	cleanUp();
        	discoTeams.clear();
        }
        
        /**
         * Sets flag so thread disables party and cleans up
         */
        public void stopParty(){
                flagPartyEnabled = false;
                flagCleanUpTeams = true;
        }
        
        /**
         * Called when discoparty starts in a new thread
         */
		public void startParty() {
        	task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
    			@Override
    			public void run() {
    				if (running) {
    					//Check for flag to generate teams
                        if(flagGenerateTeam){
                                cleanUp();
                                isCreatingTeam = true;
                                generateTeams();
                                isCreatingTeam = false;
                                flagGenerateTeam = false;
                        }
                       
                        //Check for plugin.colorEnabled and plugin.partyEnable
                        if(flagColorEnabled){
                                changeColorAllSheeps();
                        }
                        else if(flagPartyEnabled){
                                changeColorTeamSheeps();
                        }
                       
                        if(flagPartyEnabled){
                                for(DSTeam team: discoTeams){
                                        if(!team.player.isOnline()){
                                                System.out.println("[DiscoSheepPlus] Player " + team.player.getName() + " left server during party. Didn't like the music?");
                                                team.cleanUp();
                                                toDelete.add(team);
                                        }
                                }
                                discoTeams.removeAll(toDelete);
                                toDelete.clear();
                                toggleTorches();
                               
                                playMusic();
                               
                                sheepJump();

                        }
                       

                        //Check for flag to clean up party
                        if (flagCleanUpTeams) {
                                cleanUp();
                                flagCleanUpTeams = false;
                                
                                running = false;
                        }
    				} else {
    					task.cancel();
    				}
    				
    			}
            }, 0L, 10L);
        }
        
        /**
         * Turn torches in teams on or off depending on last state
         */
        private void toggleTorches() {
                try {
                        for(DSTeam team: discoTeams){
                                team.toggleTorches();
                        }
                } catch (Exception e) {
                        plugin.getLogger().severe("Error in DiscoParty method toggleTorches. ");
                }
               
        }
        /**
         * Play one sequence from the musicbox (Add more music later!)
         */
        private void playMusic() {
                try {
                        for(DSTeam team: discoTeams){
                                if(team.soundBlock != null && team.soundBlock.getState() instanceof NoteBlock){
                                        //Play off 3 times at the same time to give some real OMPH in the speaker
                                        for (int i = 0; i < 3; i++) {
                                                ((NoteBlock)team.soundBlock.getState()).play();
                                        }
                                }
                        }
                } catch (Exception e) {
                	plugin.getLogger().severe("Error in DiscoParty method playMusic.");
                }

               
        }

        private void changeColorTeamSheeps() {
                for(DSTeam team: discoTeams){
                        for(Sheep sheep: team.sheepList){
                                if(sheep != null){
                                        changeColor(sheep);
                                }
                        }
                }
               
        }

        /**
         * Make all sheeps in teams jump
         */
        private void sheepJump() {
                for(DSTeam team: discoTeams){
                        for(Sheep sheep: team.sheepList){
                                if(sheep != null){
                                        try {
                                                sheepJump(sheep);
                                        } catch (Exception e) {
                                        	plugin.getLogger().warning("A sheep could not rock on. Dead sheep?");
                                        }
                                }
                        }
                }
               
        }

        /**
         * Make a party teams for each player in server
         */
        private void generateTeams() {
               
                if(playersToGetParty == null || playersToGetParty.length == 0){
                	plugin.getLogger().info("Don't have any players to give party");
                	return;
                }
               
                DSTeam team;
               
                for(Player player: playersToGetParty){
                        team = new DSTeam(player);
                       
                        //Add musicBox and stone
                        Block goodLocation = getMusicArea(team.getPlayer().getLocation().getBlock());
                        buildMusicArea(goodLocation, team);
                        //Add torches
                        discoTeams.add(team);

                        //Add creatures to team
                        addSheeps(team, sheepsN);
                        addCreepers(team, creepersN);
                        addGhasts(team, ghastsN);
                }
        }
        /**
         * Add ghasts above the player
         * @param team
         */
        private void addGhasts(DSTeam team, int ghasts) {
                Random rand = new Random();
               
                int ghastNumber = ghasts;
                int spawnDistance = spawnRange;
                for (int i = 0; i < ghastNumber; i++) {
                        int r = rand.nextInt(spawnDistance * 2 * spawnDistance * 2);
                        int x = (r%(spawnDistance * 2)) - spawnDistance;
                        int z = (r/(spawnDistance * 2)) - spawnDistance;
                        Block spawnPlane = team.getPlayer().getLocation().getBlock().getRelative(x, 0, z);
                        Block spawnLoc = findSpawnYLoc(spawnPlane).getRelative(0, 8, 0);
                        
                        if(spawnLoc != null){
                         team.addGhast((Ghast) spawnLoc.getWorld().spawnEntity(spawnLoc.getLocation(), EntityType.GHAST));
                        }
                }
        }

        /**
         * Add creepers around the player
         * @param team
         */
        private void addCreepers(DSTeam team, int creepers) {
                Random rand = new Random();
               
                int creeperNumber = creepers;
                int spawnDistance = spawnRange;
                for (int i = 0; i < creeperNumber; i++) {
                        int r = rand.nextInt(spawnDistance * 2 * spawnDistance * 2);
                        int x = (r%(spawnDistance * 2)) - spawnDistance;
                        int z = (r/(spawnDistance * 2)) - spawnDistance;
                        Block spawnPlane = team.getPlayer().getLocation().getBlock().getRelative(x, 0, z);
                        Block spawnLoc = findSpawnYLoc(spawnPlane);
                        if(spawnLoc != null){
                                team.addCreeper((Creeper) spawnLoc.getWorld().spawnEntity(spawnLoc.getLocation(), EntityType.CREEPER));
                        }
                }
               
        }

        /**
         * Add sheeps around the player
         * @param team
         */
        private void addSheeps(DSTeam team, int sheeps) {
                Random rand = new Random();
               
                int sheepNumber = sheeps;
                int spawnDistance = spawnRange;
                for (int i = 0; i < sheepNumber; i++) {
                        int r = rand.nextInt(spawnDistance * 2 * spawnDistance * 2);
                        int x = (r%(spawnDistance * 2)) - spawnDistance;
                        int z = (r/(spawnDistance * 2)) - spawnDistance;
                        Block spawnPlane = team.getPlayer().getLocation().getBlock().getRelative(x, 0, z);
                        Block spawnLoc = findSpawnYLoc(spawnPlane);
                        if(spawnLoc != null){
                                team.addSheep((Sheep) spawnLoc.getWorld().spawnEntity(spawnLoc.getLocation(), EntityType.SHEEP));
                        }
                }
        }
       
        /**
         * Search for a free place above player to install musicbox
         * @param loc
         * @return
         */
        private Block getMusicArea(Block loc) {
               
                loc = loc.getRelative(-4, 3, -4); //So we start checking in a corner
               
               
                for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                                if(isMiniChunkFree(loc.getRelative(x*3, 0, y*3))){
                                        return loc.getRelative(x*3, 0, y*3);
                                }
                        }
                }
                return null;
        }
       
        /**
         * Check if a 3*2*3 space is free so we can have a music box there.
         * @param loc
	* @return
	*/
        private boolean isMiniChunkFree(Block loc) {
               
                for (int x = 0; x < 3; x++) {
                                for(int z = 0; z < 3; z++){
                                        for (int y = 0; y < 3; y++) {
                                        if(loc.getRelative(x, y, z).getType() != Material.AIR){
                                                return false;
                                        }
                                }
                        }
                }
                return true;
        }
        /**
         * Allocate the blocks we use for musicbox and torches
         * @param loc
         * @param team
         */
        private void buildMusicArea(Block loc, DSTeam team){
                team.buildMusicArea(loc);
        }

        /**
         * Changes color on all sheeps in the worlds
         */
        private void changeColorAllSheeps() {
                for(World world: plugin.getServer().getWorlds()){
                        for(LivingEntity entity: world.getLivingEntities()){
                                if(entity != null && entity instanceof Sheep){
                                        changeColor((Sheep) entity);
                                }
                        }
                }
        }
        /**
         * Swap wool colour to random on sheep entity
         * @param entity
         */
        private void changeColor(Sheep entity) {
                try {
                        Random r = new Random();
                        entity.setColor(dyeColors[r.nextInt(dyeColors.length)]);
                } catch (Exception e) {
                	plugin.getLogger().warning("Error: A sheep could not change colour");
                }

        }


        /**
         * Returns a good spot to spawn sheeps in the y axis to the given block.
         * First checks if its a air block, and then go down 4 blocks to find ground. If no ground found, return null.
         * The checks if its a solid block and try to find air up to 4 blcoks above given block. If no air found return null.
         * @param spot
         * @return Block
         */
        private Block findSpawnYLoc(Block spot){
                if(spot == null){
                        return null;
                }
               
                Block temp = spot;
                if(temp.getType() == Material.AIR){
                        for(int i = 0; i < 5; i++){
                                if(temp.getRelative(0, -1, 0) != null && temp.getRelative(0, -1, 0).getType() != Material.AIR){
                                        return temp;
                                }
                                temp = temp.getRelative(0, -1, 0); //Go down to find ground
                        }
                }
               
                temp = spot;
                if(temp.getType() != Material.AIR){
                        for(int i = 0; i < 5; i++){
                                if(temp.getRelative(0, 1, 0) != null && temp.getRelative(0, 1, 0).getType() == Material.AIR){
                                        return temp.getRelative(0, 1, 0);
                                }
                                temp = temp.getRelative(0, 1, 0); //Go up to find air
                        }
                }
               
                //Return null if no location found
                return null;
               
        }
       
        /**
         * Makes the sheep entity twitch/jump randomly to make a MOSH PIT!
         * @param entity
         */
        private void sheepJump(Sheep sheep) {
               
                Random r = new Random();
                //Some random jumping
                if(r.nextInt(4) == 0){
                        Vector v = sheep.getVelocity();
                       
                        v.setY(0.5f);

                        sheep.setVelocity(v);
                }
               

        }
       
        public String printDebug(){
                String ln = "\n";
                StringBuilder s = new StringBuilder();
               
                s.append("flagGenerateTeam: " + flagGenerateTeam + ln);
                s.append("flagCleanUpTeams: " + flagCleanUpTeams + ln);
                s.append("flagColorEnabled: " + flagColorEnabled + ln);
                s.append("flagPartyEnabled: " + flagPartyEnabled + ln);
                s.append("isCreatingTeams : " + isCreatingTeam + ln);
                s.append("discoPartyAlive : " + plugin.getServer().getScheduler().isCurrentlyRunning(task.getTaskId()) + ln);
                s.append("Beatspeed : " + beatspeed + ln);
                s.append("number of teams : " + discoTeams.size()+ ln);
                s.append("sheeps in teams : " + numberOfSheeps() + ln);
                s.append("tempSheepSize : " + sheepsN + ln);
                s.append("tempSpawnSize : " + spawnRange + ln);
                return s.toString();
        }
        /**
         * Total number of sheeps we currently have spawned
         * @return
         */
        private int numberOfSheeps(){
                int result = 0;
                for(DSTeam team: discoTeams){
                        for(Sheep sheep: team.sheepList){
                                if(sheep != null){
                                        result++;
                                }
                        }
                }
                return result;
        }
        /**
         * See if entity is in one of our discoteams
         * @param entity
         * @return
         */
        public boolean isOurEntity(Entity entity) {
               
                //Find sheeps in O(1) time
                if(creaturesHash.containsKey(entity.getEntityId())){
                        return true;
                }
               
                return false;
        }
        /**
         * See if block is in one of our discoteams
         * @param block
	     * @return
	     */
        public boolean isOurEntity(Block block) {
               
                //Find block in O(1) time
                if(blockHash.containsKey(block.getLocation())){
                        return true;
                }
               
                return false;
        }
}