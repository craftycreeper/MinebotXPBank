package net.minebot.xpbank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class XPDropListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		int xp = MinebotXPBank.calculateXP(player);
		event.setDroppedExp(xp);
		player.sendMessage(ChatColor.GOLD + "You dropped " + xp + " XP.");
	}
	
}
