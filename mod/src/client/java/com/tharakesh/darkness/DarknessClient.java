package com.tharakesh.darkness;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ZombieEntityRenderer;

public class DarknessClient implements ClientModInitializer {
    List<SoundEvent> jumpscareSounds = new ArrayList<>();

    private static final SoundEvent AUGH = registerSoundEvent("augh");
    private static final SoundEvent AH = registerSoundEvent("ah");
    private static final SoundEvent FART = registerSoundEvent("fart");
    private static final SoundEvent FNAF = registerSoundEvent("fnaf");
    private static final SoundEvent MILK = registerSoundEvent("milk");
    private static final SoundEvent NUMBERFIFTEEN = registerSoundEvent("numberfifteen");
    private static final SoundEvent RINGTONE = registerSoundEvent("ringtone");
    private static final SoundEvent SURPRISE = registerSoundEvent("surprise");
    private static final SoundEvent THX = registerSoundEvent("thx");
    private static final SoundEvent VINEBOOM = registerSoundEvent("vineboom");
    private static final SoundEvent WILHELM = registerSoundEvent("wilhelm");

    private int tickCounter = 0;
    Random random = new Random();
    private int interval = 1200 + random.nextInt(6000);

    @Override
    public void onInitializeClient() {
        jumpscareSounds.add(AUGH);
        jumpscareSounds.add(AH);
        jumpscareSounds.add(FART);
        jumpscareSounds.add(FNAF);
        jumpscareSounds.add(MILK);
        jumpscareSounds.add(NUMBERFIFTEEN);
        jumpscareSounds.add(RINGTONE);
        jumpscareSounds.add(SURPRISE);
        jumpscareSounds.add(THX);
        jumpscareSounds.add(VINEBOOM);
        jumpscareSounds.add(WILHELM);

        EntityRendererRegistry.register(DarknessMod.BLACK_ENTITY, context ->
                new ZombieEntityRenderer(context)
        );

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
            tickCounter = 0;
        });

    }

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of("darkness", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private void onClientTick(net.minecraft.client.MinecraftClient client) {
        tickCounter++;
        if (tickCounter == interval) {
            if (client.player != null) {
                SoundEvent randomSound = jumpscareSounds.get(random.nextInt(jumpscareSounds.size()));
                client.player.playSound(randomSound, 10.0F, 1.0F);
            }
            tickCounter = 0;
            interval = 6000 + random.nextInt(201) - 100;
        }
    }
}