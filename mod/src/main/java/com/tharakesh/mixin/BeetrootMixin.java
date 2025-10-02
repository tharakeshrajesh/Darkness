package com.tharakesh.mixin;

import com.tharakesh.darkness.Black;
import com.tharakesh.darkness.DarknessMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.List;

@Mixin(Item.class)
public abstract class BeetrootMixin {
    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onEat(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.getItem() == Items.BEETROOT && !world.isClient && user instanceof ServerPlayerEntity player) {
            MinecraftServer server = world.getServer();
            if (server != null) {
                Random random = new Random();

                if (random.nextFloat() <= 0.03) {
                    List<Black> nearby = world.getEntitiesByType(DarknessMod.BLACK_ENTITY, user.getBoundingBox().expand(10), entity -> true);

                    if (!nearby.isEmpty()) {
                        Entity target = nearby.get(0);
                        target.kill(player.getWorld());
                    }
                } else {
                    double offsetX = (random.nextDouble() - 0.5) * 50;
                    double offsetZ = (random.nextDouble() - 0.5) * 50;

                    double spawnX = player.getX() + offsetX;
                    double spawnZ = player.getZ() + offsetZ;
                    BlockPos spawnPos = new BlockPos((int) spawnX, 0, (int) spawnZ);
                    int spawnY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, spawnPos.getX(), spawnPos.getZ());

                    MobEntity mob = new Black(DarknessMod.BLACK_ENTITY, world);

                    mob.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
                    world.spawnEntity(mob);
                    mob.setTarget(player);
                }
            }
        }
    }
}


