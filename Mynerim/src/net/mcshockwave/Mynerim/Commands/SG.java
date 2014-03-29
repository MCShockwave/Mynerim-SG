package net.mcshockwave.Mynerim.Commands;

import java.util.Random;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.Mynerim.MSG;
import net.mcshockwave.Mynerim.SGMap;
import net.mcshockwave.Mynerim.Items.ItemGen;
import net.mcshockwave.Mynerim.Items.ItemGen.ArmorType;
import net.mcshockwave.Mynerim.Items.ItemGen.Type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SG implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && sender.isOp()) {
			if (args[0].equalsIgnoreCase("start")) {
				SGMap.valueOf(args[1]).startCountdown();
			}
			if (args[0].equalsIgnoreCase("end")) {
				SGMap.valueOf(args[1]).end();
			}
			if (args[0].equalsIgnoreCase("join")) {
				SGMap.valueOf(args[1]).addPlayer(Bukkit.getPlayer(args[2]));
			}
			if (args[0].equalsIgnoreCase("leave")) {
				SGMap.valueOf(args[1]).removePlayer(Bukkit.getPlayer(args[2]), true);
			}
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				try {
					if (args[0].equalsIgnoreCase("genRandom")) {
						if (args.length > 1) {
							Random rand = new Random();
							p.getInventory().addItem(
									ItemGen.getRandom(Type.valueOf(args[1])).getItem(
											args.length > 2 ? ArmorType.valueOf(args[2].toUpperCase()) : ArmorType
													.values()[rand.nextInt(ArmorType.values().length)]));
						} else {
							p.getInventory().addItem(ItemGen.getRandom(null).getItem(null));
						}
					}
					if (args[0].equalsIgnoreCase("reload")) {
						reload();

						p.sendMessage("§cReloaded");
					}
					if (args[0].equalsIgnoreCase("listChests")) {
						p.sendMessage(MSG.mapChests.toString());
					}
					if (args[0].equalsIgnoreCase("generateChests")) {
						SGMap.valueOf(args[1]).generateLoot(false);
						p.sendMessage("§cGenerated Chests for map " + args[1]);
					}
					if (args[0].equalsIgnoreCase("clearChests")) {
						SGMap.valueOf(args[1]).generateLoot(true);
						p.sendMessage("§cCleared Chests for map " + args[1]);
					}
					if (args[0].equalsIgnoreCase("setspawn")) {
						Location l = p.getLocation();
						p.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
					}
					if (args[0].equalsIgnoreCase("start")) {
						SGMap.valueOf(args[1]).startCountdown();
					}
					if (args[0].equalsIgnoreCase("end")) {
						SGMap.valueOf(args[1]).end();
					}
					if (args[0].equalsIgnoreCase("join")) {
						SGMap.valueOf(args[1]).addPlayer(Bukkit.getPlayer(args[2]));
					}
					if (args[0].equalsIgnoreCase("leave")) {
						SGMap.valueOf(args[1]).removePlayer(Bukkit.getPlayer(args[2]), true);
					}
					if (args[0].equalsIgnoreCase("loot")) {
						Inventory i = Bukkit.createInventory(null, 9, "Loot chests");
						for (SGMap sg : SGMap.values()) {
							i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.CHEST), "Loot:" + sg.name()));
						}
						p.openInventory(i);
					}
				} catch (Exception e) {
					p.sendMessage("§4Error: §c" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static void reload() {
		MSG.ins.saveConfig();
		MSG.ins.reloadConfig();

		MSG.ins.loadChests();
	}

}
