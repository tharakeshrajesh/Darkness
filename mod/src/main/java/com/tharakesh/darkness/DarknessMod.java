package com.tharakesh.darkness;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BedBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Heightmap;

public class DarknessMod implements ModInitializer {
    private int tickCounter = 0;

    private final int cycleLength = 24000;
    private final int dayLength = 6000;
    private final float transitionTime = 200;

    public int currentCycle = 0;
    private short phase = 0;

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(DarknessMod.BLACK_ENTITY, Black.createMobAttributes());
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);
        UseBlockCallback.EVENT.register(this::onBlockInteract);
        ServerWorldEvents.LOAD.register(this::onWorldLoad);
    }

    private void onWorldLoad(MinecraftServer server, ServerWorld world) {
        if (!world.isClient) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayerList().isEmpty()
                    ? null
                    : server.getPlayerManager().getPlayerList().get(0);

            if (player != null) {
                double spawnX = player.getX();
                double spawnZ = player.getZ();
                BlockPos pos = new BlockPos((int) spawnX, 0, (int) spawnZ);
                int spawnY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());

                Black mob = new Black(DarknessMod.BLACK_ENTITY, world);
                mob.refreshPositionAndAngles(spawnX, spawnY, spawnZ, world.random.nextFloat() * 360F, 0F);
                world.spawnEntity(mob);

                System.out.println("black is coming for you...");
            }
        }
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

    public static final EntityType<Black> BLACK_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("darkness", "black"),
            EntityType.Builder.create(Black::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.8f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("darkness", "black")))
    );

}
