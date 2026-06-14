package com.sarinsa.dampsoil.common.datagen.tag;

import com.sarinsa.dampsoil.common.core.DampSoil;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.tag.DampSoilTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    
    public ModBlockTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper ) {
        super( output, lookupProvider, DampSoil.MODID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
        tag( DampSoilTags.Blocks.DEAD_CROP_MAY_PLACE_ON ).add(
                Blocks.FARMLAND,
                Blocks.GRASS_BLOCK,
                Blocks.DIRT,
                Blocks.SAND,
                Blocks.COARSE_DIRT,
                Blocks.PODZOL
        );
        
        tag( BlockTags.MINEABLE_WITH_PICKAXE ).add(
                DSBlocks.FROZEN_FARMLAND.get(),
                DSBlocks.SPRINKLER.get(),
                DSBlocks.NETHERITE_SPRINKLER.get()
        );
        
        tag( BlockTags.NEEDS_STONE_TOOL ).add(
                DSBlocks.SPRINKLER.get()
        );
        
        tag( BlockTags.NEEDS_DIAMOND_TOOL ).add(
                DSBlocks.NETHERITE_SPRINKLER.get()
        );
    }
}
