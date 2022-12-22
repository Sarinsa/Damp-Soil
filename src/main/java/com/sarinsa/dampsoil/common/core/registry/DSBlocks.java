package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.block.DeadCropBlock;
import com.sarinsa.dampsoil.common.block.FrozenFarmBlock;
import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.sarinsa.dampsoil.common.core.config.DSCommonConfig.COMMON;

public class DSBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DampSoil.MODID);


    public static final RegistryObject<Block> SPRINKLER = register("sprinkler", () -> new SprinklerBlock(COMMON.sprinklerRadius), CreativeModeTab.TAB_REDSTONE);
    public static final RegistryObject<Block> NETHERITE_SPRINKLER = register("netherite_sprinkler", () -> new SprinklerBlock(COMMON.netheriteSprinklerRadius), CreativeModeTab.TAB_REDSTONE);
    public static final RegistryObject<Block> DEAD_CROP = registerNoBlockItem("dead_crop", DeadCropBlock::new);
    public static final RegistryObject<Block> FROZEN_FARMLAND = register("frozen_farmland", FrozenFarmBlock::new, CreativeModeTab.TAB_DECORATIONS);


    private static <T extends Block> RegistryObject<T> registerNoBlockItem(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier, @Nullable CreativeModeTab creativeTab) {
        RegistryObject<T> regObj = BLOCKS.register(name, supplier);
        DSItems.register(name, () -> new BlockItem(regObj.get(), new Item.Properties().tab(creativeTab)));
        return regObj;
    }
}
