package net.mcshockwave.Mynerim.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.Spells.Spell;

import org.apache.commons.lang.WordUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum ItemGen {

	// Swords
	Warhammer(
		Material.STONE_SWORD,
		55,
		Type.Sword,
		Enchantment.KNOCKBACK,
		2),
	Longsword(
		Material.IRON_SWORD,
		50,
		Type.Sword,
		Enchantment.KNOCKBACK,
		1),
	Iron_Sword(
		Material.IRON_SWORD,
		50,
		Type.Sword),
	Steel_Sword(
		Material.IRON_SWORD,
		45,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		1),
	Elven_Sword(
		Material.GOLD_SWORD,
		40,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		3),
	Glass_Sword(
		Material.DIAMOND_SWORD,
		35,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		1),
	Dwarven_Sword(
		Material.GOLD_SWORD,
		30,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		4),
	Orcish_Sword(
		Material.GOLD_SWORD,
		25,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		5),
	Ebony_Sword(
		Material.DIAMOND_SWORD,
		20,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		3),
	Daedric_Sword(
		Material.DIAMOND_SWORD,
		15,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		4),
	Dragonbone_Sword(
		Material.DIAMOND_SWORD,
		10,
		Type.Sword,
		Enchantment.DAMAGE_ALL,
		5),

	// Armor
	Leather(
		ArmorMat.LEATHER,
		60),
	Iron(
		ArmorMat.IRON,
		55),
	Steel(
		ArmorMat.IRON,
		50,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		1),
	Steel_Plate(
		ArmorMat.CHAINMAIL,
		45,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		3),
	Scaled(
		ArmorMat.CHAINMAIL,
		45,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		3),
	Glass(
		ArmorMat.DIAMOND,
		40),
	Elven(
		ArmorMat.GOLD,
		35,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		3),
	Dwarven(
		ArmorMat.GOLD,
		30,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		4),
	Orcish(
		ArmorMat.GOLD,
		30,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		4),
	Ebony(
		ArmorMat.DIAMOND,
		25,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		2),
	Daedric(
		ArmorMat.DIAMOND,
		20,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		3),
	Dragonplate(
		ArmorMat.DIAMOND,
		15,
		Enchantment.PROTECTION_ENVIRONMENTAL,
		4),

	// Bows
	Long_Bow(
		Material.BOW,
		50,
		Type.Bow),
	Hunting_Bow(
		Material.BOW,
		45,
		Type.Bow,
		Enchantment.ARROW_KNOCKBACK,
		1),
	Ancient_Nord_Bow(
		Material.BOW,
		40,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		1),
	Imperial_Bow(
		Material.BOW,
		35,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		1,
		Enchantment.ARROW_KNOCKBACK,
		1),
	Orcish_Bow(
		Material.BOW,
		30,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		2),
	Dwarven_Bow(
		Material.BOW,
		25,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		2,
		Enchantment.ARROW_KNOCKBACK,
		1),
	Elven_Bow(
		Material.BOW,
		20,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		3),
	Glass_Bow(
		Material.BOW,
		15,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		4),
	Ebony_Bow(
		Material.BOW,
		10,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		4,
		Enchantment.ARROW_KNOCKBACK,
		1),
	Daedric_Bow(
		Material.BOW,
		5,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		5),
	Dragonbone_Bow(
		Material.BOW,
		1,
		Type.Bow,
		Enchantment.ARROW_DAMAGE,
		5,
		Enchantment.ARROW_KNOCKBACK,
		1),

	// FOOD
	Sweet_Roll(
		Material.CAKE,
		1,
		Type.Food),
	Carrots(
		Material.CARROT_ITEM,
		1,
		Type.Food),
	Baked_Potato(
		Material.BAKED_POTATO,
		1,
		Type.Food),
	Loaf_of_Bread(
		Material.BREAD,
		1,
		Type.Food),
	Apple(
		Material.APPLE,
		1,
		Type.Food),
	Chicken_Breast(
		Material.COOKED_CHICKEN,
		1,
		Type.Food),
	Salmon_Meat(
		Material.COOKED_FISH,
		1,
		1,
		Type.Food),
	Horker_Meat(
		Material.COOKED_BEEF,
		1,
		Type.Food),
	Horse_Meat(
		Material.COOKED_BEEF,
		1,
		Type.Food),
	Leg_of_Goat(
		Material.COOKED_CHICKEN,
		1,
		Type.Food),
	Pheasant_Breast(
		Material.COOKED_CHICKEN,
		1,
		Type.Food),
	Raw_Beef(
		Material.RAW_BEEF,
		1,
		Type.Food),
	Raw_Rabbit_Leg(
		Material.RAW_CHICKEN,
		1,
		Type.Food),
	Venison(
		Material.COOKED_BEEF,
		1,
		Type.Food),
	Apple_Cabbage_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Beef_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Cabbage_Potato_Soup(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Horker_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Tomato_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Vegetable_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Venison_Stew(
		Material.MUSHROOM_SOUP,
		1,
		Type.Food),
	Potato(
		Material.POTATO_ITEM,
		1,
		Type.Food),
	Apple_Pie(
		Material.PUMPKIN_PIE,
		1,
		Type.Food),
	Cheese_Wheel(
		Material.PUMPKIN_PIE,
		1,
		Type.Food),

	// drinks
	Ale(
		Material.POTION,
		8193,
		1,
		Type.Food),
	Alto_Wine(
		Material.POTION,
		8225,
		1,
		Type.Food),
	Black_Briar_Mead(
		Material.POTION,
		8257,
		1,
		Type.Food),
	Honningbrew_Mead(
		Material.POTION,
		8257,
		1,
		Type.Food),
	Mead_with_Juniper_Berry(
		Material.POTION,
		8226,
		1,
		Type.Food),

	// Other,
	Arrow(
		Material.ARROW,
		5,
		Type.Other),
	// Spells
	Flames(
		Spell.Flames.item,
		100,
		Type.Spell),
	Healing(
		Spell.Healing.item,
		90,
		Type.Spell),
	Lesser_Ward(
		Spell.Lesser_Ward.item,
		100,
		Type.Spell),
	Lightning_Bolt(
		Spell.Lightning_Bolt.item,
		90,
		Type.Spell),
	Frost_Rune(
		Spell.Frost_Rune.item,
		85,
		Type.Spell),
	Ice_Storm(
		Spell.Ice_Storm.item,
		80,
		Type.Spell),
	Lightning_Cloak(
		Spell.Lightning_Cloak.item,
		80,
		Type.Spell),
	Incinerate(
		Spell.Incinerate.item,
		50,
		Type.Spell),
	Wall_of_Frost(
		Spell.Wall_of_Frost.item,
		50,
		Type.Spell),
	Blizzard(
		Spell.Blizzard.item,
		5,
		Type.Spell),
	Steadfast_Ward(
		Spell.Steadfast_Ward.item,
		80,
		Type.Spell),
	Greater_Ward(
		Spell.Greater_Ward.item,
		75,
		Type.Spell),
	Guardian_Circle(
		Spell.Guardian_Circle.item,
		10,
		Type.Spell);

	private Material						type		= null;
	private short							data		= 0;
	private ArmorMat						amat		= null;
	public int								weight;
	public Type								t;
	public String							name;
	public ItemStack						exact		= null;

	public HashMap<Enchantment, Integer>	enchants	= new HashMap<>();

	private ItemGen(Material type, int weight, Type t, Object... enchants) {
		this.type = type;
		init(weight, t, enchants);
	}

	private ItemGen(ArmorMat atype, int weight, Object... enchants) {
		this.amat = atype;
		init(weight, Type.Armor, enchants);
	}

	private ItemGen(Material type, int data, int weight, Type t, Object... enchants) {
		this.type = type;
		this.data = (short) data;
		init(weight, t, enchants);
	}

	private ItemGen(ArmorMat atype, int data, int weight, Object... enchants) {
		this.amat = atype;
		this.data = (short) data;
		init(weight, Type.Armor, enchants);
	}

	private ItemGen(ItemStack it, int weight, Type t) {
		exact = it;
		this.type = it.getType();
		this.data = it.getDurability();
		init(weight, t, new Object[0]);
	}

	private void init(int weight, Type t, Object... enchants) {
		this.weight = weight;
		this.name = name().replace('_', ' ');
		this.t = t;
		for (int i = 0; i < enchants.length; i += 2) {
			Object o = enchants[i];
			Object o2 = enchants[i + 1];
			if (o instanceof Enchantment && o2 instanceof Integer) {
				this.enchants.put((Enchantment) o, (Integer) o2);
			}
		}
	}

	public ItemStack getItem(ArmorType at) {
		Random rand = new Random();

		Material mat = t != Type.Armor ? type : getType(at, amat);
		int amount = t != Type.Spell ? rand.nextInt(((int) mat.getMaxStackSize() / 10) + 1) + 1 : 1;

		if (exact != null) {
			return exact;
		}
		
		ItemStack it = new ItemStack(mat, amount, data);
		if (t != Type.Other) {
			ItemMetaUtils.setItemName(it, ChatColor.RESET + getName(at));
		}

		for (Map.Entry<Enchantment, Integer> ent : enchants.entrySet()) {
			it.addEnchantment(ent.getKey(), ent.getValue());
		}

		return it;
	}

	public String getName(ArmorType at) {
		if (amat != null) {
			return name + " " + at.name;
		} else if (type != null) {
			return name;
		}
		throw new IllegalArgumentException();
	}

	public static ItemGen getRandom(Type t) {
		List<ItemGen> items = t == null ? Arrays.asList(values()) : getAllOfType(t);
		double totalWeight = 0.0d;
		for (ItemGen i : items) {
			totalWeight += i.weight;
		}
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < items.size(); ++i) {
			random -= items.get(i).weight;
			if (random <= 0.0d) {
				randomIndex = i;
				break;
			}
		}
		return items.get(randomIndex);
	}

	public static List<ItemGen> getAllOfType(Type t) {
		List<ItemGen> its = new ArrayList<>();
		for (ItemGen ig : ItemGen.values()) {
			if (ig.t == t) {
				its.add(ig);
			}
		}
		return its;
	}

	public static enum Type {
		Sword,
		Armor,
		Bow,
		Food,
		Other,
		Spell;
	}

	public static enum ArmorType {
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS;

		public String	name;

		private ArmorType() {
			name = WordUtils.capitalizeFully(name());
		}
	}

	public static enum ArmorMat {
		LEATHER,
		CHAINMAIL,
		IRON,
		DIAMOND,
		GOLD;
	}

	public Material getType(ArmorType at, ArmorMat am) {
		return Material.valueOf(am.name() + "_" + at.name());
	}

}
