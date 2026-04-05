package minearchive.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import java.util.Random;
public class goon extends Item {
    private static final Random random = new Random();

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
            boolean inHand = player.getMainHandItem().getItem() == this || player.getOffhandItem().getItem() == this;
            if (inHand) {
                int foodLevel = player.getFoodData().getFoodLevel();
                if (foodLevel > 0) {
                    if (player.tickCount % 2 == 0) {
                        player.getFoodData().setFoodLevel((int) (foodLevel - 0.000000000000000000000000000001));
                    }


                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 254));
                    int radius = 5;
                    int blocksToBreak = 4048;
                    for (int i = 0; i < blocksToBreak; i++) {
                        int dx = random.nextInt(radius * 2 + 1) - radius;
                        int dy = random.nextInt(radius * 2 + 1) - radius;
                        int dz = random.nextInt(radius * 2 + 1) - radius;
                        BlockPos pos = player.blockPosition().offset(dx, dy, dz);
                        if (level.getBlockState(pos).getBlock() != Blocks.BEDROCK && !level.isEmptyBlock(pos)) {
                            level.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }
    }
}