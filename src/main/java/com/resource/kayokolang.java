package com.resource;
import com.minearchive.minearchive;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.forgespi.*;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
//关于这东西原本是一个普通类，结果最后被我弄成了抽象类
//实际这个代码没有做任何事情

public class kayokolang implements RepositorySource {
    private static final String LEGACY_PACK_DIR_NAME = "kayoko_universal";
    private static final String PACK_NAME = "minearchive_legacy_resources_pack";
    private final Pack legacyPack;
    private Pack kayokolang;

    public kayokolang(URLClassLoader file, String[] newPaths, Object string, Object strings) {
        this.file = file;
        this.newPaths = newPaths;
        String = string;
        this.strings = strings;
        Pack.ResourcesSupplier supplier = name -> getLegacyPack();

        MutableComponent desc =
                Component.translatable("pack.minearchive.legacy_resources_pack.desc");

        int packFormat =
                SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);

        Pack.Info info = new Pack.Info(desc, packFormat, FeatureFlagSet.of());

        MutableComponent title =
                Component.translatable("pack.minearchive.legacy_resources_pack.title");

        this.legacyPack = Pack.create(
                PACK_NAME,
                title,
                false,
                supplier,
                info,
                PackType.CLIENT_RESOURCES,
                Pack.Position.TOP,
                false,
                PackSource.BUILT_IN
        );
    }

    private PackResources getLegacyPack() {
        IModFile file = ModList.get().getModFileById(minearchive.MODID).getFile();

        return new ModFileResourcePack(file) {

            protected Path resolve(@NotNull String... paths) {
                String[] newPaths = new String[paths.length + 1];
                newPaths[0] = LEGACY_PACK_DIR_NAME;
                System.arraycopy(paths, 0, newPaths, 1, paths.length);
                return file.findResource(newPaths);
            }
        };
    }

    private final ByteBuffer paths = null;

    private final URLClassLoader file;

    private final String[] newPaths;

    private final Object String;

    private final Object strings;


        
    @Contract(pure = true)
    private @Nullable PackResources resolve(String[] strings) {
        return null;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {

    }

    private class ModFileResourcePack implements PackResources {
        public ModFileResourcePack(IModFile file) {
        }

        @Override
        public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
            return null;
        }

        @Override
        public @Nullable IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
            return null;
        }

        @Override
        public void listResources(PackType packType, String s, String s1, ResourceOutput resourceOutput) {

        }

        @Override
        public Set<String> getNamespaces(PackType packType) {
            return Set.of();
        }

        @Override
        public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> metadataSectionSerializer) throws IOException {
            return null;
        }

        @Override
        public String packId() {
            return "";
        }

        @Override
        public void close() {

        }
    }
}
