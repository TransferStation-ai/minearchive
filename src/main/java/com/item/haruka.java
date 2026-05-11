package com.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class haruka  extends Item  {

    public haruka() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(2f)
                        .alwaysEat()
                        .build())
                .stacksTo(64)
        );
    }

    

    }




