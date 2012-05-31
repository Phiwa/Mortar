package phiwa.mortar;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import TheEnd.XemsDoom.XemDB;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.configuration.file.YamlConfiguration;

import phiwa.mortar.listeners.EntityListener;
import phiwa.mortar.listeners.PlayerListener;

import java.util.*;


public class MortarMain extends JavaPlugin {
	PluginManager pm;
	
	public static Economy eco = null;
	
	public static boolean status = true;
	public static int power = 5;
	public static String type = "Explosive";
	public static int x = 0;
	public static int max = 5;
	public static int def = 5;
	public static int mortaritem = 280;
	
	public static float dispenserpower = 5;
	
	public static boolean useWarning = true;
	public static String warning = "Fire in the hole!";
	
	public static boolean useCosts = true;
	public static boolean useItems = false;
	public static boolean useEconomy = false;
	
	public static int costitemid = 266;
	public static int costitemamount = 1;
	
	public static double costeconomyamount = 10;
	
	public static boolean useWorldGuard = false;
	public static boolean useDispensers = false;
	
	public static ChatColor red = ChatColor.RED;
	public static ChatColor green = ChatColor.GREEN;
	public static ChatColor white = ChatColor.WHITE;
	
	public static XemDB dispensersDB;
	
	File configFile; // Config erstellen
	static FileConfiguration config; // "Configvariable" erstellen

	@SuppressWarnings("rawtypes")
	public static HashMap playerpower = new HashMap();
	
	

	
	@Override
	public void onDisable() {
		// Yamls speichern, wenn das Plugin beendet wird.
		System.out.println("Mortar has been disabled!");
	}

	@Override
	public void onEnable() {
		System.out.println("[Mortar] Enabling Mortar...");
		// ////////// Alle Aktionen, die nichts mit der Config- bzw.
		// Configvariablen-Initialisierung zu tun haben kommen nach ganz unten
		// in dieser onEnable()-Methode ////////////

		// Config-Dateien und -Variablen initialisieren...
		this.configFile = new File(getDataFolder(), "config.yml");

		// ...und dann die firstRun()-Methode durchlaufen lassen.
		try {
			this.firstRun();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[Mortar][Important] A serious problem occured, please contact the author of this plugin!");
		}

		// FileConfiguration-Variablen mithilfe der YAML-Konfiguration
		// deklarieren und die "loadYamls()"-Methode nutzen.
		// WICHTIG: Ohne diesen Schritt würde das Plugin die Daten aus der
		// Config.yml innerhalb der JAR-Datei auslesen anstatt die richtige
		// Config zu nutzen!!
		MortarMain.config = new YamlConfiguration();
		loadYamls();

		// ////////// Alle weiteren Aktionen, die das Plugin beim aktivieren
		// durchführen soll kommen unter diesen Kommentar: ////////////


		//Events registern!
		new PlayerListener(this);
		new EntityListener(this);
		
		
		/// Economy? ///		
		useEconomy = config.getBoolean("Costs.PayForShots");		
		economyCheck();
		
		// WorldGuard.getPlugin()
		getWorldGuard();
		if(useWorldGuard == true) {
				System.out.println("[Mortar] WorldGuard was found on this server.");
    	}
		useWorldGuard = config.getBoolean("UseWorldGuard");
		if(useWorldGuard == true) {
			System.out.println("[Mortar] WorldGuard-support has been enabled.");
			System.out.println("[Mortar] Will check regions' build-flags");
			System.out.println("[Mortar] for mortar-explosions.");
		}
		
		
		// Dispenser-Support?
		if(useDispensers == true) {
			MortarDatabases.loadDB();
		}
		
		
	}
		
	public void economyCheck() {
		//Economy in der Konfig aktiviert?
		if(useEconomy==true){
				
			//Vault im Plugin Ordner? - Wenn nicht, wird das Plugin deaktiviert
			Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
			
		        if(x != null & x instanceof Vault) {
		            System.out.println("[Mortar] Enabled Mortar.");
		            System.out.println("[Mortar] Using Vault for economy support.");
		            setupEconomy();
		        }else{
		        	useEconomy = false;
		        	System.out.println("[Mortar] Vault was not found!");
		        	System.out.println("[Mortar] This will produce errors, so please install");
		        	System.out.println("[Mortar] Vault or open your config and set");
		        	System.out.println("[Mortar] UseEconomy to false!");
		        }
		        
	    //Normaler Start up, wenn Economy ausgschaltet ist
		}else{
			System.out.println("[Mortar] Enabled Mortar.");
		}
		
	}
	
