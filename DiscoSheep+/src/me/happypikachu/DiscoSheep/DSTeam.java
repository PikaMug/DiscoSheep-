package me.happypikachu.DiscoSheep;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

/**
 * Contains the entities for each party that belongs to a player.
 *
 */
public class DSTeam {
        LinkedList<Sheep> sheepList = new LinkedList<Sheep>();
        LinkedList<Creeper> creeperList = new LinkedList<Creeper>();
        LinkedList<Ghast> ghastList = new LinkedList<Ghast>();
       
        Player player;
        Block soundBlock;
        Block stoneBlock;
        Block torches[];
        boolean light;
       
        public DSTeam(Player p) {
                player = p;
                light = false;
                torches = new Block[4];
        }
        
        public void buildMusicArea(Block loc){
                if(loc == null){
                        return;
                }
                soundBlock = loc.getRelative(1, 1, 1);
                soundBlock.setType(Material.NOTE_BLOCK);
                if(soundBlock.getState() instanceof NoteBlock){
                        ((NoteBlock)soundBlock.getState()).setRawNote((byte) 0x11);
                }
               
                stoneBlock = loc.getRelative(1, 0, 1);
                stoneBlock.setType(Material.STONE);    
               
                torches[0] = loc.getRelative(1, 0, 0);
                torches[1] = loc.getRelative(0, 0, 1);
                torches[2] = loc.getRelative(1, 0, 2);
                torches[3] = loc.getRelative(2, 0, 1);
               
                //turn torches on
                toggleTorches();
                light = !light; //To avoid flicker

                //add to hash
                DSParty.blockHash.put(soundBlock.getLocation(), soundBlock);
                DSParty.blockHash.put(stoneBlock.getLocation(), stoneBlock);
               
               
                for (Block torch: torches) {
                        DSParty.blockHash.put(torch.getLocation(), torch);
                }
               
        }
        
        /**
         * Return player connected to this team
         * @return
         */
        public Player getPlayer() {
                return player;
        }
        
        /**
         * Removes all sheeps, boxes and torches in this team.
         */
        public void cleanUp(){
                cleanUpEntitys();
                cleanUpBoxes();
        }
        
        private void cleanUpEntitys(){
                for(Sheep sheep: sheepList){
                        if(sheep != null){
                                DSParty.creaturesHash.remove(sheep.getEntityId());
                                sheep.remove();
                        }
                }
                sheepList.clear();
               
                for(Creeper creeper: creeperList){
                        if(creeper != null){
                                DSParty.creaturesHash.remove(creeper.getEntityId());
                                creeper.remove();
                        }
                }
                creeperList.clear();
               
                for(Ghast ghast: ghastList){
                        if(ghast != null){
                                DSParty.creaturesHash.remove(ghast.getEntityId());
                                ghast.remove();
                        }
                }
                ghastList.clear();
        }
        
        private void cleanUpBoxes(){
                for (Block torch : torches) {
                        if(torch != null){
                                DSParty.blockHash.remove(torch.getLocation());
                                torch.setType(Material.AIR);
                        }
                }
                if(soundBlock != null){
                        DSParty.blockHash.remove(soundBlock.getLocation());
                        soundBlock.setType(Material.AIR);
                }
                if(stoneBlock != null){
                        DSParty.blockHash.remove(stoneBlock.getLocation());
                        stoneBlock.setType(Material.AIR);
                }
        }
        /**
         * Turn torches on or off
         */
        public void toggleTorches(){
                light = !light;
                for(Block torch: torches){
                        if(torch != null && (torch.getType() == Material.AIR || torch.getType() == Material.TORCH)){
                                if(light){
                                        if(stoneBlock.getType() == Material.STONE){
                                                torch.setType(Material.TORCH);
                                        }
                                }
                                else{
                                        torch.setType(Material.AIR);    
                                }
                        }
                }
        }
        /**
         * Add sheep to the sheeplist for this team
         * @param sheep
         */
        public void addSheep(Sheep sheep){
                if(sheep != null){
                        sheepList.add(sheep);
                        DSParty.creaturesHash.put(sheep.getEntityId(), (Entity) sheep);              
                }else{
                        System.out.println("[DiscoSheepPlus] addSheep in DiscoTeam received a sheep that was null. Sheep not added");
                }
        }
        /**
         * Add creeper to the creeperlist for this team
         * @param sheep
         */
        public void addCreeper(Creeper creeper) {
                if(creeper != null){
                        creeperList.add(creeper);
                        DSParty.creaturesHash.put(creeper.getEntityId(), (Entity) creeper);
                }else{
                        System.out.println("[DiscoSheepPlus] addCreeper in DiscoTeam recived a creeper that was null. Creeper not added");
                }
        }
        /**
         * Add ghast to the ghastlist for this team
         * @param sheep
         */
        public void addGhast(Ghast ghast) {
                if(ghast != null){
                        ghastList.add(ghast);
                        DSParty.creaturesHash.put(ghast.getEntityId(), (Entity) ghast);      
                }else{
                        System.out.println("[DiscoSheepPlus] addGhast in DiscoTeam recived a ghast that was null. Ghast not added");
                }

        }

       
}