package se.itssimple.zombieshateslight.fabric.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.ai.BreakLightSourcesGoal;

@Mixin(Zombie.class)
public abstract class ZombieGoalMixin extends Mob {

    protected ZombieGoalMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void customGoals(CallbackInfo ci) {
        var thisZombie = (Zombie) (Object) this;
        this.goalSelector.addGoal(ModCommon.GOAL_PRIORITY.getValue(), new BreakLightSourcesGoal(thisZombie));
    }
}
