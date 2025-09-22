package com.tharakesh.darkness;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;


public class DarknessClient implements ClientModInitializer {
	public static final SoundEvent AUGH = registerSoundEvent("augh");	

	@Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register(this::onBlockInteract);
    }

	private static SoundEvent registerSoundEvent(String name) {
		Identifier id = Identifier.of("darkness", name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

    private ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();

        if (world.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK) {
			System.out.println("Diamond block interacted");
            player.playSound(AUGH, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}