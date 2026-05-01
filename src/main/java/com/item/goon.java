package com.item;

import com.minearchive.Config;
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
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static com.minearchive.Config.*;

public class goon extends Item {
    private static final Random random = new Random();
    private static Double BASE_RADIUS() {
        return Double.valueOf(Config.BASE_RADIUS.get());
    }

    private static int MAX_RADIUS() {
        return MAX_RADIUS.get();
    }

    private static double SPEED_MULTIPLIER() {
        return Config.SPEED_MULTIPLIER.get();
    }
//
    private static double MIN_SPEED() {
        return Config.MIN_SPEED.get();
    }

    private static double LAUNCH_FORCE() {
        return LAUNCH_FORCE.get();
    }

    private static double UPWARD_FORCE() {
        return UPWARD_FORCE.get();
    }

    public goon(Properties properties) {
        super(properties);
    }

    public goon() {
        this(new Properties());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slot, isSelected);

        if (!level.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;
            boolean inHand = player.getMainHandItem().getItem() == this ||
                    player.getOffhandItem().getItem() == this;

            if (inHand && player.tickCount % 2 == 0) {
                int radius = calculateRadiusBasedOnFallSpeed(player);
                processHammerEffect(level, player, radius);
                killEntitiesInRadius(level, player, radius);
            }
        }
    }

    private int calculateRadiusBasedOnFallSpeed(Player player) {
        double fallSpeed = Math.abs(player.getDeltaMovement().y);
        
        if (fallSpeed < Config.MIN_SPEED.get()) {
            return BASE_RADIUS.get();
        }
        
        int speedBonus = (int)(fallSpeed * SPEED_MULTIPLIER.get());
        int radius = BASE_RADIUS.get() + speedBonus;
        
        return Math.min(radius, Config.MAX_RADIUS.get());
    }

    private void processHammerEffect(Level level, Player player, int radius) {
        List<BlockPos> affectedPositions = getAffectedPositions(player, radius);

        for (BlockPos pos : affectedPositions) {
            if (!level.isLoaded(pos)) continue;

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (isValidBlock(state, level, pos)) {
                double distance = Math.sqrt(
                        Math.pow(pos.getX() - player.getX(), 2) +
                                Math.pow(pos.getY() - player.getY(), 2) +
                                Math.pow(pos.getZ() - player.getZ(), 2)
                );

                boolean isEdgeBlock = distance > radius - 1.5;

                if (isEdgeBlock) {
                    launchBlock(level, pos, state, player);
                } else {
                    level.destroyBlock(pos, true);
                }
            }
        }

        int foodLevel = player.getFoodData().getFoodLevel();
        if (foodLevel > 0) {
            player.getFoodData().setFoodLevel((int) (foodLevel - 1));
        }
    }

    private List<BlockPos> getAffectedPositions(Player player, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos center = player.blockPosition();

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

    private boolean isValidBlock(BlockState state, Level level, BlockPos pos) {
        return !state.isAir() &&
                state.getDestroySpeed(level, pos) >= 0 &&
                state.getBlock().defaultDestroyTime() > 0;
    }

    private void launchBlock(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;

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

            double dx = pos.getX() - player.getX();
            double dy = pos.getY() - player.getY();
            double dz = pos.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0) {
                dx /= distance;
                dy /= distance;
                dz /= distance;

                double horizontalForce = random.nextDouble() * 0.2 + LAUNCH_FORCE.get();
                double upwardForce = random.nextDouble() * 0.2 + UPWARD_FORCE.get();

                itemEntity.setDeltaMovement(
                        dx * horizontalForce + random.nextGaussian() * 0.1,
                        Math.max(0.3, dy) + upwardForce + random.nextGaussian() * 0.1,
                        dz * horizontalForce + random.nextGaussian() * 0.1
                );

                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }

        level.removeBlock(pos, false);
    }

    public static int getCurrentRadius(Player player) {
        double fallSpeed = Math.abs(player.getDeltaMovement().y);
        if (fallSpeed < MIN_SPEED.get()) {
            return BASE_RADIUS.get();
        }
        int speedBonus = (int)(fallSpeed * SPEED_MULTIPLIER.get());
        int radius = BASE_RADIUS.get() + speedBonus;
        return Math.min(radius, MAX_RADIUS.get());
    }

    private void killEntitiesInRadius(Level level, Player player, int radius) {
        if (level.isClientSide) return;
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 254));

        Vec3 playerPos = player.position();

        List<Entity> entities = level.getEntities(null,
                new AABB(
                        playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                        playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
                )
        );

        for (Entity entity : entities) {
            if (entity == player) continue;

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                double distance = playerPos.distanceTo(livingEntity.position());

                if (distance <= radius) {
                    livingEntity.hurt(level.damageSources().playerAttack(player), Float.MAX_VALUE);

                    if (distance > radius - 1.5) {
                        Vec3 direction = livingEntity.position()
                                .subtract(playerPos)
                                .normalize()
                                .scale(LAUNCH_FORCE.get())
                                .add(0, UPWARD_FORCE.get(), 0);

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
    //当此物品被选中时在MC的物品显示里的提示
    @Override
    public void appendHoverText(ItemStack stack, Level level,
                                List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.minearchive.goon.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}