package minearchive;
import minearchive.item.catatt;
import net.minecraft.world.item.Item;
import minearchive.item.goon;
import minearchive.item.kayoko;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static minearchive.item.catatt.CatAttractionHandler.CAT_ATTRACTION;

/**
 * Minecraft模组主类，负责初始化模组核心功能
 * 包含方块、物品、创意栏标签的注册，以及事件监听的注册
 */
@Mod(Minearchive.MODID)
public class Minearchive {
    // 模组ID常量，用于统一标识
    public static final String MODID = "minearchive";
    // 日志记录器，用于输出调试信息
    private static final Logger LOGGER = LogUtils.getLogger();

    // 延迟注册器：方块注册表（使用Forge注册系统）
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // 延迟注册器：物品注册表
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // 延迟注册器：创意栏标签页
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 注册示例方块：kayoko_block，属性为石材质感
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("kayoko_block",
        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // 注册方块对应的物品（方块放置物）
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("kayoko_block",
        () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // 注册自定义物品：kayoko（使用kayoko类）
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("kayoko", kayoko::new);

    // 注册食物物品：hina，营养值1，饱和度2
    public static final RegistryObject<Item> NINA = ITEMS.register("hina",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 注册食物物品：lu，属性同上
    public static final RegistryObject<Item> LU = ITEMS.register("lu",
        () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 注册goon物品（使用goon类）
    public static final RegistryObject<Item> GNT = ITEMS.register("goon", goon::new);

    /**
     * 模组构造函数，初始化事件总线和注册表
     * 注册各类DeferredRegister并绑定事件监听
     */
    public Minearchive() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册通用初始化方法
        modEventBus.addListener(this::commonSetup);

        // 注册方块、物品、创意栏标签到事件总线
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册Forge事件总线监听
        MinecraftForge.EVENT_BUS.register(this);

        // 注册创意栏构建监听
        modEventBus.addListener(this::addCreative);
        // 注册附魔相关事件
        catatt.CatAttractionHandler.ENCHANTMENTS.register(modEventBus);
    }

    // 示例物品创建方法（当前未被使用）
    private static Item get() {
        return new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build()));
    }

    /**
     * 通用初始化方法，在模组加载时执行
     * @param event FMLCommonSetupEvent 事件对象
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // 创意栏主标签页注册（显示在战斗标签之前）
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register(
        "minearchive_tab",
        () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.minearchive_tab"))
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                if (NINA != null) output.accept(NINA.get());
                if (LU != null) output.accept(LU.get());
                if (GNT != null) output.accept(GNT.get());
            })
            .build()
    );

    // 第二个创意栏标签页（显示在主标签之后）
    public static final RegistryObject<CreativeModeTab> SECOND_TAB = CREATIVE_MODE_TABS.register(
        "minearchive_tab_2",
        () -> CreativeModeTab.builder()
            .withTabsAfter(EXAMPLE_TAB.getId())
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.minearchive_tab_2"))
            .displayItems((parameters, output) -> {
                // 创建附魔书并添加到标签页
                ItemStack enchantedBook1 = EnchantedBookItem.createForEnchantment(
                    new EnchantmentInstance(CAT_ATTRACTION.get(), 1)
                );
                output.accept(enchantedBook1);

                // 普通书本附魔示例
                ItemStack vanillaBook = new ItemStack(Items.ENCHANTED_BOOK);
                vanillaBook.enchant(CAT_ATTRACTION.get(), 1);
                output.accept(vanillaBook);
            })
            .build()
    );

    /**
     * 向创意栏特定标签页添加物品
     * @param event 创意栏构建事件
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 将方块物品添加到建筑方块标签页
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    /**
     * 服务器启动事件处理
     * @param event 服务器启动事件对象
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    /**
     * 玩家tick事件处理，检测玩家生命值并触发特殊效果
     * @param event 玩家tick事件对象
     */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            // 检测玩家是否持有kayoko物品
            if (kayoko.isPlayerHoldingKayoko(event.player)) {
                // 生命值低于5时触发图腾效果
                if (event.player.getHealth() <= 5.0F) {
                    kayoko.triggerTotemEffect(event.player);
                }
            }
        }
    }

    /**
     * 客户端专用事件处理类
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        /**
         * 客户端初始化方法
         * @param event 客户端设置事件对象
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
    public void onServerTick(TickEvent.ClientTickEvent event){

    }
    // 未使用的MBean支持类（可能为遗留代码）
    private static class MBeanSupport {
        public void register(String catatt, Object aNew) {
        }
    }
    // 在文件末尾，最后一个类（MBeanSupport）之后添加以下内容

    /**
     * 客户端血量监控器 - 每1秒检查所有可见玩家的血量
     * 仅在客户端运行，不会影响服务端
     */
    /*@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientHealthMonitor {

        private static int tickCounter = 0;
        private static final int CHECK_INTERVAL_TICKS = 20; // 20 ticks = 1秒

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            // 只在 Phase.END 执行，避免重复
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            tickCounter++;
            if (tickCounter >= CHECK_INTERVAL_TICKS) {
                checkAllPlayersHealth();
                tickCounter = 0;
            }
        }

       /* private static void checkAllPlayersHealth() {
            Minecraft mc = Minecraft.getInstance();

            // 安全检查：确保游戏和玩家存在
            if (mc == null || mc.level == null || mc.player == null) {
                return;
            }

            var players = mc.level.players();
            if (players.isEmpty()) {
                return;
            }

            // 控制台输出血量信息
            LOGGER.info("========== 玩家血量检查 ==========");
            for (var player : players) {
                String name = player.getName().getString();
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                boolean isLocal = (player == mc.player);

                String prefix = isLocal ? "[本地] " : "[其他] ";
                LOGGER.info("{} {}: {}/{} HP", prefix, name, health, maxHealth);
            }
        }
    }*/
}
