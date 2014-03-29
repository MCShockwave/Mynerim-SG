package net.mcshockwave.Mynerim;

import net.mcshockwave.DragonShouts.Shout;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.Mynerim.Commands.Join;
import net.mcshockwave.Mynerim.Commands.Leave;
import net.mcshockwave.Mynerim.Commands.SG;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MSG extends JavaPlugin {

	public static final Shout[]				shouts		= { Shout.Become_Ethereal, Shout.Cyclone, Shout.Dragon_Aspect,
			Shout.Drain_Vitality, Shout.Elemental_Fury, Shout.Fire_Breath, Shout.Frost_Breath, Shout.Ice_Form,
			Shout.Marked_For_Death, Shout.Slow_Time, Shout.Soul_Tear, Shout.Storm_Call, Shout.Unrelenting_Force,
			Shout.Whirlwind_Sprint						};

	public static MSG						ins			= null;

	public static HashMap<Block, String>	mapChests	= new HashMap<>();

	public static Location					spawn;

	public void onEnable() {
		ins = this;

		Bukkit.getPluginManager().registerEvents(new DefaultListener(), this);

		getCommand("sg").setExecutor(new SG());
		getCommand("join").setExecutor(new Join());
		getCommand("leave").setExecutor(new Leave());

		MCShockwave.mesJoin = "";
		MCShockwave.mesKick = "";
		MCShockwave.mesLeave = "";

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				loadChests();
			}
		}, 10l);

		spawn = new Location(dW(), 534.5, 94.5, 87.5);

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getGameMode() != GameMode.CREATIVE) {
				p.teleport(spawn);
			}
		}
	}

	public static World dW() {
		return Bukkit.getWorld("Mynerim Map 1");
	}

	public Shout getRandShout() {
		Random rand = new Random();
		return shouts[rand.nextInt(shouts.length)];
	}

	public void addChest(Block b, String map) {
		List<String> ss = getConfig().getStringList("chests");

		Location l = b.getLocation();

		String add = l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ() + ";"
				+ map;
		ss.add(add);
		mapChests.put(b, map);

		getConfig().set("chests", ss);

		saveConfig();
	}

	public void delChest(Block b, String map) {
		List<String> ss = getConfig().getStringList("chests");

		Location l = b.getLocation();

		String rem = l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ() + ";"
				+ map;
		ss.remove(rem);
		mapChests.remove(b);

		getConfig().set("chests", ss);

		saveConfig();
	}

	public HashMap<Block, String> getChestsFromConfig() {
		HashMap<Block, String> c = new HashMap<>();

		for (String s : getConfig().getStringList("chests")) {
			String[] ss = s.split(";");
			World w = Bukkit.getWorld(ss[0]);
			int x = Integer.parseInt(ss[1]);
			int y = Integer.parseInt(ss[2]);
			int z = Integer.parseInt(ss[3]);

			String type = ss[4];
			Block b = w.getBlockAt(x, y, z);

			c.put(b, type);
		}

		return c;
	}

	public void loadChests() {
		mapChests = new HashMap<>();
		mapChests = getChestsFromConfig();
	}

	public void onDisable() {
		for (SGMap m : SGMap.values()) {
			m.end();
			m.updateSigns();
		}
	}

	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}

}
