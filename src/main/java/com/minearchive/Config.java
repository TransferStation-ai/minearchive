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

    //以下配置的允许值都是1024但客户端请不要将其设置到256以上的值可能导致很卡
    // The maximum allowed values for the following configurations are all 1024
  //  However, the client should not adjust this value to a value of about 256 or above

 // 基础半径
    public static final ForgeConfigSpec.IntValue BASE_RADIUS= BUILDER.comment("The base radius of the goon when performing sabotage missions").defineInRange("goonbaseradius", 5, 0, 1024);
    // 最大半径
    public static final ForgeConfigSpec.IntValue MAX_RADIUS= BUILDER.comment("Normally, the base radius is limited by the maximum radius, so don't set the maximum radius to a larger value as well").defineInRange("goonMaximumRadius", 128, 0, 1024);
    // 速度转半径系数
    public static final ForgeConfigSpec.IntValue SPEED_MULTIPLIER= BUILDER.comment("Added in the 3.0 beta").defineInRange("goonVelocitytoblockradiuscoefficient", 15, 0, 1024);
    // 最小触发速度
    public static final ForgeConfigSpec.DoubleValue MIN_SPEED= BUILDER.comment("Minimum trigger speed This thing should be enabled when the fall speed is greater than the minimum speed").defineInRange("goonMinimumtriggerspeed", 0.5, 0, 1024);
    // 击飞边缘方块的力度
    public static final ForgeConfigSpec.DoubleValue LAUNCH_FORCE= BUILDER.comment("When a block is knocked away, the force with which it is knocked away").defineInRange("goonTheforcewithwhichtheedgedblocksareknockedaway", 1.5, 0, 1024);
    // 向上击飞的额外力度
    public static final ForgeConfigSpec.DoubleValue UPWARD_FORCE= BUILDER.comment("Usually this value results in the extra height at which the item flies").defineInRange("extraforcetoflyupwards", 0.8, 0, 1024);

}
