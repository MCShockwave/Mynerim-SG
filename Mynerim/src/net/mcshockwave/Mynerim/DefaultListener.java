package net.mcshockwave.Mynerim;

import net.mcshockwave.DragonShouts.DragonShouts;
import net.mcshockwave.DragonShouts.Shout;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.MCS.Utils.LocUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultListener implements Listener {

	public static HashMap<Sign, SGMap>	signs	= new HashMap<>();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		final Player p = event.getPlayer();
		final ItemStack it = event.getItemInHand();
		Block b = event.getBlock();

		if (b.getType() == Material.CHEST && ItemMetaUtils.hasCustomName(it)
				&& ItemMetaUtils.getItemName(it).startsWith("Loot:")) {
			final String type = ItemMetaUtils.getItemName(it).replace("Loot:", "");

			ItemMetaUtils.setItemName(it, "Chest");
			p.setItemInHand(it);
			Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
				public void run() {
					ItemMetaUtils.setItemName(it, "Loot:" + type);
					p.setItemInHand(it);
				}
			}, 1l);

			MSG.ins.addChest(b, type);
			p.sendMessage(ChatColor.GREEN + "Loot chest for map " + type + " placed!");
		}

		if (SGMap.getPlayerMap(p) != null || p.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}

		if (it.getType() == Material.CAKE && ItemMetaUtils.hasCustomName(it)
				&& ItemMetaUtils.getItemName(it).equalsIgnoreCase("§rSweet Roll")) {
			event.setCancelled(true);
			p.setItemInHand(null);

			p.getWorld().playSound(p.getLocation(), Sound.EAT, 3, 1);
			p.getWorld().playEffect(p.getEyeLocation(), Effect.STEP_SOUND, Material.CAKE_BLOCK);

			int food = p.getFoodLevel();
			food += 12;
			float sat = p.getSaturation();
			sat += 6;
			if (food > 20) {
				int addSat = food - 20;
				food = 20;
				sat += addSat;
				if (sat > 20) {
					sat = 20;
				}
			}
			p.setFoodLevel(food);
			p.setSaturation(sat);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();

		if (MSG.mapChests.containsKey(b)) {
			p.sendMessage(ChatColor.RED + "Loot chest for map " + MSG.mapChests.get(b) + " destroyed!");
			MSG.ins.delChest(b, MSG.mapChests.get(b));
		}

		if (SGMap.getPlayerMap(p) != null || p.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (SGMap.getPlayerMap(p) != null
				&& (!SGMap.getPlayerMap(p).started || !SGMap.getPlayerMap(p).canMove && !SGMap.isASpec(p))) {
			if (!LocUtils.isSame(event.getFrom(), event.getTo())) {
				event.setTo(SGMap.getPlayerMap(p).getPlayerSpawn(p));
			}
		}

		if (SGMap.isASpec(p)) {
			p.setFoodLevel(20);
			p.setSaturation(2);

			if (SGMap.getPlayerMap(p) == null && event.getTo().getBlockY() <= 62
					&& p.getGameMode() != GameMode.CREATIVE) {
				event.setTo(MSG.spawn);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		if (SGMap.getPlayerMap(p) != null && SGMap.getPlayerMap(p).started) {
			SGMap m = SGMap.getPlayerMap(p);

			if (m.dead.contains(p.getName()))
				return;
			event.setDeathMessage("");
			m.onDeath(p);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();

		if (SGMap.getPlayerMap(p) != null) {
			SGMap.getPlayerMap(p).items.add(event.getItemDrop());
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ee = event.getEntity();
		Entity de = event.getDamager();

		if (ee instanceof Player
				&& (de instanceof Player || de instanceof Arrow && ((Arrow) de).getShooter() instanceof Player)) {
			Player p = (Player) ee;
			Player d = de instanceof Arrow ? (Player) ((Projectile) de).getShooter() : (Player) de;
			if (de instanceof Arrow) {
				d = (Player) ((Arrow) de).getShooter();
			}

			SGMap.resetDurability(p);
			SGMap.resetDurability(d);

			SGMap pm = SGMap.getPlayerMap(p);
			SGMap dm = SGMap.getPlayerMap(d);

			if (pm != null && dm != null && pm == dm && !pm.grace) {
				if (pm.dead.contains(p.getName()) || dm.dead.contains(d.getName())) {
					event.setCancelled(true);
				}
			} else
				event.setCancelled(true);
		}

		// spec blocking
		if (ee instanceof Player && de instanceof Projectile) {
			Projectile arrow = (Projectile) de;
			Player d = (Player) arrow.getShooter();
			Player p = (Player) ee;

			Vector velocity = arrow.getVelocity();
			Class<? extends Projectile> pc = arrow.getClass();

			if (SGMap.isASpec(p)) {
				p.teleport(p.getLocation().add(0, 5, 0));
				MCShockwave.send(p, "You are in the way of a(n) %s!",
						arrow.getType().name().toLowerCase().replace('_', ' '));

				Projectile newArrow = d.launchProjectile(pc);
				newArrow.setShooter(d);
				newArrow.setVelocity(velocity);
				newArrow.setBounce(false);

				event.setCancelled(true);
				arrow.remove();
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity he = event.getWhoClicked();
		if (he instanceof Player) {
			Player p = (Player) he;

			SGMap m = SGMap.getPlayerMap(p);
			if (m != null && m.dead.contains(p.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();

		SGMap m = SGMap.getPlayerMap(p);
		if (m != null && m.dead.contains(p.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();

		if (e instanceof Player) {
			Player p = (Player) e;

			SGMap m = SGMap.getPlayerMap(p);
			if (m != null && m.dead.contains(p.getName()) || m == null) {
				event.setCancelled(true);
			}
		}
	}

	public void onQuit(Player p) {
		SGMap m = SGMap.getPlayerMap(p);

		if (m != null) {
			m.removePlayer(p, true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		onQuit(event.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		onQuit(event.getPlayer());
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String cmd = event.getMessage();
		String label = cmd.split(" ")[0];

		if (SGMap.isASpec(p)
				&& (label.equalsIgnoreCase("ds") || label.equalsIgnoreCase("shout") || label.equalsIgnoreCase("dshout"))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSignUpdate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[Map]")) {
			final SGMap sg = SGMap.valueOf(event.getLine(1));

			if (sg != null) {
				final Sign s = (Sign) event.getBlock().getState();
				Bukkit.getScheduler().runTaskLater(MSG.ins, new Runnable() {
					public void run() {
						setSignText(s, sg);
					}
				}, 1l);
			}
		}
	}

	public static void setSignText(Sign s, SGMap m) {
		if (!m.started) {
			s.setLine(0, "§2§nJoin Map");
			s.setLine(1, "§a" + m.name);
			s.setLine(2, "§b" + m.players.size() + " / " + m.spawns.length);
			s.setLine(3, "§eClick to join");
		} else {
			if (!m.deathmatch) {
				s.setLine(0, "§4§nStarted");
				s.setLine(1, "§a" + m.name);
				s.setLine(2, "§b" + m.getAlive().size() + " / " + m.players.size());
				s.setLine(3, "§cStarted");
			} else {
				s.setLine(0, "§4§nStarted");
				s.setLine(1, "§a" + m.name);
				s.setLine(2, "§b" + m.getAlive().size() + " / " + m.players.size());
				s.setLine(3, "§3Deathmatch");
			}
		}
		s.update();
		signs.remove(s);
		signs.put(s, m);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();
		Action a = event.getAction();
		ItemStack it = p.getItemInHand();

		if (a == Action.RIGHT_CLICK_BLOCK && SGMap.getPlayerMap(p) == null && b != null && b.getState() instanceof Sign) {
			Sign s = (Sign) b.getState();
			for (SGMap sg : SGMap.values()) {
				if (s.getLine(1).equalsIgnoreCase("§a" + sg.name)) {
					if (sg.started) {
						p.performCommand("join " + sg.name());
					} else {
						sg.addSpec(p);
					}
					if (!signs.containsKey(s)) {
						signs.put(s, sg);
					}
					setSignText(s, sg);
					break;
				}
			}
		}

		if (b != null && SGMap.isASpec(p) && b.getType() == Material.BOOKSHELF
				&& DragonShouts.word_walls.containsKey(b)) {
			event.setCancelled(true);
		}

		if (SGMap.isASpec(p) && SGMap.getPlayerMap(p) != null && a == Action.LEFT_CLICK_AIR) {
			SGMap sgm = SGMap.getPlayerMap(p);
			final ArrayList<Player> al = sgm.getAlive();
			ItemMenu m = new ItemMenu("Alive Players", (al.size() + 8) / 9 * 9);

			int i = 0;
			for (Player p2 : al) {
				Button bu = new Button(true, Material.SKULL_ITEM, 1, 3, p2.getName(), "Click to teleport");
				bu.onClick = new ButtonRunnable() {
					public void run(Player c, InventoryClickEvent event) {
						Player tp = Bukkit.getPlayer(ChatColor.stripColor(ItemMetaUtils.getItemName(event
								.getCurrentItem())));
						c.teleport(tp);
						MCShockwave.send(ChatColor.GREEN, c, "Teleported to %s", tp.getName());
					}
				};

				m.addButton(bu, i);
				i++;
			}

			m.open(p);
		}

		if (a == Action.RIGHT_CLICK_BLOCK && it.getType() == Material.BLAZE_ROD && b.getType() == Material.CHEST) {
			event.setCancelled(true);
			if (MSG.mapChests.containsKey(b)) {
				MCShockwave.send(ChatColor.GREEN, p, "Chest is loot for map %s", MSG.mapChests.get(b));
			} else
				MCShockwave.send(ChatColor.RED, p, "Chest is %s a loot chest", "not");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();

		p.teleport(MSG.spawn);
		MSG.clearInv(p);
	}

	public void updateInventory(ItemMenu im, PlayerInventory in) {
		Player p = (Player) in.getHolder();

		im.addButton(new Button(false, Material.POTION, (int) p.getHealth(), 8261, "Health"), 0);
		im.addButton(new Button(false, Material.COOKED_BEEF, p.getFoodLevel(), 0, "Food"), 1);
		im.addButton(new Button(false, Material.GOLDEN_APPLE, (int) p.getSaturation(), 0, "Saturation"), 2);

		ArrayList<String> known = getKnownShouts(p);
		im.addButton(new Button(false, Material.DRAGON_EGG, 1, 0, "Known Shouts", known.toArray(new String[0])), 8);

		for (int i = 0; i < 9; i++) { // hotbar
			im.i.setItem(i + 45, in.getItem(i));
		}

		for (int i = 18; i < 45; i++) { // inventory
			im.i.setItem(i, in.getItem(i - 9));
		}

		for (int i = 0; i < 4; i++) {
			im.i.setItem(i + 9, in.getArmorContents()[i]);
		}
	}

	public ArrayList<String> getKnownShouts(Player p) {
		ArrayList<String> ret = new ArrayList<>();
		for (Shout s : Shout.values()) {
			if (s.hasLearnedShout(p, 0)) {
				String add = "§7" + s.name + " - §a";
				if (s.hasLearnedShout(p, 1)) {
					add += s.w1;
				}
				if (s.hasLearnedShout(p, 2)) {
					add += " " + s.w2;
				}
				if (s.hasLearnedShout(p, 3)) {
					add += " " + s.w3;
				}
				ret.add(add);
			}
		}
		return ret;
	}

	HashMap<Player, BukkitTask>	updateTask	= new HashMap<>();

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity ce = event.getRightClicked();

		if (ce instanceof Player) {
			Player c = (Player) ce;

			if (SGMap.getPlayerMap(p) != null && SGMap.isASpec(p) && SGMap.getPlayerMap(c) != null && !SGMap.isASpec(c)) {
				final PlayerInventory pi = c.getInventory();

				final ItemMenu im = new ItemMenu("Inventory of " + c.getName(), 54);

				updateInventory(im, pi);

				im.open(p);

				updateTask.put(p, new BukkitRunnable() {
					public void run() {
						updateInventory(im, pi);
					}
				}.runTaskTimer(MSG.ins, 0, 20));
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();

			if (updateTask.containsKey(p)) {
				updateTask.get(p).cancel();
				updateTask.remove(p);
			}
		}
	}
}
