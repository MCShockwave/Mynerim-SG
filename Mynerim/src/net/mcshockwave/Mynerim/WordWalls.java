package net.mcshockwave.Mynerim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public enum WordWalls {

	Blackrun_1(
		12,
		71,
		61),
	Blackrun_2(
		-75,
		124,
		243),
	Blackrun_3(
		-85,
		64,
		-83),
	Blackrun_4(
		198,
		45,
		42),
	Blackrun_5(
		29,
		59,
		143);

	public Location	l;

	WordWalls(int x, int y, int z) {
		l = new Location(MSG.dW(), x, y, z);
	}

	public static List<WordWalls> getWalls(SGMap m) {
		ArrayList<WordWalls> ret = new ArrayList<>();
		for (WordWalls w : values()) {
			if (w.name().startsWith(m.name())) {
				ret.add(w);
			}
		}
		return ret;
	}

}
