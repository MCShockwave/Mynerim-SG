package net.mcshockwave.Mynerim.Commands;

import net.mcshockwave.Mynerim.SGMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			SGMap m = SGMap.getPlayerMap(p);
			if (m == null)
				return false;
			p.sendMessage("§cLeft map " + m.name + "!");
			m.removePlayer(p, true);
		}
		return false;
	}

}
