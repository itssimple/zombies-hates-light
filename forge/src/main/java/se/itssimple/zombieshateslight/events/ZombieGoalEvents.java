package se.itssimple.zombieshateslight.events;

import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;
import se.itssimple.zombieshateslight.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZombieGoalEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Zombie zombie) {
            zombie.goalSelector.addGoal(ModCommon.GOAL_PRIORITY.getValue(), new BreakLightSourcesGoal(zombie));
        }
    }
}
