package phiwa.mortar.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import phiwa.mortar.MortarMain;

public class PlayerListener implements Listener {
	public static GameMode mode;

	public static ChatColor red = ChatColor.RED;
	public static ChatColor green = ChatColor.GREEN;
	public static ChatColor white = ChatColor.WHITE;
	public static Player activeplayer;
	public static int eggid;
	public static boolean cooldown = false;

	MortarMain plugin;

	 public PlayerListener(MortarMain plugin) {
		 plugin.getServer().getPluginManager().registerEvents(this, plugin);
	 }
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if(!(MortarMain.status == true)) {
			return;
		}				
		if(event.getAction()==Action.LEFT_CLICK_AIR || event.getAction()==Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		Player player = event.getPlayer();
		int item = player.getItemInHand().getTypeId();
			
		if (player.hasPermission("mortar.use") || player.hasPermission("mortar.*") || player.isOp()) {
			if (item == MortarMain.mortaritem && cooldown == false) {						
				if (MortarMain.useCosts == true) {
					if(MortarMain.payForShot(player) == true) {
						if(MortarMain.useWarning == true) {
							player.chat(red + MortarMain.warning);
						}
						Egg egg = player.throwEgg();
						eggid = egg.getEntityId();
						cooldown = true;
					}	
				}else{
					if(MortarMain.useWarning == true) {
						player.chat(red + MortarMain.warning);
					}
					Egg egg = player.throwEgg();
					eggid = egg.getEntityId();
					cooldown = true;	
				}							
				return;	
			}else if (cooldown == true) {
				player.sendMessage("Please wait for the impact of your current mortar.");
			}else if (item == MortarMain.mortaritem && !player.hasPermission("mortar.use") && !player.hasPermission("mortar.*") && !player.isOp()) {
				player.sendMessage(white + "You do not have the permission to use the mortar.");
			}	
		}
	}	

	
	// Mörser beim Ei-Wurf abschießen, wenn dieser durch das gesetzte Item
	// ausgelöst wurde
	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		
		// Player player = event.getPlayer();
		Egg thrownegg = event.getEgg();
		int throwneggid = thrownegg.getEntityId();
		if (eggid == throwneggid) {

			Egg e = event.getEgg();
			Location pos = e.getLocation();
			Player player = event.getPlayer();
			String playername = event.getPlayer().getName();
			if(MortarMain.useWorldGuard == true) {
				if(plugin.regionCheckWG(player, pos) == true) {
					pos.getWorld().createExplosion(pos, (Integer) MortarMain.playerpower.get(playername), true);
					cooldown = false;
				}else{
					player.sendMessage("Nice try, but you aren't allowed to shoot there.");
					cooldown = false;
				}
			}else{
				pos.getWorld().createExplosion(pos, (Integer) MortarMain.playerpower.get(playername), true);
				cooldown = false;
			}
		}

	}

	// Beim Joinen eines Spielers dessen Mörserstärke auf den Default-Wert
	// setzen,
	// um eine Commandexception beim nutzen des "showpower"-Commands zu
	// verhindern,
	// wenn vorher nicht der "setpower"-Command genutzt wurde.

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();

		MortarMain.playerpower.put(playername, MortarMain.def);

	}

}