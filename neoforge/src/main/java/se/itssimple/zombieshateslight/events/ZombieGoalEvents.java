package se.itssimple.zombieshateslight.events;

import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.data.Constants;

/**
 * The NeoForge version of the event
 */
public class ZombieGoalEvents {
    /**
     * Default Constructor
     */
    public ZombieGoalEvents() {
    }

    /**
     * The event for when entities join the server
     * @param event -
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Monster monster) {
            if (!BreakLightSourcesGoal.isAffectedEntity(monster)) {
                return;
            }
            Constants.LOG.info("Adding goal to monster {}", monster);
            monster.goalSelector.addGoal(ModCommon.GOAL_PRIORITY.getValue(), new BreakLightSourcesGoal(monster));
        }
    }
}