package com.minearchive;
import com.eventlistening.totemeventHandler;
import com.item.*;
import net.minecraft.world.item.Item;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import static com.item.Catatt.CatAttractionHandler.CAT_ATTRACTION;
import static net.minecraft.client.Minecraft.getInstance;

@Mod(minearchive.MODID)
public class minearchive {

        // You put your mod ID in this to allow some class files to define the mod location more quickly
    //你把你的模组ID放进这里面以便让一些类文件能够更快定义模组位置
    //There's a cold joke that when I was commenting on this, I accidentally commented out my own mod ID
    //Just like that //     public static final String MODID = "minearchive";

    public static final String MODID = "minearchive";
    // Directly reference a slf4j logger
    //直接引用slf4j日志机
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "untitled1" namespace
    //创建一个延迟注册器来存放所有块，这些块都会注册在“untitled”命名空间下
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "untitled1" namespace
    //创建一个延迟注册表来存放所有将登记在“untitled”命名空间下的物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
//无法被移除的方块注册
    //反正他一直都在这里我没有办法去移除他
//Block registrations that cannot be removed
//He's always been here anyway, and there's no way to remove him
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("kayoko_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("kayoko_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
    //吃我一招注册之墙
    //Eat my trick registration wall
    public static final RegistryObject<Item> EXAMPLE_ITEM //这里填的时候记得填全大写，这里因为是早期忘记修了
            = ITEMS.register("kayoko", Kayoko::new); //这里的参数是有两，一个是方法名和一个物品注册名但最好的情况是物品注册名和方法名都是小写跟同一个名字
    public static final RegistryObject<Item> GNT
            = ITEMS.register("goon", Goon::new);
    public static final RegistryObject<Item>  NINA
            = ITEMS.register("hina", Hina::new);
    public static final RegistryObject<Item>  MUTSUKI
            = ITEMS.register("mutsuki", Mutsuki::new);
    public static final RegistryObject<Item>  ARU
            = ITEMS.register("Aru", Aru::new);
    public static final RegistryObject<Item>  HARUKA
            = ITEMS.register("haruka", Haruka::new);

    public minearchive() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the commonSetup method for modloading
        //注册 commonSetup 方法进行 modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        // 将延迟寄存器注册到mod事件总线，这样块才能被注册
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        // 将延迟寄存器注册到mod事件总线，这样项目才能被注册
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        //将延迟注册器注册到模组事件总线，这样标签页才会被注册
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        //注册参加我们感兴趣的服务器及其他游戏活动
        MinecraftForge.EVENT_BUS.register(this);
        // Register the item to a creative tab
        //将该物品注册到创意标签页
        modEventBus.addListener(this::addCreative);
        Catatt.CatAttractionHandler.ENCHANTMENTS.register(modEventBus);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //注册我们模组的ForgeConfigSpec，这样Forge就能帮我们创建并加载配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static Item get() {
        return new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build()));
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));



    }
    //看到此注释请自己去steam买个超级地球 虽然这不是强迫你就是
    //If you see this comment, please go to Steam to buy a Super Earth yourself Although this is not forcing you to do
    //非法广播：创造物品标签栏列表
    //Illegal broadcasting: Create a list of item tags
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register(
            "minearchive_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.minearchive_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(EXAMPLE_ITEM.get());
                        if (NINA != null) {
                            output.accept(NINA.get());
                        }
                        if (GNT != null) {
                            output.accept(GNT.get());
                        }
                        if (MUTSUKI != null) {
                            output.accept(MUTSUKI.get());
                        }
                        if (ARU != null) {
                            output.accept(ARU.get());
                        }
                        if (HARUKA != null) {
                            output.accept(HARUKA.get());
                        }
                    })
                    .build()
    );
    public static final RegistryObject<CreativeModeTab> SECOND_TAB = CREATIVE_MODE_TABS.register(
            "minearchive_tab_2",
            () -> CreativeModeTab.builder()
                    .withTabsAfter(EXAMPLE_TAB.getId())
                    .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.minearchive_tab_2"))
                    .displayItems((parameters, output) -> {
                        ItemStack enchantedBook1 = EnchantedBookItem.createForEnchantment(
                                new EnchantmentInstance(CAT_ATTRACTION.get(), 1)
                        );
                        output.accept(enchantedBook1);

                        ItemStack vanillaBook = new ItemStack(Items.ENCHANTED_BOOK);
                        vanillaBook.enchant(CAT_ATTRACTION.get(), 1);
                        output.accept(vanillaBook);
                    })
                    .build()
    );
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    //你可以使用 SubscribeEvent，让事件总线发现调用的方法
    //事实是你必须得使用 @Mod.EventBusSubscriber来让类文件，无需在主类调用，就能生效
    //The fact is that you have to use @Mod.EventBusSubscriber to make the class file take effect without having to call it in the main class
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        //服务器开始时做点什么
        //也许我可以在这里定义服务器事件
        LOGGER.info("HELLO from server starting");
        MinecraftForge.EVENT_BUS.register(totemeventHandler.class);
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    //你可以使用 EventBusSubscriber 自动注册所有标注为 @SubscribeEvent 的类中的所有静态方法。
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", getInstance().getUser().getName());
        }
    }

    private static class MBeanSupport {
        public void register(String catatt, Object aNew) {
        }
    }
}
