package phiwa.mortar;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import TheEnd.XemsDoom.XemDB;

public class MortarDatabases {

	public static void loadDB() {
		
	        if(!(new File("plugins/Mortar/databases/dispensers.xem").exists())){
	            MortarMain.dispensersDB = new XemDB("plugins/Mortar/databases/dispensers.xem", "dispensers");
	            System.out.println("[Mortar] Created dispensers-database.");
	        }else{
	        	MortarMain.dispensersDB = new XemDB("plugins/Mortar/databases/dispensers.xem", "dispensers");
	        	System.out.println("[Mortar] Loaded dispensers-database.");
	        }
	        
	}
	
	public static void createEntry(Location loc, Player player) {
		if(!(player.hasPermission("mortar.dispensers")) && !(player.isOp())) {
			return;
		}

		String playername = player.getName().toLowerCase();
		
		World world = loc.getWorld();
		int locX = loc.getBlockX();
		int locY = loc.getBlockY();
		int locZ = loc.getBlockZ();
		///////// >>>Database-Eintrag erstellen<<< /////////
		String entry = world + ":" + locX + ":" + locY + ":" + locZ;
		MortarMain.dispensersDB.addEntry(entry, playername);		
	}
	
	public static boolean checkEntry(Location loc) {
		
		World world = loc.getWorld();
		int locX = loc.getBlockX();
		int locY = loc.getBlockY();
		int locZ = loc.getBlockZ();
		
		String entry = world + ":" + locX + ":" + locY + ":" + locZ;
		
		if(MortarMain.dispensersDB.hasIndex(entry)) {
			return true;
		}else{
			return false;
		}	
	}
	
}
