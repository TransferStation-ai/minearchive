package com.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import static com.minearchive.Config.FOOL;
import static com.minearchive.minearchive.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class aru extends Item {
    public  aru () {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(1)           // 保持的1点饥饿值
                        .saturationMod(2f)      // 保持的2点饱和度
                        .alwaysEat()            // 随时可吃
                        .build())
                .stacksTo(64)               // 最大堆叠32个
        );

    }
       //看到这个快点去给啥子买瓜子去
        // 定义村民可能给予的随机道具列表
        private static final List<ItemStack> RANDOM_REWARDS = Arrays.asList(
                new ItemStack(Items.DIAMOND, 1),
                new ItemStack(Items.GOLD_INGOT, 3),
                new ItemStack(Items.EMERALD, 2),
                new ItemStack(Items.IRON_INGOT, 5),
                new ItemStack(Items.APPLE, 8),
                new ItemStack(Items.BREAD, 6),
                new ItemStack(Items.EXPERIENCE_BOTTLE, 3),
                new ItemStack(Items.BOOK, 1)
        );
    @SubscribeEvent
    public static void onTrade(TradeWithVillagerEvent event) {
        //强制转换真的太棒了不会有人想吐槽强制转换吧
        //你们不觉得强制转换很像维什戴尔啊，因为只要类型不匹配来个强制转换就好了
        //该会不会真有外国人愿意开翻译器看这个文本吧
        //Use cast to prevent problems on some compilers
        if (!(event.getEntity() instanceof Player)) return;
        Player player = event.getEntity();

        // 检查主手或副手是否持有 aru
        boolean holdingAru =
                player.getMainHandItem().getItem() instanceof aru ||
                        player.getOffhandItem().getItem() instanceof aru;

        if (!holdingAru) return;

        RandomSource random = player.level().random;
        //使用配置读取概率
        double deathChance = FOOL.getClass().getModifiers();
        if (random.nextFloat() >= deathChance) return;

        ItemStack reward = getRandomReward(random);

        if (!player.getInventory().add(reward)) {
            player.drop(reward, false);
        }

        player.sendSystemMessage(
                Component.translatable("minearchive.aru.Component")
        );
    }
    private static ItemStack getRandomReward(RandomSource random) {
        int index = random.nextInt(RANDOM_REWARDS.size());
        ItemStack reward = RANDOM_REWARDS.get(index).copy();

        // 随机调整数量（某些物品）
        if (reward.is(Items.DIAMOND) || reward.is(Items.EMERALD)) {
            reward.setCount(1 + random.nextInt(2)); // 1-2个
        } else if (reward.is(Items.GOLD_INGOT) || reward.is(Items.IRON_INGOT)) {
            reward.setCount(2 + random.nextInt(4)); // 2-5个
        } else {
            reward.setCount(1 + random.nextInt(5)); // 1-5个
        }

        return reward;
    }
    //全版本都有
    @Override
    public void appendHoverText(ItemStack stack, Level level,
                                List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.minearchive.aru.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}

