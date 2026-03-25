package se.itssimple.zombieshateslight.events;

import net.minecraft.world.entity.monster.zombie.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.data.Constants;

public class ZombieGoalEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Zombie zombie) {
            Constants.LOG.info("Adding goal to zombie {}", zombie);
            zombie.goalSelector.addGoal(ModCommon.GOAL_PRIORITY.getValue(), new BreakLightSourcesGoal(zombie));
        }
    }
}
