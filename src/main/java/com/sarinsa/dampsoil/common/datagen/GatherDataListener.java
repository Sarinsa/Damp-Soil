package com.sarinsa.dampsoil.common.datagen;

import com.sarinsa.dampsoil.common.core.DampSoil;
import com.sarinsa.dampsoil.common.datagen.loot.ModLootProvider;
import com.sarinsa.dampsoil.common.datagen.tag.ModBlockTagsProvider;
import com.sarinsa.dampsoil.common.datagen.tag.ModItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber( modid = DampSoil.MODID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class GatherDataListener {
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        final DataGenerator dataGen = event.getGenerator();
        final PackOutput packOutput = dataGen.getPackOutput();
        final ExistingFileHelper fileHelper = event.getExistingFileHelper();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        final BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider( packOutput, lookupProvider, fileHelper );
        
        
        if( event.includeServer() ) {
            dataGen.addProvider( true, new ModLootProvider( dataGen ) );
            dataGen.addProvider( true, blockTagsProvider );
            dataGen.addProvider( true, new ModItemTagsProvider( packOutput, lookupProvider, blockTagsProvider.contentsGetter(), fileHelper ) );
        }
    }
}
