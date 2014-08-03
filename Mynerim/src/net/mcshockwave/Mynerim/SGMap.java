package net.mcshockwave.Mynerim;

import net.mcshockwave.DragonShouts.DragonShouts;
import net.mcshockwave.DragonShouts.Shout;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Currency.ItemsUtils;
import net.mcshockwave.MCS.Currency.LevelUtils;
import net.mcshockwave.MCS.Currency.PointsUtils;
import net.mcshockwave.MCS.Stats.Statistics;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;
import net.mcshockwave.Mynerim.Commands.SG;
import net.mcshockwave.Mynerim.Items.ItemGen;
import net.mcshockwave.Mynerim.Items.ItemGen.ArmorType;
import net.mcshockwave.Mynerim.Items.ItemGen.Type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;

public enum SGMap {

	Blackrun(
		l(25, 74, 70, 270),
		l(27, 74, 64, 300),
		l(31, 74, 60, 310),
		l(37, 74, 58),
		l(43, 74, 60, 30),
		l(47, 74, 64, 40),
		l(49, 74, 70, 90),
		l(47, 74, 76, 120),
		l(43, 74, 80, 130),
		l(37, 74, 82, 180),
		l(31, 74, 80, 210),
		l(27, 74, 76, 220)),
	Irkngthand(
		l(998, 129, 1, 270),
		l(1000, 129, -8, 300),
		l(1006, 129, -14, 310),
		l(1015, 129, -16),
		l(1024, 129, -14, 30),
		l(1030, 129, -8, 40),
		l(1032, 129, 1, 90),
		l(1030, 129, 10, 120),
		l(1024, 129, 16, 130),
		l(1015, 129, 18, 180),
		l(1006, 129, 16, 210),
		l(1000, 129, 10, 220));

	public String				name;

	public Location[]			spawns;
	public ArrayList<String>	players;
	public ArrayList<String>	dead;
	public ArrayList<Item>		items;
	public boolean				started		= false, grace = false, deathmatch = false, startCount = false;

	public Scoreboard			sb			= null;
	public Objective			playersLeft	= null;
	public Objective			health		= null;

	SGMap(Location... spawns) {
		this.players = new ArrayList<>();
		this.dead = new ArrayList<>();
		this.items = new ArrayList<>();
		this.spawns = spawns;
		this.name = name().replace('_', ' ');

		if (name().equals("Blackrun")) {
			Location[] sps = spawns;
			for (int i = 0; i < sps.length; i++) {
				sps[i].add(0, 0.5, 0);
			}
		}
	}

	public static Location l(int x, int y, int z) {
		return new Location(MSG.dW(), x, y, z).add(0.5, 0, 0.5);
	}

	public static Location l(int x, int y, int z, int p) {
		return new Location(MSG.dW(), x, y, z, p, 0).add(0.5, 0, 0.5);
	}

	public static SGMap getPlayerMap(Player p) {
		for (SGMap m : values()) {
			if (m.players.contains(p.getName())) {
				return m;
			}
		}
		return null;
	}

	public static boolean isASpec(Player p) {
		return SGMap.getPlayerMap(p) != null && SGMap.getPlayerMap(p).dead.contains(p.getName())
				|| SGMap.getPlayerMap(p) == null;
	}

