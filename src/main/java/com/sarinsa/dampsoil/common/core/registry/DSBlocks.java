package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.block.DeadCropBlock;
import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.sarinsa.dampsoil.common.core.config.DSCommonConfig.COMMON;

public class DSBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DampSoil.MODID);


    public static final RegistryObject<Block> SPRINKLER = register("sprinkler", () -> new SprinklerBlock(COMMON.sprinklerRadius::get), ItemGroup.TAB_REDSTONE);
    public static final RegistryObject<Block> NETHERITE_SPRINKLER = register("netherite_sprinkler", () -> new SprinklerBlock(COMMON.netheriteSprinklerRadius::get), ItemGroup.TAB_REDSTONE);
    public static final RegistryObject<Block> DEAD_CROP = registerNoBlockItem("dead_crop", DeadCropBlock::new);


    private static <T extends Block> RegistryObject<T> registerNoBlockItem(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier, @Nullable ItemGroup itemGroup) {
        RegistryObject<T> regObj = BLOCKS.register(name, supplier);
        DSItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties().tab(itemGroup)));
        return regObj;
    }
}
