package com.item;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import java.util.*;
import java.util.stream.Collectors;

public class kayoko extends Item {
    private static final List<MobEffect> BENEFICIAL_EFFECTS = Arrays.asList(
            MobEffects.MOVEMENT_SPEED,      // 速度
            MobEffects.DIG_SPEED,           // 急迫
            MobEffects.DAMAGE_BOOST,        // 力量
            MobEffects.REGENERATION,        // 再生
            MobEffects.DAMAGE_RESISTANCE,   // 抗性提升
            MobEffects.FIRE_RESISTANCE,     // 防火
            MobEffects.WATER_BREATHING,     // 水下呼吸
            MobEffects.JUMP,                // 跳跃提升
            MobEffects.NIGHT_VISION,        // 夜视
            MobEffects.HEALTH_BOOST,        // 生命提升
            MobEffects.ABSORPTION,          // 伤害吸收
            MobEffects.SATURATION,          // 饱和
            MobEffects.LUCK,                // 幸运
            MobEffects.DOLPHINS_GRACE,      // 海豚的恩惠
            MobEffects.SLOW_FALLING,        // 缓降
            MobEffects.CONDUIT_POWER,       // 潮涌能量
            MobEffects.HERO_OF_THE_VILLAGE  // 村庄英雄
    );

    // 可用的附魔列表（过滤掉诅咒附魔）
    private static List<Enchantment> AVAILABLE_ENCHANTMENTS = null;

    // 保底计数器：记录每个玩家连续没有触发附魔的次数
    private static final Map<UUID, Integer> ENCHANTMENT_PITY_COUNTER = new HashMap<>();

