package se.itssimple.zombieshateslight.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import se.itssimple.zombieshateslight.ModCommon;
import se.itssimple.zombieshateslight.util.Reference;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A goal that causes affected monsters to seek out and break nearby light sources.
 * <p>
 * The monster will search for blocks that emit light and are included in the
 * {@code affected_blocks} tag within a configurable radius. Once a target is found,
 * the monster will path to it and break the block after a short duration.
 * <p>
 * Which monster types are affected is determined by the {@code affected_entities} entity type tag.
 */
public class BreakLightSourcesGoal extends Goal {
    /** The monster entity performing this goal. */
    protected final Monster monster;
    /** Reference to the current level. */
    protected final Level level;

    /** The path being followed to reach the target light source. */
    private Path findingPath;

    /** The block position of the current target light source. */
    private BlockPos targetLightPosition;
    /** Number of ticks the monster has been breaking the current target. */
    private int breakingTime;
    /** Cooldown ticks remaining before the goal can be used again. */
    private int breakingCooldown;

    /** Maximum ticks required to break a light source block. */
    private static final int MAX_BREAKING_TIME = 100;
    /** Maximum cooldown ticks after breaking a light source. */
    private static final int MAX_BREAKING_COOLDOWN = 200;
    /** Search radius in blocks for finding light sources. */
    private static final Integer LIGHT_SOURCE_RADIUS = ModCommon.LIGHT_SOURCE_RADIUS.getValue();

    /** Tag defining which blocks are considered valid light sources to break. */
    private static final TagKey<Block> AFFECTED_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "affected_blocks"));
    /** Tag defining which entity types are affected by this goal. */
    private static final TagKey<EntityType<?>> AFFECTED_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "affected_entities"));

    /**
     * Checks whether the given mob type is affected by this goal.
     *
     * @param mob the mob to check
     * @return {@code true} if the mob's entity type is in the {@code affected_entities} tag
     */
    public static boolean isAffectedEntity(Mob mob) {
        return mob.getType().is(AFFECTED_ENTITIES);
    }

    /**
     * Creates a new BreakLightSourcesGoal for the given monster.
     *
     * @param monster the monster that will perform this goal
     */
    public BreakLightSourcesGoal(Monster monster) {
        this.monster = monster;
        this.level = this.monster.level();

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.breakingCooldown = 0;
    }

    /**
     * Determines if the goal can be used. Decrements the breaking cooldown and
     * searches for the nearest light source if the cooldown has expired.
     *
     * @return {@code true} if a valid light source target was found and cooldown is expired
     */
    @Override
    public boolean canUse() {
        if (this.breakingCooldown > 0) {
            this.breakingCooldown--;
            return false;
        }

        this.targetLightPosition = findNearestLightSource();
        return this.targetLightPosition != null;
    }

    /**
     * Searches for the nearest light source block within the configured radius.
     * <p>
     * Iterates through all block positions in a spherical area around the monster,
     * checking for blocks that emit light and are included in the {@code affected_blocks} tag.
     * Returns the closest reachable light source.
     *
     * @return the position of the nearest valid light source, or {@code null} if none found
     */
    private BlockPos findNearestLightSource() {
        BlockPos zombiePosition = this.monster.blockPosition();
        AtomicReference<BlockPos> foundPos = new AtomicReference<>();

        AtomicReference<Double> closestDistance = new AtomicReference<>(Double.MAX_VALUE);

        BlockPos.betweenClosedStream(
                        zombiePosition.offset(-LIGHT_SOURCE_RADIUS, -LIGHT_SOURCE_RADIUS, -LIGHT_SOURCE_RADIUS),
                        zombiePosition.offset(LIGHT_SOURCE_RADIUS, LIGHT_SOURCE_RADIUS, LIGHT_SOURCE_RADIUS))
                .filter(pos -> pos.distSqr(zombiePosition) <= LIGHT_SOURCE_RADIUS * LIGHT_SOURCE_RADIUS)
                .forEach(pos -> {
                    BlockState state = this.level.getBlockState(pos);

                    if (state.getLightEmission() != 0 && state.is(AFFECTED_BLOCKS)) {
                        double distanceSquared = this.monster.distanceToSqr(Vec3.atCenterOf(pos));
                        var path = this.monster.getNavigation().createPath(pos, 0);

                        if (distanceSquared < closestDistance.get() && path != null && path.canReach()) {
                            closestDistance.set(distanceSquared);
                            foundPos.set(pos.immutable());
                            this.findingPath = path;
                        }
                    }
                });

        return foundPos.get();
    }

    /**
     * Determines if the goal should continue to be used.
     *
     * @return {@code true} if the target is still valid, emitting light, and within range
     */
    @Override
    public boolean canContinueToUse() {
        return this.breakingCooldown <= 0 &&
                this.targetLightPosition != null &&
                this.level.getBlockState(this.targetLightPosition).getLightEmission() != 0 &&
                !(this.monster.distanceToSqr(Vec3.atCenterOf(this.targetLightPosition)) > (LIGHT_SOURCE_RADIUS * LIGHT_SOURCE_RADIUS + 4));
    }

    /**
     * Called when the goal starts. Begins pathfinding to the target light source.
     */
    @Override
    public void start() {
        this.monster.getNavigation().moveTo(this.findingPath, 1.0D);
        this.breakingTime = 0;
    }

    /**
     * Called when the goal stops. Resets all goal state.
     */
    @Override
    public void stop() {
        this.targetLightPosition = null;
        this.breakingTime = 0;
        this.findingPath = null;
        this.monster.getNavigation().stop();
    }

    /**
     * Called every tick while the goal is active. Handles pathfinding to the target
     * and breaking the block when in range.
     */
    @Override
    public void tick() {
        if(this.targetLightPosition == null || this.findingPath == null) {
            return;
        }

        this.monster.getLookControl().setLookAt(Vec3.atCenterOf(this.targetLightPosition));

        if(this.monster.distanceToSqr(Vec3.atCenterOf(this.targetLightPosition)) < 2.0D) {
            this.breakingTime++;

            this.monster.swing(InteractionHand.MAIN_HAND);

            if(this.breakingTime >= MAX_BREAKING_TIME) {
                this.level.destroyBlock(this.targetLightPosition, false, this.monster);
                this.breakingCooldown = MAX_BREAKING_COOLDOWN;
                this.findingPath = null;
                this.targetLightPosition = null;
            }
        } else {
            this.monster.getNavigation().moveTo(this.findingPath, 1.0D);
        }
    }
}