package se.itssimple.zombieshateslight.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.util.Reference;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BreakLightSourcesGoal extends Goal {
    protected final Zombie zombie;
    protected final Level level;

    private Path findingPath;

    private BlockPos targetLightPosition;
    private int breakingTime;
    private int breakingCooldown;

    private static final int MAX_BREAKING_TIME = 100;
    private static final int MAX_BREAKING_COOLDOWN = 200;
    private static final Integer LIGHT_SOURCE_RADIUS = ModCommon.LIGHT_SOURCE_RADIUS.getValue();

    private static final TagKey<Block> AFFECTED_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Reference.MOD_ID, "affected_blocks"));

    public BreakLightSourcesGoal(Zombie zombie) {
        this.zombie = zombie;
        this.level = zombie.level();

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.breakingCooldown = 0;
    }

    @Override
    public boolean canUse() {
        if (this.breakingCooldown > 0) {
            this.breakingCooldown--;
            return false;
        }

        this.targetLightPosition = findNearestLightSource();
        return this.targetLightPosition != null;
    }

    private BlockPos findNearestLightSource() {
        BlockPos zombiePosition = this.zombie.blockPosition();
        AtomicReference<BlockPos> foundPos = new AtomicReference<>();

        AtomicReference<Double> closestDistance = new AtomicReference<>(Double.MAX_VALUE);

        BlockPos.betweenClosedStream(
                zombiePosition.offset(-LIGHT_SOURCE_RADIUS, -LIGHT_SOURCE_RADIUS, -LIGHT_SOURCE_RADIUS),
                zombiePosition.offset(LIGHT_SOURCE_RADIUS, LIGHT_SOURCE_RADIUS, LIGHT_SOURCE_RADIUS))
                .filter(pos -> pos.distSqr(zombiePosition) <= LIGHT_SOURCE_RADIUS * LIGHT_SOURCE_RADIUS)
                .forEach(pos -> {
                    BlockState state = this.level.getBlockState(pos);

                    if (state.getLightEmission() != 0 && state.is(AFFECTED_BLOCKS)) {
                        double distanceSquared = this.zombie.distanceToSqr(Vec3.atCenterOf(pos));
                        var path = this.zombie.getNavigation().createPath(pos, 0);

                        if (distanceSquared < closestDistance.get() && path != null && path.canReach()) {
                            closestDistance.set(distanceSquared);
                            foundPos.set(pos.immutable());
                            this.findingPath = path;
                        }
                    }
                });

        return foundPos.get();
    }

    @Override
    public boolean canContinueToUse() {
        return this.breakingCooldown <= 0 &&
                this.targetLightPosition != null &&
                this.level.getBlockState(this.targetLightPosition).getLightEmission() != 0 &&
                !(this.zombie.distanceToSqr(Vec3.atCenterOf(this.targetLightPosition)) > (LIGHT_SOURCE_RADIUS * LIGHT_SOURCE_RADIUS + 4));
    }

    @Override
    public void start() {
        this.zombie.getNavigation().moveTo(this.findingPath, 1.0D);
        this.breakingTime = 0;
    }

    @Override
    public void stop() {
        this.targetLightPosition = null;
        this.breakingTime = 0;
        this.findingPath = null;
        this.zombie.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(this.targetLightPosition == null || this.findingPath == null) {
            return;
        }

        this.zombie.getLookControl().setLookAt(Vec3.atCenterOf(this.targetLightPosition));

        if(this.zombie.distanceToSqr(Vec3.atCenterOf(this.targetLightPosition)) < 2.0D) {
            this.breakingTime++;

            this.zombie.swing(InteractionHand.MAIN_HAND);

            if(this.breakingTime >= MAX_BREAKING_TIME) {
                this.level.destroyBlock(this.targetLightPosition, false, this.zombie);
                this.breakingCooldown = MAX_BREAKING_COOLDOWN;
                this.findingPath = null;
                this.targetLightPosition = null;
            }
        } else {
            this.zombie.getNavigation().moveTo(this.findingPath, 1.0D);
        }
    }
}