	public static boolean payForShot(Player player) {
		if(useItems == true) {
			if(payForShotItems(player) == true) {
				return true;
			}else{
				return false;
			}
		}else if(useEconomy == true) {
			if(payForShotEconomy(player) == true) {
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static boolean payForShotItems(Player player) {
		
			Inventory inv = player.getInventory();

			if (inv.contains(MortarMain.costitemid, MortarMain.costitemamount)) {

				inv.removeItem(new ItemStack(MortarMain.costitemid, MortarMain.costitemamount));
				player.updateInventory();

				return true;
			} else {
				player.sendMessage("You don't have enough resources to pay for the mortar.");
				return false;
			}
		
	}
	
	public static boolean payForShotEconomy(Player player) {
		
			String playername = player.getName();
					
			double balance = eco.getBalance(playername.toLowerCase());
					
			if( balance >= costeconomyamount){
				eco.withdrawPlayer(playername, costeconomyamount);
				player.sendMessage("You just paid " +costeconomyamount +" to fire your mortar.");
				return true;
			}else{
				player.sendMessage(red + "You don't have enough money!");
				return false;
			}					
			
	}
	
	
	//Economy Provider bekommen
	private Boolean setupEconomy(){
	    RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	    if (economyProvider != null){
	        eco = economyProvider.getProvider();
	    }
	    return (eco != null);
	}
	
	//WorldGuard - GetPlugin
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    if(useWorldGuard == true) {
		    // WorldGuard may not be loaded
		    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
		    	useWorldGuard = false;
		    	System.out.println("[Mortar][Warning] You set the config-option");
				System.out.println("[Mortar][Warning] UseWorldGuard to true, but");
				System.out.println("[Mortar][Warning] WorldGuard is not installed");
				System.out.println("[Mortar][Warning] on this server!");
				System.out.println("[Mortar][Warning] Deactivated the use of WorldGuard!");
		        return null; // Maybe you want throw an exception instead	        
		    }else{
		    	useWorldGuard = true;
		    }
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
		
	//WorldGuard - RegionCheck	
	public boolean regionCheckWG(Player player, Location location) {
		if(getWorldGuard().canBuild(player, location) == true) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Mortar-Methoden
	private void enableMortar(CommandSender sender) {
		if (sender.hasPermission("mortar.*") || sender.isOp()) {
			status = true;
			sender.sendMessage(green + "Mortar has been enabled!");
		} else {
			sender.sendMessage("You are no Admin...");
		}
	}

	private void disableMortar(CommandSender sender) {
		if (sender.hasPermission("mortar.*") || sender.isOp()) {
			status = false;
			sender.sendMessage(red + "Mortar has been disabled!");
		}

		else {
			sender.sendMessage("You are no Admin...");
		}
	}

	@SuppressWarnings("unchecked")
	// //....////
	private boolean setPower(CommandSender sender, String[] args) {
		if (!sender.hasPermission("mortar.use")
				&& !sender.hasPermission("mortar.*") && !sender.isOp()) {
			sender.sendMessage("You are not allowed to use this command...");
			return false;
		}
		if (args.length < 2) {
			return false;
		}
		try {
			String s = args[1];

			int i = Integer.parseInt(s);

			if (i >= 0 && i <= max) {

				power = i;
				String playername = sender.getName();
				playerpower.put(playername, i);
				power = def;

				sender.sendMessage("The power of your mortar has been set to "
						+ red + +i + white + ".");
			} else {
				sender.sendMessage("Please use a number between " + red + "0"
						+ white + " and " + red + max + white + ".");
			}
		} catch (NumberFormatException nfe) {
			sender.sendMessage("Please use a number");
		}
		return true;
	}

	private void showPower(CommandSender sender) {
		if (sender.hasPermission("mortar.use") || sender.hasPermission("mortar.*") || sender.isOp()) {

			String playername = sender.getName();
			power = (Integer) playerpower.get(playername);
			sender.sendMessage("Your mortar's power is " + red + power + white
					+ ".");
			power = def;

		} else {
			sender.sendMessage("You are not allowed to use this command...");
		}

	}

	private void showUsage(CommandSender sender) {
		sender.sendMessage("Use:");
		sender.sendMessage(red + "/mortar on" + white
				+ " - Enables the plugin.");
		sender.sendMessage(red + "/mortar off" + white
				+ " - Disables the plugin");
		sender.sendMessage(red + "/mortar setpower <value>" + white
				+ " - Sets the power of your mortar.");
		sender.sendMessage(red + "/mortar showpower" + white
				+ " - Shows you the power of your mortar.");
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("mortar")) {
			return false;
		}
		if (args.length <= 0) {
			this.showUsage(sender);
			return false;
		}
		if (args[0].equalsIgnoreCase("on")) {
			this.enableMortar(sender);
		} else if (args[0].equalsIgnoreCase("off")) {
			this.disableMortar(sender);
		} else if (args[0].equalsIgnoreCase("setpower")) {
			return this.setPower(sender, args);
		}
		// else if(args[0].equalsIgnoreCase("settype"))
		// {
		// return this.setType(sender, args);
		// }
		else if (args[0].equalsIgnoreCase("showpower")) {
			this.showPower(sender);
		}
		// else if(args[0].equalsIgnoreCase("showtype"))
		// {
		// this.showType(sender);
		// }
		else if (args[0].equalsIgnoreCase("help")) {
			this.showUsage(sender);
		}
		else if (args[0].equalsIgnoreCase("reload")) {
			this.loadYamls();
		}
		else {
			this.showUsage(sender);
		}
		return false;
	}

	
	// Config-bezogene Methoden//
	// Überprüfen, ob die Configs schon existieren, wenn nicht: Ordner und
	// Config erstellen
	private void firstRun() throws Exception {
		if (this.configFile.exists()) // Überprüft, ob die YAML vorhanden ist
		{
			return;
		}
		System.out.println("[Mortar] Mortar is run for the first time...");
		System.out.println("[Mortar] No Config found!");
		System.out.println("[Mortar] Creating default Config!");

		this.configFile.getParentFile().mkdirs(); // Erstellt das Verzeichnis
													// /plugins/<Pluginname>/,
													// falls es nicht gefunden
													// wurde
		this.copy(getResource("config.yml"), this.configFile); // Kopiert den
																// Inhalt der
																// Config aus
																// der JAR-File
																// in die
																// Default-Config

		System.out.println("[Mortar] Default Config was created successfully!");
	}

	// Defaults aus der Config in der JAR in die neue Config kopieren
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Lädt bzw. "relädt" die Configs
	public void loadYamls() {
		try {
			MortarMain.config.load(configFile); // Lädt die Werte der Config-Datei in
											// die FileConfig-Variable

			// Variablen aus der
			// Config ziehen
			
			max = config.getInt("Mortar.MaxPower");
			def = config.getInt("Mortar.DefaultPower");
			
			useWarning = config.getBoolean("Mortar.SendWarning");
			warning = config.getString("Mortar.Warning");
			
			mortaritem = config.getInt("Mortar.Mortar-Item");
			
			useCosts = config.getBoolean("Costs.PayForShots");
			useItems = config.getBoolean("Costs.UseItems");
			useEconomy= config.getBoolean("Costs.UseEconomy");
			
			costitemid = config.getInt("PayWithItems.Item");
			costitemamount = config.getInt("PayWithItems.Amount");
			
			costeconomyamount = config.getDouble("PayWithEconomy.Amount");
			
			useWorldGuard = config.getBoolean("UseWorldGuard");
			useDispensers = config.getBoolean("UseDispensers");

			if (max > 10)
			{
				max = 10;
				config.set("Mortar.MaxPower", max);
				System.out.println("[Mortar][IMPORTANT] The MaxPower must not be set"); // MaxPower-Limit überprüfen und ggf. ändern
				System.out.println("[Mortar][IMPORTANT] above 10 because that might");
				System.out.println("[Mortar][IMPORTANT] crash the server!");
				System.out.println("[Mortar][IMPORTANT] The MaxPower was automatically");
				System.out.println("[Mortar][IMPORTANT] set to 10!");
			}
			
			if (useCosts == true && useEconomy == true) {
				if(useCosts == false && useItems == false) {
					System.out.println("[Mortar] You actived Economy-Support, but you");
					System.out.println("[Mortar] didn't choose a type of payment!");
					System.out.println("[Mortar] How can players pay for a shot if");
					System.out.println("[Mortar] you deactive payment via items and");
					System.out.println("[Mortar] payment via economy?!");
				}
			}

			System.out.println("[Mortar] Config has been loaded successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[Mortar] No Config found!");
		}
	}

	// Speichert alle FileConfig-Variablen in die Config-Datei
	// Kann überall genutzt werden wo ein Wert geändert wird, sollte aber vor
	// allem bei "onDisable" durchlaufen
	public void saveYamls() {
		try {
			MortarMain.config.save(configFile); // Speichert die Werte der
											// FileConfig-Variablen in die
											// Config-Datei
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}