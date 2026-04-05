package se.itssimple.zombieshateslight.events;

import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.data.Constants;
import se.itssimple.zombieshateslight.util.Reference;

/**
 * The event handler that adds the goals to affected entities
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZombieGoalEvents {
    /** Default constructor */
    public ZombieGoalEvents() {}

    /**
     * Handle entity join and register goals for applicable entities
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