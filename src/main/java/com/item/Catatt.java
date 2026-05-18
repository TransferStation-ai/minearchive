package com.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber
public class Catatt extends Enchantment {

    public static final String ENCHANTMENT_ID = "cat_attraction";

    public Catatt() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
                new EquipmentSlot[] {
                        EquipmentSlot.HEAD,
                        EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS,
                        EquipmentSlot.FEET
                });
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return super.isAllowedOnBooks();
    }

    @Override
    public boolean isTreasureOnly() {
        return super.isTreasureOnly();
    }

    @Override
    public boolean isTradeable() {
        return super.isTradeable();
    }

    @EventBusSubscriber
    public static class CatAttractionHandler {
        public static final DeferredRegister<Enchantment> ENCHANTMENTS =
                DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "minearchive");
        public static final RegistryObject<Enchantment> CAT_ATTRACTION = ENCHANTMENTS.register(ENCHANTMENT_ID,
                Catatt::new);

        private static int tickCounter = 0;
        private static int levelBonus;

        public static Catatt getEnchantment() {
            if (CAT_ATTRACTION.isPresent()) {
                return (Catatt) CAT_ATTRACTION.get();
            }
            return null;
        }

        public static boolean hasEnchantedArmor(Player player) {
            Catatt enchantment = getEnchantment();
            if (enchantment == null) return false;

            for (ItemStack armorSlot : player.getArmorSlots()) {
                if (armorSlot.getEnchantmentLevel(enchantment) > 0) {
                    return true;
                }
            }
            return false;
        }

        public static int getTotalEnchantmentLevel(Player player) {
            Catatt enchantment = getEnchantment();
            if (enchantment == null) return 0;

            int totalLevel = 0;
            for (ItemStack armorSlot : player.getArmorSlots()) {
                totalLevel += armorSlot.getEnchantmentLevel(enchantment);
            }
            return Math.min(totalLevel, 3);
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Player player = event.player;
            if (player.level().isClientSide) {
                return;
            }

            ServerLevel level = (ServerLevel) player.level();
            BlockPos playerPos = player.blockPosition();

            if (!hasEnchantedArmor(player)) return;

            int enchantmentLevel = getTotalEnchantmentLevel(player);

            tickCounter++;
            if (tickCounter % 20 != 0) return;

            if (Math.random() < 0.05 + (0.02 * enchantmentLevel)) {
                trySpawnCatsAroundPlayer(level, playerPos, enchantmentLevel);
            }

            attractNearbyCats(level, player, enchantmentLevel);
        }

        private static void trySpawnCatsAroundPlayer(ServerLevel level, BlockPos pos, int levelBonus) {
            CatAttractionHandler.levelBonus = levelBonus;
            int x = pos.getX() + (level.random.nextInt(25) - 12);
            int z = pos.getZ() + (level.random.nextInt(25) - 12);

            for (int i = 0; i < 3; i++) {
                int y = pos.getY() + (level.random.nextInt(5) - 2);
                BlockPos spawnPos = new BlockPos(x, y, z);

                if (level.getBlockState(spawnPos).isAir() &&
                        level.getBlockState(spawnPos.below()).isSolidRender(level, spawnPos.below())) {

                    Cat cat = new Cat(EntityType.CAT, level);
                    cat.setPos(x + 0.5, y, z + 0.5);
                    cat.setTame(false);
                    cat.setPersistenceRequired();

                    level.addFreshEntity(cat);
                    break;
                }
            }
        }

        private static void attractNearbyCats(ServerLevel level, Player player, int levelBonus) {
            AABB area = new AABB(player.blockPosition()).inflate(15);
            List<Cat> nearbyCats = level.getEntitiesOfClass(Cat.class, area);

            for (Cat cat : nearbyCats) {
                if (cat.isTame() && cat.getOwner() == player) continue;

                double distance = cat.distanceToSqr(player);
                double attractionChance = 0.0;

                if (distance < 25) {
                    attractionChance = 0.6 + (0.1 * levelBonus);
                } else if (distance < 100) {
                    attractionChance = 0.3 + (0.1 * levelBonus);
                } else if (distance < 225) {
                    attractionChance = 0.1 + (0.05 * levelBonus);
                }

                if (level.random.nextDouble() < attractionChance) {
                    Vec3 playerPos = player.position();
                    cat.getNavigation().moveTo(playerPos.x, playerPos.y, playerPos.z, 1.0);

                    if (level.random.nextDouble() < 0.3 * levelBonus) {
                        cat.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SPEED,
                                100,
                                Math.min(levelBonus - 1, 1)
                        ));
                    }
                }
            }
        }

        public static int getLevelBonus() {
            return levelBonus;
        }

        public static void setLevelBonus(int levelBonus) {
            CatAttractionHandler.levelBonus = levelBonus;
        }
    }
}