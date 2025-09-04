package se.itssimple.zombieshateslight;

import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.zombieshateslight.data.Constants;
import se.itssimple.zombieshateslight.util.Reference;

public class ModCommon {
	public static ConfigHolder CONFIG;

	public static ConfigEntry<Integer> LIGHT_SOURCE_RADIUS;
	public static ConfigEntry<Integer> GOAL_PRIORITY;

	public static void init() {
		Constants.LOG.info("Loading {} (ID: {}), version {}", Reference.NAME, Reference.MOD_ID, Reference.VERSION);
		load();
	}

	private static void load() {
        //se.itssimple.obsidianweave.ModCommon.init();
		CONFIG = se.itssimple.obsidianweave.ModCommon.registerConfig(Reference.MOD_ID, builder -> {
			LIGHT_SOURCE_RADIUS = builder.define("light_source_radius", 10);
			GOAL_PRIORITY = builder.define("goal_priority", 3);
		});
	}
}