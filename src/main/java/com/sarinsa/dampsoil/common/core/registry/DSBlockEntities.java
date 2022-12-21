package com.sarinsa.dampsoil.common.core.registry;

import com.sarinsa.dampsoil.common.core.DampSoil;
import com.sarinsa.dampsoil.common.tile.SprinklerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings({"ConstantConditions", "SameParameterValue"})
public class DSBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DampSoil.MODID);


    public static final RegistryObject<BlockEntityType<SprinklerBlockEntity>> SPRINKLER = register("sprinkler", () ->
            BlockEntityType.Builder.of(SprinklerBlockEntity::new, new Block[] {DSBlocks.SPRINKLER.get(), DSBlocks.NETHERITE_SPRINKLER.get()}).build(null));


    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> supplier) {
        return TILE_ENTITIES.register(name, supplier);
    }
}
