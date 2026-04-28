package com.minearchive;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
//你说的对但，但是我要在这里放东西，虽然是在测试版的2.6版本之前这里是没有任何作用的
//You're right, but I'm going to put something here, although it didn't work here until the beta version 2.6
@Mod.EventBusSubscriber(modid = minearchive.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec SPEC = BUILDER.build();
}