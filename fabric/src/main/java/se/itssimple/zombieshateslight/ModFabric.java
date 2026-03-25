package se.itssimple.zombieshateslight;

import net.fabricmc.api.ModInitializer;


public class ModFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ModCommon.init();
	}
}