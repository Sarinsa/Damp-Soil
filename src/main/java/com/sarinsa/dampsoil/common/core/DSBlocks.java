package com.sarinsa.dampsoil.common.core;

import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DSBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DampSoil.MODID);


    public static final RegistryObject<Block> SPRINKLER = register("sprinkler", SprinklerBlock::new, ItemGroup.TAB_REDSTONE);


    private static <T extends Block> RegistryObject<T> registerNoBlockItem(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier, ItemGroup itemGroup) {
        RegistryObject<T> regObj = BLOCKS.register(name, supplier);
        DSItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties().tab(itemGroup)));
        return regObj;
    }
}
