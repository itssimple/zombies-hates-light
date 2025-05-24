package se.itssimple.zombieshateslight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.data.Constants;
import se.itssimple.zombieshateslight.util.Reference;

import java.util.Map;

public class ModFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ModCommon.init();
	}
}