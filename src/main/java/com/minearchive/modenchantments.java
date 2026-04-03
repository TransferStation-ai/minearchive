package minearchive;

import com.item.catatt;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class modenchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "minearchive"); // "minearchive" 是模组ID

    public static final RegistryObject<Enchantment> CAT_ATTRACTION =
            ENCHANTMENTS.register("cat_attraction", () -> new catatt()); // 假设 catatt 是自定义附魔类
}