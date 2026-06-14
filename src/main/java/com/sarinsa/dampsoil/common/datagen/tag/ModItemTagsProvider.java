package com.sarinsa.dampsoil.common.datagen.tag;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    
    public ModItemTagsProvider( PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider,
                                CompletableFuture<TagLookup<Block>> lookup, @Nullable ExistingFileHelper fileHelper ) {
        super( packOutput, provider, lookup, DampSoil.MODID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
    
    }
}
