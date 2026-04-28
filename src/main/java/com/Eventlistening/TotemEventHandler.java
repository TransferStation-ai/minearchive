package com.Eventlistening;

import com.item.kayoko;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import static com.item.kayoko.triggerTotemEffect;
import static com.minearchive.minearchive.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemEventHandler {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // 关键修复：只有持有物品时才触发
        if (!shouldTriggerTotem(player)) return;
        // 新增：检查攻击者是否持有 hina 物品
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            Item hinaItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "hina"));
            if (hinaItem != null && hinaItem != Items.AIR) {
                ItemStack mainHand = livingAttacker.getMainHandItem();
                ItemStack offHand = livingAttacker.getOffhandItem();
                if (mainHand.is(hinaItem) || offHand.is(hinaItem)) {
                    // 50% 概率导致死亡（不触发 totem）
                    if (livingAttacker.getRandom().nextFloat() < 0.5f) {
                        return; // 直接返回，不取消死亡事件
                    }
                }
            }
        }
        Level level = player.level();
        // 关键：取消死亡状态
        player.deathTime = 0;
        player.setHealth(1.0F);
        // 取消死亡事件
        event.setCanceled(true);
        // 清除所有负面效果
        player.removeAllEffects();
        // 触发不死图腾效果
        triggerTotemEffect(player);
        // 消耗物品并播放声音
        consumeKayoko(player, level);

    }
    @SuppressWarnings("removal")
    public static void consumeKayoko(Player player, Level level) {
        // 修复：只在服务端执行物品消耗
        if (level.isClientSide) return;

        Item kayokoItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "kayoko"));
        if (kayokoItem == null || kayokoItem == Items.AIR) {
            return;
        }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(kayokoItem)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
                break;
            }
        }
    }
    private static boolean shouldTriggerTotem(Player player) {
        if (player == null) return false;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        // 检查是否持有 kayoko 物品
        return mainHand.getItem() instanceof kayoko || offHand.getItem() instanceof kayoko;
    }
}