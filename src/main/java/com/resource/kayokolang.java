package com.resource;
import com.minearchive.minearchive;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.function.Consumer;

import static com.minearchive.minearchive.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class kayokolang implements RepositorySource {
    //试图把一个资源包给到资源包列表
    private static final String LEGACY_PACK_DIR_NAME = "kayoko_universal";
    private static final String PACK_NAME = "minearchive_legacy_resources_pack";
    private final Pack legacyPack;
    public kayokolang() {
        ResourcesSupplier supplier = name -> getLegacyPack();
        MutableComponent desc = Component.translatable("pack.minearchive.legacy_resources_pack.desc");
        int packFormatVersion = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
        Pack.Info info = new Pack.Info(desc, packFormatVersion, FeatureFlagSet.of());
        MutableComponent title = Component.translatable("pack.minearchive.legacy_resources_pack.title");
        this.legacyPack = Pack.create(PACK_NAME, title, false, supplier, info, PackType.CLIENT_RESOURCES,
                Pack.Position.TOP, false, PackSource.BUILT_IN);
    }
    private @NotNull PathPackResources getLegacyPack() {
        IModFile file = ModList.get().getModFileById(minearchive.MODID).getFile();
        Path root = file.getFilePath().resolve(LEGACY_PACK_DIR_NAME);

        return new PathPackResources(
                file.getFileName(),
                false,
                root
                //纠正了之前可能产生问题的用法
        );
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        consumer.accept(this.legacyPack);
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft.getInstance()
                    .getResourcePackRepository()
                    .addPackFinder(new kayokolang());
            //将原有有问题的方案替换成没有问题的方案。
        });
    }
}