package com.tharakesh.darkness;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BedBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class DarknessMod implements ModInitializer {
    private int tickCounter = 0;

    private final int cycleLength = 1200; //24000
    private final int dayLength = 300; //6000
    private final float transitionTime = 100; // 200

    public int currentCycle = 0;
    private short phase = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);
        UseBlockCallback.EVENT.register(this::onBlockInteract);
    }

    private void onTick(MinecraftServer server) {
        tickCounter++;
        currentCycle = tickCounter % cycleLength;

        if (0 <= currentCycle && currentCycle < (dayLength - transitionTime)) {
            phase = 0;
        } else if ((dayLength - transitionTime) <= currentCycle && currentCycle < dayLength) {
            phase = 1;
        } else if (dayLength <= currentCycle && currentCycle < (cycleLength - transitionTime)) {
            phase = 2;
        } else if ((cycleLength - transitionTime) <= currentCycle && currentCycle < cycleLength) {
            phase = 3;
        }

        if (phase == 0 && server.getOverworld().getTimeOfDay() != 0) {
            server.getOverworld().setTimeOfDay(0);
        } else if (phase == 1) {
            float progress = (float)(currentCycle - (dayLength - transitionTime)) / transitionTime;
            server.getOverworld().setTimeOfDay((long)(progress * 13000.0));
        } else if (phase == 2 && server.getOverworld().getTimeOfDay() != 13000) {
            server.getOverworld().setTimeOfDay(13000);
        } else if (phase == 3) {
            float progress = (float)(currentCycle - (cycleLength - transitionTime)) / transitionTime;
            server.getOverworld().setTimeOfDay((long)(13000.0 + progress * -13000.0));
        }

    }

    private ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        BlockPos blockpos = hit.getBlockPos();
        if (world.getBlockState(blockpos).getBlock() instanceof BedBlock) {
            world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
            world.createExplosion(null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 6.0f, true, World.ExplosionSourceType.BLOCK);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

}
