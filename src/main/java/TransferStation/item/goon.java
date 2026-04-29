package TransferStation.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class goon extends Item {
    private static final Random random = new Random();
    // 基础半径
    private static final int BASE_RADIUS = 5;
    // 最大半径
    private static final int MAX_RADIUS = 15;
    // 高度系数 - 每10格高度增加1格半径
    private static final float HEIGHT_MULTIPLIER = 0.1f;
    // 最小高度（Y坐标）
    private static final int MIN_HEIGHT = 64;
    // 击飞边缘方块的力度
    private static final double LAUNCH_FORCE = 1.5;
    // 向上击飞的额外力度
    private static final double UPWARD_FORCE = 0.8;

    public goon(Properties properties) {
        super(properties);
    }

    public goon() {
        this(new Properties());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slot, isSelected);

        if (!level.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;
            boolean inHand = player.getMainHandItem().getItem() == this ||
                    player.getOffhandItem().getItem() == this;

            if (inHand && player.tickCount % 2 == 0) {
                processHammerEffect(level, player);
                killEntitiesInRadius(level, player, calculateRadiusBasedOnHeight(player));
            }
        }
    }
    private void processHammerEffect(Level level, Player player) {
        // 计算动态半径（基于高度）
        int radius = calculateRadiusBasedOnHeight(player);

        // 获取所有需要处理的方块位置
        List<BlockPos> affectedPositions = getAffectedPositions(player, radius);

        // 处理方块
        for (BlockPos pos : affectedPositions) {
            if (!level.isLoaded(pos)) continue;

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            // 检查是否是有效方块
            if (isValidBlock(state, level, pos)) {
                // 计算到玩家的距离
                double distance = Math.sqrt(
                        Math.pow(pos.getX() - player.getX(), 2) +
                                Math.pow(pos.getY() - player.getY(), 2) +
                                Math.pow(pos.getZ() - player.getZ(), 2)
                );

                // 判断是否在边缘区域
                boolean isEdgeBlock = distance > radius - 1.5;

                if (isEdgeBlock) {
                    // 边缘方块 - 击飞
                    launchBlock(level, pos, state, player);
                } else {
                    // 内部方块 - 直接破坏
                    level.destroyBlock(pos, true);
                }
            }
        }

        // 轻微减少饥饿值
        int foodLevel = player.getFoodData().getFoodLevel();
        if (foodLevel > 0) {
            player.getFoodData().setFoodLevel((int) (foodLevel - 0.000000000000000000000000000001));
        }
    }
    private int calculateRadiusBasedOnHeight(Player player) {
        return getCurrentRadius(player);
    }
    private List<BlockPos> getAffectedPositions(Player player, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos center = player.blockPosition();

        // 球形区域遍历
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        positions.add(center.offset(x, y, z));
                    }
                }
            }
        }
        return positions;
    }
    /**
     * 检查是否是有效方块
     */
    private boolean isValidBlock(BlockState state, Level level, BlockPos pos) {
        return !state.isAir() &&
                state.getDestroySpeed(level, pos) >= 0 &&
                state.getBlock().defaultDestroyTime() > 0;
    }
    /**
     * 击飞方块
     */
    private void launchBlock(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide) return;

        // 确保是服务器端
        ServerLevel serverLevel = (ServerLevel) level;

        // 为每个掉落物添加击飞效果
        List<ItemStack> drops = Block.getDrops(
                state,
                serverLevel,
                pos,
                serverLevel.getBlockEntity(pos),
                player,
                player.getMainHandItem()
        );

        for (ItemStack drop : drops) {
            ItemEntity itemEntity = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    drop
            );

            // 计算击飞方向（从玩家指向方块）
            double dx = pos.getX() - player.getX();
            double dy = pos.getY() - player.getY();
            double dz = pos.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0) {
                // 标准化方向向量
                dx /= distance;
                dy /= distance;
                dz /= distance;

                // 应用击飞力度，并增加向上的分量
                double horizontalForce = random.nextDouble() * 0.2 + LAUNCH_FORCE;
                double upwardForce = random.nextDouble() * 0.2 + UPWARD_FORCE;

                itemEntity.setDeltaMovement(
                        dx * horizontalForce + random.nextGaussian() * 0.1,
                        Math.max(0.3, dy) + upwardForce + random.nextGaussian() * 0.1,
                        dz * horizontalForce + random.nextGaussian() * 0.1
                );

                // 设置物品实体的生命周期
                itemEntity.setDefaultPickUpDelay();

                // 生成物品实体
                level.addFreshEntity(itemEntity);
            }
        }

        // 移除原始方块
        level.removeBlock(pos, false);
    }

    /**
     * 获取当前效果半径（用于调试或显示）
     */
    public static int getCurrentRadius(Player player) {
        double playerY = player.getY();
        int heightRadiusBonus = 0;

        if (playerY > MIN_HEIGHT) {
            heightRadiusBonus = (int) ((playerY - MIN_HEIGHT) * HEIGHT_MULTIPLIER);
        }

        int radius = BASE_RADIUS + heightRadiusBonus;
        return Math.min(radius, MAX_RADIUS);
    }
    /**
     * 杀死球形范围内的所有生物
     */
    private void killEntitiesInRadius(Level level, Player player, int radius) {
        if (level.isClientSide) return;
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 254));
        // 获取玩家位置
        Vec3 playerPos = player.position();

        // 获取范围内的所有实体
        List<Entity> entities = level.getEntities(null,
                new AABB(
                        playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                        playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
                )
        );

        for (Entity entity : entities) {
            // 排除玩家自身
            if (entity == player) continue;

            // 检查是否是生物（包含怪物、动物等）
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // 计算到玩家的距离
                double distance = playerPos.distanceTo(livingEntity.position());

                // 只在球形范围内生效
                if (distance <= radius) {
                    // 对生物造成伤害
                    livingEntity.hurt(level.damageSources().playerAttack(player), Float.MAX_VALUE);

                    // 可选的击飞效果
                    if (distance > radius - 1.5) { // 边缘生物
                        Vec3 direction = livingEntity.position()
                                .subtract(playerPos)
                                .normalize()
                                .scale(LAUNCH_FORCE)
                                .add(0, UPWARD_FORCE, 0);

                        livingEntity.setDeltaMovement(
                                direction.x + random.nextGaussian() * 0.1,
                                direction.y + random.nextGaussian() * 0.1 + 0.3,
                                direction.z + random.nextGaussian() * 0.1
                        );
                        livingEntity.hurtMarked = true;
                    }
                }
            }
        }
    }
    //全版本都有
    public void appendHoverText(ItemStack stack, Level level,
                                List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, (TooltipContext) level, tooltip, flag);
        tooltip.add(Component.translatable("item.minearchive.goon.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}