package com.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class mutsuki extends Item {
    public mutsuki() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(2f)
                        .alwaysEat()
                        .build())
                .stacksTo(64)
        );
    }
//将TNT生成在玩家所在地
    //Spawn TNT at the player's location
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof net.minecraft.world.entity.player.Player) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            PrimedTnt tnt = new PrimedTnt(level, x, y, z, null);
            level.addFreshEntity(tnt);
        }
        return super.finishUsingItem(stack, level, entity);
    }
}