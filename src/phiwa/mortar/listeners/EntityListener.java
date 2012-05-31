package phiwa.mortar.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import phiwa.mortar.MortarDatabases;
import phiwa.mortar.MortarMain;

public class EntityListener implements Listener {
	public static GameMode mode;

	public static Arrow arrow;
	
	public static int arrowID;
	
	public static ChatColor red = ChatColor.RED;
	public static ChatColor green = ChatColor.GREEN;
	public static ChatColor white = ChatColor.WHITE;

	MortarMain plugin;

	public EntityListener(MortarMain plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent event) {
		
		if(MortarMain.useDispensers == false) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if(!(player.hasPermission("mortar.dispensers"))) {
			return;
		}
		
		if(event.getBlock().getTypeId() != 23) {
			return;
		}
		
		Location loc = event.getBlockPlaced().getLocation();
		
		MortarDatabases.createEntry(loc, player);		//Dispenser ist explosiv!
		
	}
	
	
  
	@EventHandler
	public void onDispenserShotREAL(BlockDispenseEvent event) {
		
		if(MortarMain.useDispensers == false) {
System.out.println("1");
			return;
		}
		
		if(event.getBlock().getTypeId() != 23) {
System.out.println("2");
			return;
		}
		
		Location loc = event.getBlock().getLocation();
		
		if(MortarDatabases.checkEntry(loc) == false) {
System.out.println("3");
			return;		//Explosiver Dispenser?
		}
System.out.println("4");		
		Vector velocity = event.getVelocity();		
System.out.println("Velocity: " + velocity);
System.out.println("Dispenser-Location: " + loc);
		event.setCancelled(true);		
		//arrow = loc.getWorld().spawnArrow(loc, velocity, (float) 0.6, 12);   // Location, Velocity, Speed, Spread
///////////////////////////////////////////////////
Arrow arrow2 = loc.getWorld().spawnArrow(loc, velocity.multiply(10), (float)5.0, 12);
///////////////////////////////////////////////////
		//arrowID = arrow.getEntityId();
	}
	
	@EventHandler
	public void shootArrowREAL(ProjectileHitEvent event) {	
		
		Entity entity = event.getEntity();
		Location loc = entity.getLocation();
System.out.println("arrowID: " + arrowID);
System.out.println("Pfeil-ID: " + entity.getEntityId());
	  /*if(!(arrowID == entity.getEntityId())) {
			return;
		}*/
waiting(5);
loc.getBlock().setTypeId(7);
System.out.println(loc);
System.out.println("Explosion kommt jetzt");		
		//entity.getLocation().getWorld().createExplosion(loc, MortarMain.dispenserpower);
System.out.println("Explosion vorbei");
		arrowID = 0;

	}
	
///////////////////////////////////////////////////	
///////////////////////////////////////////////////
	public static void waiting (int n){
	        
	    long t0, t1;
	
	    t0 =  System.currentTimeMillis();
	
	    do{
	        t1 = System.currentTimeMillis();
	    }
	    while ((t1 - t0) < (n * 1000));
	}
///////////////////////////////////////////////////
///////////////////////////////////////////////////		
	
/*	
	
	@EventHandler
	public void onDispenserShotWORKING(BlockDispenseEvent event) {
		
		///////////////////////////////////////////////////
		
	}
	
	@EventHandler
	public void shootArrowWORKING(ProjectileHitEvent event) {	
		
		Entity entity = event.getEntity();
		Location loc = entity.getLocation();
		
		entity.getLocation().getWorld().createExplosion(loc, MortarMain.dispenserpower);

	}
*/
}