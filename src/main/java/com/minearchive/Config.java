package com.minearchive;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

//你说的对但，但是我要在这里放东西，虽然是在测试版的2.5版本之前这里是没有任何作用的
//You're right, but I'm going to put something here, although it didn't work here until the beta version 2.5
@Mod.EventBusSubscriber(modid = minearchive.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    //为一个名称为hina物品相关的判定添加配置项以便能够调整相关的概率，因为我脸真的太黑了，50%成100%了
    //Add a config to a decision named Hina item to be able to adjust the associated probability, because my face is really dark, 50% becomes 100%
    //代码中的允许值应该是0~1
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.DoubleValue MAGIC_NUMBER = BUILDER.comment("When an attacker holding Hina attacks a player holding Kayoko, there is a chance that Kayoko's immortal totem effect will be destroyed").defineInRange("hina_kayoko", 0.5, 0, 1);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static final ForgeConfigSpec.DoubleValue FOOL= BUILDER.comment("The probability of being cared for when a player holds an ARU item").defineInRange("magicNumber", 0.5, 0, 1);

}