    // 构造器
    public kayoko() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(99)           // 保持原来的1点饥饿值
                        .saturationMod(99f)      // 保持原来的2点饱和度
                        .alwaysEat()            // 随时可吃
                        .build())
                .stacksTo(64)               // 最大堆叠64个（或保持原样）
        );

        // 延迟初始化可用附魔列表
        if (AVAILABLE_ENCHANTMENTS == null) {
            initAvailableEnchantments();
        }
    }

    // 初始化可用附魔列表
    private void initAvailableEnchantments() {
        AVAILABLE_ENCHANTMENTS = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(enchantment -> !enchantment.isCurse())  // 排除诅咒附魔
                .collect(Collectors.toList());
    }

    // 获取随机附魔
    private Enchantment getRandomEnchantment(Random rand) {
        if (AVAILABLE_ENCHANTMENTS == null || AVAILABLE_ENCHANTMENTS.isEmpty()) {
            initAvailableEnchantments();
        }
        return AVAILABLE_ENCHANTMENTS.get(rand.nextInt(AVAILABLE_ENCHANTMENTS.size()));
    }

    // 随机增强玩家背包物品
    private void randomlyEnhancePlayerItem(Player player, Random rand) {
        // 获取玩家背包中可附魔的物品
        List<ItemStack> enchantableItems = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            // 只选择可附魔且有耐久的物品（武器、工具、盔甲）
            if (!stack.isEmpty() && stack.isEnchantable() && stack.getMaxDamage() > 0) {
                enchantableItems.add(stack);
            }
        }

        if (enchantableItems.isEmpty()) {
            // 如果没有可附魔的物品，尝试选择其他可接受的物品
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty() && stack.isEnchantable()) {
                    enchantableItems.add(stack);
                }
            }
        }

        if (enchantableItems.isEmpty()) {
            return;  // 背包中没有可附魔的物品
        }

        // 随机选择一个物品
        ItemStack targetStack = enchantableItems.get(rand.nextInt(enchantableItems.size()));

        // 检查物品是否已有附魔
        boolean hasEnchantments = EnchantmentHelper.getEnchantments(targetStack).size() > 0;

        if (hasEnchantments) {
            int newEnchantmentCount = 1 + rand.nextInt(5);
            for (int i = 0; i < newEnchantmentCount; i++) {
                addRandomEnchantment(targetStack, rand, 1 + rand.nextInt(2));  // 等级1-2
            }
        } else {
            int enchantmentCount = 1 + rand.nextInt(20);
            for (int i = 0; i < enchantmentCount; i++) {
                addRandomEnchantment(targetStack, rand, 1 + rand.nextInt(2));  // 等级1-2
            }
        }
    }

    // 为物品添加随机附魔
    private void addRandomEnchantment(ItemStack stack, Random rand, int level) {
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            Enchantment enchantment = getRandomEnchantment(rand);

            // 检查附魔是否可以应用于该物品
            if (enchantment.canEnchant(stack)) {
                Map<Enchantment, Integer> currentEnchantments = new HashMap<>(
                        EnchantmentHelper.getEnchantments(stack)
                );

                if (currentEnchantments.containsKey(enchantment)) {
                    // 已存在，提升等级
                    int currentLevel = currentEnchantments.get(enchantment);
                    int newLevel = Math.min(5, currentLevel + 1);  // 最大等级限制为5
                    currentEnchantments.put(enchantment, newLevel);
                    EnchantmentHelper.setEnchantments(currentEnchantments, stack);
                } else {
                    // 检查与其他附魔的兼容性
                    boolean compatible = true;
                    for (Enchantment existing : currentEnchantments.keySet()) {
                        if (!enchantment.isCompatibleWith(existing)) {
                            compatible = false;
                            break;
                        }
                    }

                    if (compatible) {
                        currentEnchantments.put(enchantment, level);
                        EnchantmentHelper.setEnchantments(currentEnchantments, stack);
                    } else {
                        attempts++;
                        continue;
                    }
                }
                break;
            }
            attempts++;
        }
    }

    // 全背包附魔（保底机制）
    private void enchantAllPlayerItems(Player player, Random rand) {
        int totalEnchanted = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty() && stack.isEnchantable()) {
                // 随机决定给这个物品添加1-3个附魔
                int enchantmentCount = 3 + rand.nextInt(10);

                for (int j = 0; j < enchantmentCount; j++) {
                    addRandomEnchantment(stack, rand, 1 + rand.nextInt(2));
                }

                totalEnchanted++;



            }
        }
    }

    // 更新保底计数器
    private void updatePityCounter(Player player, boolean gotEnchantment) {
        UUID playerId = player.getUUID();

        if (gotEnchantment) {
            // 触发了附魔，重置计数器
            ENCHANTMENT_PITY_COUNTER.put(playerId, 0);
        } else {
            // 没有触发附魔，增加计数器
            int currentCount = ENCHANTMENT_PITY_COUNTER.getOrDefault(playerId, 0) + 1;
            ENCHANTMENT_PITY_COUNTER.put(playerId, currentCount);

            // 检查是否达到保底
            if (currentCount >= 1) {
                // 触发全背包附魔
                Random rand = new Random();
                enchantAllPlayerItems(player, rand);

                // 重置计数器
                ENCHANTMENT_PITY_COUNTER.put(playerId, 0);
            }
        }
    }

    public static void triggerTotemEffect(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        // 模拟不死图腾的效果
        player.setHealth(1.0F);  // 恢复1点生命值

        // 清除所有负面效果
        player.removeAllEffects();

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 800, 255));

        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 255));

        // 添加防火效果
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 255));

        // 在玩家位置播放效果粒子
        Level level = player.level();
        for(int i = 0; i < 20; ++i) {
            double d0 = player.getRandom().nextGaussian() * 0.02;
            double d1 = player.getRandom().nextGaussian() * 0.02;
            double d2 = player.getRandom().nextGaussian() * 0.02;
            // 这里可以添加粒子效果，但需要粒子系统支持
        }
    }

    // +++ 新增方法：检查玩家是否手持kayoko物品 +++
    public static boolean isPlayerHoldingKayoko(Player player) {
        if (player == null) return false;

        // 检查主手和副手
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return mainHand.getItem() instanceof kayoko || offHand.getItem() instanceof kayoko;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // 调用父类方法处理基础食物逻辑
        //这里进行服务端判定是因为如果不做服务端判定的话，编译器和MC会出现一些奇奇怪怪的问题
        ItemStack result = super.finishUsingItem(stack, level, entity);

        // 只在服务器端执行效果给予逻辑
        if (!level.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;
            Random rand = new Random();

            // 50%概率选择增益效果，50%概率选择附魔物品
            if (rand.nextFloat() < 0.50f) {
                // 50%概率：给予增益效果

                // 随机选择一个有益效果
                MobEffect randomEffect = BENEFICIAL_EFFECTS.get(rand.nextInt(BENEFICIAL_EFFECTS.size()));

                // 随机持续时间和等级
                int duration = 600 + rand.nextInt(600);  // 30-60秒
                int amplifier = rand.nextInt(255);         // 等级0-1

                player.addEffect(new MobEffectInstance(randomEffect, duration, amplifier));

                if (rand.nextFloat() < 0.50f) {
                    MobEffect secondEffect = BENEFICIAL_EFFECTS.get(rand.nextInt(BENEFICIAL_EFFECTS.size()));
                    int secondDuration = 300 + rand.nextInt(300);  // 15-30秒

                    // 确保不重复相同效果
                    if (!secondEffect.equals(randomEffect)) {
                        player.addEffect(new MobEffectInstance(secondEffect, secondDuration, 0));
                    }
                }

                // 更新保底计数器（没有触发附魔）
                updatePityCounter(player, false);
            } else {
                randomlyEnhancePlayerItem(player, rand);

                // 更新保底计数器（触发了附魔）
                updatePityCounter(player, true);
            }
        }

        return result;
    }
//其实并非硬编码如果你点一下那4个东西，就会显现出原来的代码
    // 可选：添加工具提示
    @Override
    public void appendHoverText(ItemStack stack, Level level,
                                List<net.minecraft.network.chat.Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(net.minecraft.network.chat.Component.translatable("item.minearchive.kayoko.tooltip")
                .withStyle(net.minecraft.ChatFormatting.GRAY));

        tooltip.add(net.minecraft.network.chat.Component.translatable("item.minearchive.kayoko.enchant_hint")
                .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));

        tooltip.add(net.minecraft.network.chat.Component.translatable("item.minearchive.kayoko.pity_mechanism")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY, net.minecraft.ChatFormatting.ITALIC));

        tooltip.add(net.minecraft.network.chat.Component.translatable("item.minearchive.kayoko.holding_effect")
                .withStyle(net.minecraft.ChatFormatting.GOLD));
    }
}