	public void addSpec(Player p) {
		if (sb != null) {
			p.setScoreboard(sb);
		}
		players.add(p.getName());
		dead.add(p.getName());
		MSG.clearInv(p);
		p.setHealth(20);
		p.teleport(spawns[0]);
		MCShockwave.send(ChatColor.DARK_AQUA, p, "%s to spectate players!", "Left-click");

		p.setAllowFlight(true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10000000, 10));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 0));

		updateScoreboard();
		updateSigns();
	}

	public ArrayList<Player> getAlive() {
		ArrayList<Player> ps = new ArrayList<>();

		for (String s : players) {
			if (!dead.contains(s)) {
				ps.add(Bukkit.getPlayer(s));
			}
		}
		return ps;
	}

	public Location getPlayerSpawn(Player p) {
		return spawns[players.indexOf(p.getName())];
	}

	public void regScoreboard() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();

		playersLeft = sb.registerNewObjective("PlayersLeft", "dummy");
		playersLeft.setDisplayName("§a§lAlive Players:");
		playersLeft.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (String s : players) {
			Player p = Bukkit.getPlayer(s);

			playersLeft.getScore(p).setScore(1);
			playersLeft.getScore(p).setScore(0);
		}

		health = sb.registerNewObjective("Health", "health");
		health.setDisplayName("/ 20");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);

		updateScoreboard();
	}

	public void updateScoreboard() {
		if (sb == null) {
			regScoreboard();
		}
		for (String s : players) {
			Player p = Bukkit.getPlayer(s);
			p.setScoreboard(sb);

			if (dead.contains(p.getName())) {
				playersLeft.getScoreboard().resetScores(p);
			}
		}

		for (OfflinePlayer op : sb.getPlayers()) {
			if (!players.contains(op.getName()) || dead.contains(op.getName())) {
				sb.resetScores(op);
			}
		}
	}

	public void addPlayer(final Player p) {
		int pid = players.size();
		if (pid >= spawns.length) {
			p.sendMessage("§cGame is full!");
		} else {
			players.add(p.getName());

			tpPlayerToPodium(p);

			resetPlayer(p);

			Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
				public void run() {
					for (Player p2 : getAlive()) {
						p2.showPlayer(p);
						p.showPlayer(p2);
					}
				}
			}, 10l);

			if (pid >= 3 && !startCount) {
				startCountdown();
			} else if (pid <= 3 && startCount) {
				cancelCount();
			}
		}

		updateSigns();

		broadcast(ChatColor.DARK_AQUA, "%s has joined", p.getName());
	}

	public void cancelCount() {
		for (BukkitRunnable br : scdTasks) {
			br.cancel();
		}
		scdTasks.clear();
		startCount = false;
	}

	public void tpPlayerToPodium(Player p) {
		int pid = players.indexOf(p.getName());
		Location spawn = this.spawns[pid];
		if (!spawn.getChunk().isLoaded()) {
			spawn.getChunk().load();
		}
		p.teleport(spawn);
	}

	public static void resetPlayer(Player p) {
		if (p.getGameMode() != GameMode.ADVENTURE) {
			p.setGameMode(GameMode.ADVENTURE);
		}
		p.setAllowFlight(false);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setSaturation(5);
		MSG.clearInv(p);

		for (PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}
	}

	@SuppressWarnings("deprecation")
	public static void resetDurability(Player p) {
		PlayerInventory pi = p.getInventory();

		for (ItemStack it : pi.getContents()) {
			if (it != null && it.getType() != Material.AIR && it.getType().getMaxDurability() > 16) {
				if (it.getDurability() > 0) {
					it.setDurability((short) 0);
				}
			}
		}
		for (ItemStack it : pi.getArmorContents()) {
			if (it != null && it.getType() != Material.AIR) {
				if (it.getDurability() > 0) {
					it.setDurability((short) 0);
				}
			}
		}

		p.updateInventory();
	}

	public void removePlayer(Player p, boolean kill) {
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

		players.remove(p.getName());
		dead.remove(p.getName());
		resetPlayer(p);
		p.teleport(MSG.spawn);

		if (!started) {
			for (String ps : players) {
				Player p2 = Bukkit.getPlayer(ps);
				tpPlayerToPodium(p2);
			}
			broadcast(ChatColor.DARK_AQUA, "%s has left", p.getName());
		} else if (kill) {
			onDeath(p);
			updateScoreboard();
		}
		updateSigns();

		Shout.clearShouts(p);
	}

	public String getItemNameDeath(ItemStack it) {
		String ret = "";
		if (ItemMetaUtils.hasCustomName(it)) {
			ret = ChatColor.stripColor(ItemMetaUtils.getItemName(it));
		} else
			ret = WordUtils.capitalizeFully(it.getType().name().replace('_', ' '));
		return ret;
	}

	public String getDeathMessage(Player p) {
		String message = "§a%p§7 was killed".replace("%p", p.getName());
		if (p.getKiller() != null) {
			Score s = playersLeft.getScore(p.getKiller());
			s.setScore(s.getScore() + 1);

			message += " by §a%k§7 using §6%i".replace("%k", p.getKiller().getName()).replace("%i",
					getItemNameDeath(p.getKiller().getItemInHand()));

			Random rand = new Random();
			int minXp = SQLTable.Settings.getInt("Setting", "XPMin", "Value");
			int maxXp = SQLTable.Settings.getInt("Setting", "XPMax", "Value");
			LevelUtils.addXP(p.getKiller(), rand.nextInt(maxXp - minXp) + minXp, "killing " + p.getName(), true);
		}
		return message;
	}

	public void onDeath(Player p) {
		if (!dead.contains(p.getName()) && players.contains(p.getName())) {
			broadcast(ChatColor.GRAY, getDeathMessage(p));
			for (ItemStack it : p.getInventory().getContents()) {
				if (it != null && it.getType() != Material.AIR) {
					items.add(p.getWorld().dropItemNaturally(p.getEyeLocation(), it));
				}
			}
			for (ItemStack it : p.getInventory().getArmorContents()) {
				if (it != null && it.getType() != Material.AIR) {
					items.add(p.getWorld().dropItemNaturally(p.getEyeLocation(), it));
				}
			}
			MSG.clearInv(p);
			dead.add(p.getName());

			int left = players.size() - dead.size();
			if (left != 1 && left < 4 && !deathmatch) {
				broadcast(ChatColor.DARK_AQUA, "There are %s players still alive", left);
				deathmatchCount();
			} else if (left == 1) {
				for (String ps : players) {
					if (!dead.contains(ps)) {
						MCShockwave.broadcast(ChatColor.AQUA, "%s has won on map %s with %s health left!", ps, name,
								(int) Bukkit.getPlayer(ps).getHealth());
						PointsUtils.addPoints(Bukkit.getPlayer(ps), players.size() * 100, "winning");
						Statistics.incrWins(ps, true);
						break;
					}
				}
				end();
			} else if (left < 1) {
				end();
			} else
				broadcast(ChatColor.DARK_AQUA, "There are %s players still alive", left);

			for (String ps : players) {
				Player p2 = Bukkit.getPlayer(ps);
				p2.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 100, 2);
			}

			Shout.clearShouts(p);

			p.setHealth(20);
			p.teleport(spawns[0]);
			MCShockwave.send(ChatColor.DARK_AQUA, p, "%s to spectate players!", "Left-click");

			p.setAllowFlight(true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10000000, 10));
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 0));

			updateScoreboard();
			updateSigns();
		}
	}

	public boolean	canMove	= true;

	public void deathmatchCount() {
		deathmatch = true;
		int[] count = { 45, 30, 15, 10, 5, 4, 3, 2, 1 };
		int time = count[0];
		for (int i : count) {
			final int i2 = i;
			Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
				public void run() {
					broadcast(ChatColor.YELLOW, "Deathmatch starting in %s second" + (i2 == 1 ? "" : "s") + "!", i2);
					if (i2 == 10) {
						canMove = false;
						for (String ps : players) {
							Player p = Bukkit.getPlayer(ps);
							if (!dead.contains(ps)) {
								tpPlayerToPodium(p);
								p.setNoDamageTicks(250);
							} else
								p.teleport(spawns[0]);
						}
					}
				}
			}, (time * 20) - (i * 20));
		}
		Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
			public void run() {
				deathmatch();
			}
		}, time * 20);
	}

	BukkitRunnable	dmFw	= null;

	List<Player>	yfw		= new ArrayList<>();

	public void deathmatch() {
		broadcast(ChatColor.YELLOW, "%s started!", "§lDeathmatch");

		for (Player p : getAlive()) {
			if (ItemsUtils.hasItem(p.getName(), SQLTable.MynerimItems, "Yellow_Fw")) {
				yfw.add(p);
			}
		}

		String xyz = spawns[0].getBlockX() + " " + spawns[0].getBlockY() + " " + spawns[0].getBlockZ();
		for (String s : players) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound mynerim.deathmatch " + s + " " + xyz
					+ " 10 1 100");
		}

		canMove = true;

		dmFw = new BukkitRunnable() {
			public void run() {
				for (Player p : getAlive()) {
					Firework fire = (Firework) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.FIREWORK);
					FireworkMeta fm = fire.getFireworkMeta();
					FireworkEffect f = FireworkEffect.builder().withColor(yfw.contains(p) ? Color.YELLOW : Color.RED)
							.withTrail().build();
					fm.addEffect(f);
					fm.setPower(1);
					fire.setFireworkMeta(fm);
				}
			}
		};

		dmFw.runTaskTimer(MSG.ins, 100, 100);

		updateSigns();
	}

	ArrayList<BukkitRunnable>	scdTasks	= new ArrayList<>();

	public void startCountdown() {
		startCount = true;
		int[] br = { 60, 45, 30, 15, 10, 5, 4, 3, 2, 1 };
		int ti = br[0];

		for (int i : br) {
			long delay = (ti - i) * 20;

			final int i2 = i;
			BukkitRunnable bur = new BukkitRunnable() {
				public void run() {
					broadcast(ChatColor.YELLOW, "Game starting in %s second" + (i2 == 1 ? "" : "s") + "!", i2);
					if (i2 <= 5) {
						for (Player p : getAlive()) {
							p.playSound(p.getLocation(), Sound.CLICK, 5, 2);
						}
					}
				}
			};
			scdTasks.add(bur);
			bur.runTaskLater(MSG.ins, delay);
		}

		new BukkitRunnable() {
			public void run() {
				startGame();
			}
		}.runTaskLater(MSG.ins, ti * 20);
	}

	BukkitRunnable	wws	= null;

	public void startGame() {
		grace = true;
		started = true;
		broadcast(ChatColor.GREEN, "Game %s!", "started");
		broadcast(ChatColor.GREEN, "You have %s seconds of grace period!", 30);
		Random rand = new Random();

		for (String ps : players) {
			Player p = Bukkit.getPlayer(ps);
			p.getInventory().addItem(ItemGen.getRandom(Type.Food).getItem(null));
			p.getInventory().addItem(
					ItemGen.getRandom(Type.Armor).getItem(ArmorType.values()[rand.nextInt(ArmorType.values().length)]));
			p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 10, 1);
			Shout s = MSG.ins.getRandShout();
			s.setLearned(p);
			p.sendMessage(" ");
			MCShockwave.send(ChatColor.RED, p, "Your starting shout is %s (" + s.name + ")", s.w1);
			MCShockwave.send(ChatColor.DARK_AQUA, p, "Type %s to use it, or %s to bind it to your current held item",
					"/ds " + s.w1, "/ds bind " + s.w1);

			p.setNoDamageTicks(600);

			if (ItemsUtils.hasItem(p.getName(), SQLTable.MynerimItems, "Extra_Shout")) {
				Shout s2 = MSG.ins.getRandShout();
				s2.setLearned(p);
				MCShockwave.send(ChatColor.RED, p, "Your extra shout is %s (" + s2.name + ")", s == s2 ? s2.w2 : s2.w1);
				ItemsUtils.addItem(p.getName(), SQLTable.MynerimItems, "Extra_Shout", -1);
			}
			if (ItemsUtils.hasItem(p.getName(), SQLTable.MynerimItems, "Start_Weapon")) {
				if (rand.nextBoolean()) {
					p.getInventory().addItem(ItemGen.getRandom(Type.Bow).getItem(null));
					p.getInventory().addItem(ItemGen.getRandom(Type.Other).getItem(null));
				} else {
					p.getInventory().addItem(ItemGen.getRandom(Type.Sword).getItem(null));
				}
				MCShockwave.send(p, "You got a starting weapon!");
				ItemsUtils.addItem(p.getName(), SQLTable.MynerimItems, "Start_Weapon", -1);
			}
		}

		for (WordWalls ww : WordWalls.getWalls(this)) {
			setWordWall(ww);
		}

		wws = new BukkitRunnable() {
			public void run() {
				for (Block b : DragonShouts.word_walls.keySet()) {
					PacketUtils.playParticleEffect(ParticleEffect.ENCHANTMENT_TABLE,
							b.getLocation().add(0.5, 0.5, 0.5), 0, 1, 25);
				}
			}
		};

		wws.runTaskTimer(MSG.ins, 10, 10);

		Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
			public void run() {
				int t = 0;
				boolean gen = generateLoot(false);
				while (!gen && t < 250) {
					gen = generateLoot(false);
					t++;
				}
			}
		}, 1l);

		Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
			public void run() {
				broadcast(ChatColor.RED, "%s is over!", "Grace Period");
				grace = false;
			}
		}, 600l);

		refill = new BukkitRunnable() {
			public void run() {
				broadcast(ChatColor.YELLOW, "Chests have been %s!", "refilled");

				int t = 0;
				boolean gen = generateLoot(false);
				while (!gen && t < 250) {
					gen = generateLoot(false);
					t++;
				}
			}
		};
		refill.runTaskLater(MSG.ins, 6000);

		autoDM = new BukkitRunnable() {
			public void run() {
				if (!deathmatch) {
					deathmatchCount();
				}
			}
		};
		autoDM.runTaskLater(MSG.ins, 12000);

		regScoreboard();
		updateSigns();
	}

	BukkitRunnable	refill	= null, autoDM = null;

	public boolean generateLoot(boolean justClear) {
		try {
			Random rand = new Random();
			for (Map.Entry<Block, String> e : MSG.mapChests.entrySet()) {
				if (e.getValue().equalsIgnoreCase(name())) {
					Block b = e.getKey();
					if (b != null && b.getType() == Material.CHEST && b.getState() instanceof Chest) {
						Chest c = (Chest) b.getState();
						Inventory i = c.getBlockInventory();
						if (i != null) {
							i.clear();

							if (!justClear) {
								for (int x = 0; x < rand.nextInt(5) + 2; x++) {
									i.setItem(
											rand.nextInt(27),
											ItemGen.getRandom(Type.values()[rand.nextInt(Type.values().length)])
													.getItem(
															ArmorType.values()[rand.nextInt(ArmorType.values().length)]));
								}
							}

							c.update();
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			SG.reload();
			return false;
		}
	}

	public void end() {
		for (BukkitRunnable bt : new BukkitRunnable[] {dmFw, refill, autoDM, wws}) {
			try {
				if (bt == null) {
					continue;
				}
				bt.cancel();
			} catch (Exception e) {
			}
		}
		Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
			public void run() {
				for (String ps : players.toArray(new String[0])) {
					Player p = Bukkit.getPlayer(ps);
					resetPlayer(p);
					p.setNoDamageTicks(20);
					removePlayer(p, false);
				}
				players.clear();
			}
		}, 1l);
		Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
			public void run() {
				updateSigns();
			}
		}, 20);
		try {
			clearShoutsConfig();
		} catch (Exception e) {
		}
		started = false;
		startCount = false;
		deathmatch = false;
		canMove = true;
		playersLeft.unregister();
		health.unregister();
		sb = null;
		generateLoot(true);
		dead.clear();
		for (Item i : items) {
			i.remove();
		}
		items.clear();
		yfw.clear();
	}

	public void updateSigns() {
		for (Sign s : DefaultListener.signs.keySet().toArray(new Sign[0])) {
			SGMap m = DefaultListener.signs.get(s);
			if (m == this) {
				DefaultListener.setSignText(s, this);
			}
		}
	}

	public void broadcast(ChatColor c, String s, Object... args) {
		for (String ps : players) {
			Player p = Bukkit.getPlayer(ps);
			MCShockwave.send(c, p, s, args);
		}
	}

	public void setWordWall(WordWalls w) {
		DragonShouts.ins.addWordWall(w.l.getBlock(), MSG.ins.getRandShout());
	}

	public void clearShoutsConfig() {
		FileConfiguration da = DragonShouts.learnedData;
		List<String> wws = da.getStringList("word_walls");
		List<String> lww = da.getStringList("used_word_walls");

		for (WordWalls w : WordWalls.getWalls(this)) {
			for (String s : wws.toArray(new String[0])) {
				if (s.startsWith(w.l.getBlockX() + ";" + w.l.getBlockY() + ";" + w.l.getBlockZ())) {
					wws.remove(s);
				}
			}
			for (String s : lww.toArray(new String[0])) {
				if (s.contains(w.l.getBlockX() + ";" + w.l.getBlockY() + ";" + w.l.getBlockZ())) {
					lww.remove(s);
				}
			}
		}

		da.set("word_walls", wws);
		da.set("used_word_walls", lww);

		DragonShouts.ins.saveConfig();
		DragonShouts.ins.saveLearnedData();

		DragonShouts.ins.reloadConfig();
		DragonShouts.ins.reloadLearnedData();

		try {
			DragonShouts.ins.reloadAll();
		} catch (Exception e) {
		}
	}

}
