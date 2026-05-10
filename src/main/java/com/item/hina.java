package com.item;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static com.minearchive.minearchive.MODID;
//这里忘记提到了hina既可以用于吃也可以用于驱散怪物和反制kayoko
//I forgot to mention here that hina can be used both for eating and for dispeling monsters and countering kayoko
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public  class  hina extends Item {
    public  hina () {
   super(new Properties()
           .food(new FoodProperties.Builder()
                        .nutrition(1)           // 保持的1点饥饿值
                        .saturationMod(2f)      // 保持的2点饱和度
                        .alwaysEat()            // 随时可吃
                        .build())
            .stacksTo(64)               // 最大堆叠32个
        );

}
/*
  The following content may be related to some optimization modules that are incompatible, please report them in time
    后面的内容可能与一些优化模组不兼容相关的报错请及时报告
    */

private static final Set<Player> HINA_HOLDERS = ConcurrentHashMap.newKeySet();

    // 玩家Tick事件：检测是否持有Hina物品，更新持有者集合
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;

        Player player = event.player;
        boolean isHolding = isHoldingHina(player);

        if (isHolding) {
            HINA_HOLDERS.add(player);
        } else {
            HINA_HOLDERS.remove(player);
        }
    }

    // 检测玩家是否手持Hina物品（主手+副手）
    //检测玩家是否手持Hina物品（主手+副手）
    private static boolean isHoldingHina(Player player) {
        return isHina(player.getMainHandItem()) || isHina(player.getOffhandItem());
    }

    // 判断物品是否为Hina物品（需确保Hinam类存在）
    //但这个文件本身就是Hinam类所以AI这个注释有点和意味
    //Determine whether the item is a Hina item (make sure that the Hinam type exists)
    //But this file itself is a Kinam class, so the AI annotation is a bit meaningful
    private static boolean isHina(ItemStack stack) {
        return stack.getItem() instanceof hina;
    }
    // 世界Tick事件：处理持有Hina物品玩家的附近生物路径
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;

        // 移除已死亡的玩家
        HINA_HOLDERS.removeIf(player -> !player.isAlive());

        for (Player holder : HINA_HOLDERS) {
            // 获取玩家周围12格内的所有Mob
            List<Mob> nearbyMobs = level.getEntitiesOfClass(
                    Mob.class,
                    holder.getBoundingBox().inflate(12.0)
            );

            for (Mob mob : nearbyMobs) {
                modifyEntityPathfinding(mob, holder);
            }
        }
    }
    // 修改生物路径：若生物靠近持有者，则远离并清除攻击目标
    private static void modifyEntityPathfinding(Mob entity, Player holder) {
        PathNavigation nav = entity.getNavigation();
        if (nav == null) return;

        double distance = entity.distanceTo(holder);
        if (distance >= 8.0) return;

        // 关键拓展：清除敌对生物的攻击目标（即使正在攻击持有者）
        if (entity.getTarget() == holder) {
            entity.setTarget(null); // 强制清除攻击目标
            entity.setAggressive(false); // 取消敌对状态
        }

        // 仅PathfinderMob支持随机路径生成
        if (entity instanceof PathfinderMob pathfinderMob) {
            // 计算远离持有者的目标位置
            Vec3 targetPos = DefaultRandomPos.getPosAway(pathfinderMob, 16, 7, holder.position());
            if (targetPos != null) {
                nav.stop(); // 停止当前路径
                nav.moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0); // 移动到新位置
            }
        }
    }
}