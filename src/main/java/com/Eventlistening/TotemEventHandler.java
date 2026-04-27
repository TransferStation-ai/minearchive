package com.Eventlistening;

import com.item.kayoko;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static com.item.kayoko.triggerTotemEffect;

public class TotemEventHandler {

    private static ServerLevel level;

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        // 只处理玩家死亡
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查是否应该触发不死图腾效果（这里可以添加条件判断）
        if (shouldTriggerTotem(player)) {
            //不死图腾的粒子
            for (int i = 0; i < 30; i++) {
                double ox = player.getRandom().nextGaussian() * 0.02;
                double oy = player.getRandom().nextGaussian() * 0.02;
                double oz = player.getRandom().nextGaussian() * 0.02;

                level.sendParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        1,
                        ox, oy, oz,
                        0.0
                );
            }
        }
            // 取消死亡事件
            event.setCanceled(true);
            // 清除所有负面效果
            player.removeAllEffects();
            // 触发不死图腾效果
        triggerTotemEffect(player);
            //用于播放不死图腾的声音
        Kayoko(player);
        }

    @SuppressWarnings("removal")
    public static void Kayoko(Player player) {
        Item kayokoItem;
        kayokoItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("kayoko"));
        if (kayokoItem == null || kayokoItem == Items.AIR) {
            return; // 物品未找到，不执行操作
        }
        //遍历玩家的背包里面能够更快地找到物品
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(kayokoItem)) {
                stack.shrink(1); // 减少一个物品
                if (stack.isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
                break; // 只移除一个，找到后立即退出循环
            }
        }
    }

    private static boolean shouldTriggerTotem(Player player) {
        boolean isPlayerHoldingKayoko;
            if (player == null) {
                return false;
            }
            // 检查主手和副手
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            return mainHand.getItem() instanceof kayoko || offHand.getItem() instanceof kayoko;
        }
}

