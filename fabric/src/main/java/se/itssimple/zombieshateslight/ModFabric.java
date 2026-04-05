package se.itssimple.zombieshateslight;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.monster.Monster;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.data.Constants;

/** The Fabric version for the mod */
public class ModFabric implements ModInitializer {

	/** Register the entity load event to fix the goal loading */
	public ModFabric()
	{
		ServerEntityEvents.ENTITY_LOAD.register((entity, server) -> {
			if (!server.isClientSide() && entity instanceof Monster monster) {
				if (!BreakLightSourcesGoal.isAffectedEntity(monster)) {
					return;
				}
				Constants.LOG.info("Adding goal to monster {}", monster);
				monster.goalSelector.addGoal(ModCommon.GOAL_PRIORITY.getValue(), new BreakLightSourcesGoal(monster));
			}
		});
	}

	/** Initializer of stuff */
	@Override
	public void onInitialize() {
		ModCommon.init();
	}
}