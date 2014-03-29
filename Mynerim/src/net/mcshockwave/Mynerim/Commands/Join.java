package net.mcshockwave.Mynerim.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.Mynerim.SGMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (args.length > 0) {
				for (SGMap m : SGMap.values()) {
					if (m.name().equalsIgnoreCase(args[0]) && !m.started && SGMap.getPlayerMap(p) != m) {
						if (m.players.size() < m.spawns.length) {
							if (m.players.contains(p.getName())) {
								m.removePlayer(p, false);
							}
							m.addPlayer(p);
							break;
						} else
							MCShockwave.send(ChatColor.RED, p, "Game is %s!", "full");
					}
				}
			}
		}
		return false;
	}

}
