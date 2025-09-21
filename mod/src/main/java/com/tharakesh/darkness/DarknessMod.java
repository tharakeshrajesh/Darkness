package com.tharakesh.darkness;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class DarknessMod implements ModInitializer {
    private int tickCounter = 0;

    private final int cycleLength = 1200; //24000
    private final int dayLength = 300; //6000
    private final int nightLength = 900; // 18000
    private final float transitionTime = 100; // 200

    private int currentCycle = 0;
    private short phase = 0;

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);
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

}
