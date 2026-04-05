package se.itssimple.zombieshateslight;

import se.itssimple.obsidianweave.data.ConfigEntry;
import se.itssimple.obsidianweave.data.ConfigHolder;
import se.itssimple.zombieshateslight.data.Constants;
import se.itssimple.zombieshateslight.util.Reference;

/**
 * ModCommon contains all the configuration info for the mod
 */
public class ModCommon {
	/** Default constructor */
	public ModCommon() {}

	/** The config holder where the mod accesses info */
	public static ConfigHolder CONFIG;

	/** The entry for the Light Source Radius */
	public static ConfigEntry<Integer> LIGHT_SOURCE_RADIUS;
	/** The entry for the goal priority */
	public static ConfigEntry<Integer> GOAL_PRIORITY;

	/**
	 * The init method for the ModCommon class, telling us which mod and version we've loaded
	 */
	public static void init() {
		Constants.LOG.info("Loading {} (ID: {}), version {}", Reference.NAME, Reference.MOD_ID, Reference.VERSION);
		load();
	}

	/**
	 * This is where we load the config.
	 */
	private static void load() {
		CONFIG = se.itssimple.obsidianweave.ModCommon.registerConfig(Reference.MOD_ID, builder -> {
			LIGHT_SOURCE_RADIUS = builder.define("light_source_radius", 10);
			GOAL_PRIORITY = builder.define("goal_priority", 3);
		});
	}
}