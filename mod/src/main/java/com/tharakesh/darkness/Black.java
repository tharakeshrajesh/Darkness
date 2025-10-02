package com.tharakesh.darkness;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Black extends ZombieEntity {
    public Black(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.MOVEMENT_SPEED, 0.5);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    protected boolean isAffectedByDaylight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 1.5);
        if (nearestPlayer != null && !this.getWorld().isClient) {
            nearestPlayer.setHealth(0.0F);
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.clear(goal -> true);
        this.targetSelector.clear(goal -> true);
        this.goalSelector.add(0, new AlwaysFollowPlayerGoal(this));
    }

    private static class AlwaysFollowPlayerGoal extends Goal {
        private final Black entity;

        public AlwaysFollowPlayerGoal(Black entity) {
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(entity, -1.0);
            if (nearestPlayer != null) {
                entity.getNavigation().startMovingTo(nearestPlayer, 1.0);
            }
        }
    }

}