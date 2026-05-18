package com.item;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.Random;
import java.util.UUID;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import static com.minearchive.minearchive.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Haruka extends Item {
    private static final Random RAND = new Random();
    private static final UUID ATK_UUID = UUID.fromString("12345678-1234-1234-1234-123456789012");
    private static final AttributeModifier.Operation SET_OP = AttributeModifier.Operation.ADDITION;

    public Haruka() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder().nutrition(1).saturationMod(2f).alwaysEat().build())
                .stacksTo(64));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player p) {
            BlockPos c = p.blockPosition();
            for (int x = -5; x <= 5; x++)
                for (int y = -2; y <= 2; y++)
                    for (int z = -5; z <= 5; z++) {
                        BlockPos pos = c.offset(x, y, z);
                        BlockState s = level.getBlockState(pos);
                        if (s.getBlock() instanceof CropBlock crop && !crop.isMaxAge(s))
                            crop.growCrops(level, pos, s);
                    }

            AttributeInstance atk = p.getAttribute(Attributes.ATTACK_DAMAGE);
            if (atk != null) {
                atk.removeModifier(ATK_UUID);
                atk.addTransientModifier(new AttributeModifier(ATK_UUID, "haruka_atk", 1.0, SET_OP));
            }
            p.getPersistentData().putBoolean("haruka_iron", true);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = event.getEntity();
            onAttack(p);
        }
    }

    public static void onAttack(Player p) {
        if (p.level().isClientSide) {
            return;
        }
        if (p.getPersistentData().getBoolean("haruka_iron") && RAND.nextFloat() < 0.3f) {
            var golem = EntityType.IRON_GOLEM.create(p.level());
            if (golem != null) {
                golem.moveTo(p.position());
                p.level().addFreshEntity(golem);
            }
        }
    }
}