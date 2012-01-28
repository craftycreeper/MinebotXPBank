package net.minebot.xpbank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinebotXPBankCommand implements CommandExecutor {

	//private MinebotXPBank plugin;
	
	public MinebotXPBankCommand(MinebotXPBank plugin) {
		//this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String xpcommand = "balance";
		if (args.length >= 1) {
			xpcommand = args[0];
		}
		
		if (xpcommand.equals("balance") || xpcommand.equals("bal")) {
			commandBalance(sender, args);
		}
		
		else if (xpcommand.equals("withdraw") || xpcommand.equals("take")) {
			commandWithdraw(sender, args);
		}
		
		else if (xpcommand.equals("deposit") || xpcommand.equals("put")) {
			commandDeposit(sender, args);
		}
		
		else if (xpcommand.equals("help")) {
			commandHelp(sender);
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.WHITE + " /xpbank help " + ChatColor.GREEN + "for commands.");
		}
		
		return true;
	}
	
	private void commandBalance(CommandSender sender, String[] args) {
		String acct = null;
		Player target = null;
		
		if (args.length >= 2)
			acct = args[1];
		else if (args.length < 2 && sender instanceof Player) {
			target = (Player)sender;
			acct = target.getName();
		}
		
		if (acct == null) {
			sender.sendMessage(ChatColor.RED + "The console does not have an XP bank account. Use xpbank bal <player>");
			return;
		}
		
		//Get amount
		Integer amount = MinebotXPBank.getBalance(acct);
		if (amount == null && target == null) {
			sender.sendMessage(ChatColor.GREEN + acct + " does not have an XP bank account.");
			return;
		}
		else if (amount == null && target != null) {
			 MinebotXPBank.initAccount(acct);
			 amount = 0;
		}
		
		sender.sendMessage(ChatColor.GREEN + "XP bank balance for " + acct + ": " + ChatColor.WHITE + amount + " XP");
		
		if (target != null) {
			sender.sendMessage(ChatColor.GREEN + "XP on hand: " + ChatColor.WHITE + calculateXP(target) + " XP ");
		}
	}
	
	private void commandWithdraw(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have an XP bank account.");
			return;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /xpbank withdraw <amount>");
			return;
		}
		
		Player p = (Player)sender;
		Integer currentBalance = MinebotXPBank.getBalance(p.getName());
		if (currentBalance == null) {
			MinebotXPBank.initAccount(p.getName());
			currentBalance = 0;
		}
		
		Integer withdrawAmount;
		
		if (args[1].equals("all")) {
			withdrawAmount = currentBalance;
		}
		else {
			try {
				withdrawAmount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Please provide a valid number amount to withdraw!");
				return;
			}
		}
		
		int newBalance = currentBalance - withdrawAmount;
		if (newBalance < 0) {
			sender.sendMessage(ChatColor.RED + "You don't have that many XP to withdraw.");
		}
		else {
			MinebotXPBank.setBalance(p.getName(), newBalance);
			sender.sendMessage(withdrawAmount.toString() + " XP " + ChatColor.GREEN + "withdrawn.");
			sender.sendMessage(ChatColor.GREEN + "Remaining balance: " + ChatColor.WHITE + newBalance + " XP");
			
			int oldExp = calculateXP(p);
			p.setLevel(0);
			p.setExp(0);
			p.giveExp(oldExp + withdrawAmount);
		}
		
	}
	
	private void commandDeposit(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "The console does not have an XP bank account.");
			return;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /xpbank deposit <amount>");
			return;
		}
		
		Player p = (Player)sender;
		
		Integer depositAmount;
		
		if (args[1].equals("all")) {
			depositAmount = calculateXP(p);
		}
		else {
			try {
				depositAmount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Please provide a valid number amount of XP to deposit!");
				return;
			}
		}
		
		Integer currentBalance = MinebotXPBank.getBalance(p.getName());
		if (currentBalance == null) {
			MinebotXPBank.initAccount(p.getName());
			currentBalance = 0;
		}
		
		int oldExp = calculateXP(p);
		if (depositAmount > oldExp) {
			sender.sendMessage(ChatColor.RED + "You don't have that many XP to deposit.");
		}
		else {
			int newBalance = currentBalance + depositAmount;
			MinebotXPBank.setBalance(p.getName(), newBalance);
			sender.sendMessage(depositAmount.toString() + " XP " + ChatColor.GREEN + "deposited.");
			sender.sendMessage(ChatColor.GREEN + "New balance: " + ChatColor.WHITE + newBalance + " XP");
			
			p.setLevel(0);
			p.setExp(0);
			p.giveExp(oldExp - depositAmount);
		}
		
	}
	
	private void commandHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "XP Bank Commands:");
		sender.sendMessage("/xpbank balance | bal" + ChatColor.GRAY + " - Check your XP balance & XP on hand");
		sender.sendMessage("/xpbank deposit | put <amount or 'all'>" + ChatColor.GRAY + " - Deposit XP");
		sender.sendMessage("/xpbank withdraw | take <amount or 'all'>" + ChatColor.GRAY + " - Withdraw XP");
	}
	
	private int calculateXP(Player p) {
		int i = 0;
		int xp = 0;
		while (i < p.getLevel()) {
			xp += 7 + Math.floor(i * 3.5);
			i++;
		}
		
		int nextXP = (int)Math.floor(p.getExp() * (7 + Math.floor(p.getLevel() * 3.5)));
		
		return xp + nextXP;
	}
	
}
