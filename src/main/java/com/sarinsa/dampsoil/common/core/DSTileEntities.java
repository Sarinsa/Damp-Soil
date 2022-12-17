package com.sarinsa.dampsoil.common.core;

import com.sarinsa.dampsoil.common.tile.SprinklerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings({"ConstantConditions", "SameParameterValue"})
public class DSTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DampSoil.MODID);


    public static final RegistryObject<TileEntityType<SprinklerTileEntity>> SPRINKLER = register("sprinkler", () ->
            TileEntityType.Builder.of(SprinklerTileEntity::new, new Block[] {DSBlocks.SPRINKLER.get()}).build(null));


    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<TileEntityType<T>> supplier) {
        return TILE_ENTITIES.register(name, supplier);
    }
}
