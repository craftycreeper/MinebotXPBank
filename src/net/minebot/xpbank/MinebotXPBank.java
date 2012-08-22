package net.minebot.xpbank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MinebotXPBank extends JavaPlugin {

	protected static File dataDir = new File("plugins/MinebotXPBank");
	
	private static YamlConfiguration accounts = new YamlConfiguration();
	
	public void onEnable() {
		
		//If folder does not exist, create it
		if (!dataDir.isDirectory()) {
			dataDir.mkdir();
		}
		
		try {
			accounts.load(dataDir + "/" + "accounts.yml");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
			getLogger().warning("Could not load accounts: invalid accounts file. Disabling.");
			return;
		}
		
		//Command
		getCommand("xpbank").setExecutor(new XPBankCommand(this));
		
		//Death listener for drop XP
		getServer().getPluginManager().registerEvents(new XPDropListener(), this);
		
		getLogger().info("Version " + getDescription().getVersion() + " enabled.");
	}

	public void onDisable() {
		getLogger().info("Version " + getDescription().getVersion() + " disabled.");
	}
	
	private static void saveAccounts() {
		try {
			accounts.save(dataDir + "/" + "accounts.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Integer getBalance(String name) {
		Object amountObj = accounts.get(name);
		if (amountObj == null) return null;
		else return accounts.getInt(name);
	}
	
	public static void setBalance(String name, Integer amount) {
		accounts.set(name, amount);
		saveAccounts();
	}
	
	public static void initAccount(String name) {
		accounts.set(name, 0);
		saveAccounts();
	}
	
	public static int calculateXP(Player p) {
		int xp = 0;
		
		for (int i = 1; i <= p.getLevel(); i++) {
			if (i < 17)
				xp += 17;
			else
				xp += ((i - 16) * 3) + 17;
		}
		
		int xpToNext = 0;
		int nextLevel = p.getLevel() + 1;
		
		if (nextLevel < 17)
			xpToNext = 17;
		else
			xpToNext = ((nextLevel - 16) * 3) + 17;
		
		int nextXP = (int)Math.floor(p.getExp() * xpToNext);
		
		return xp + nextXP;
	}
	
}
