package minearchive;
import com.item.catatt;
import net.minecraft.world.item.Item;
import com.item.goon;
import com.item.kayoko;
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

import static com.item.catatt.CatAttractionHandler.CAT_ATTRACTION;

@Mod(minearchive.MODID)
public class minearchive {
        // Define mod id in a common place for everything to reference
        public static final String MODID = "minearchive";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "untitled1" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "untitled1" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("kayoko_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("kayoko_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> EXAMPLE_ITEM
            = ITEMS.register("kayoko", kayoko::new);
    public static final RegistryObject<Item> NINA
            = ITEMS.register("hina", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> LU
            = ITEMS.register("lu", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> GNT
            = ITEMS.register("goon", goon::new);

    public minearchive() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        catatt.CatAttractionHandler.ENCHANTMENTS.register(modEventBus);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
    }

    private static Item get() {
        return new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build()));
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));



    }
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
                        if (LU != null) {
                            output.accept(LU.get());
                        }
                        if (GNT != null) {
                            output.accept(GNT.get());
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
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            if (kayoko.isPlayerHoldingKayoko(event.player)) {
                if (event.player.getHealth() <= 5.0F) { // 生命值低于5时触发
                    kayoko.triggerTotemEffect(event.player);
                }
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    private static class MBeanSupport {
        public void register(String catatt, Object aNew) {
        }
    }
}
