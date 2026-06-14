package com.sarinsa.dampsoil.common.datagen.loot;

import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ModBlockLoot extends BlockLootSubProvider {
    
    private final Set<Block> knownBlocks = new HashSet<>();
    
    protected ModBlockLoot( Set<Item> set, FeatureFlagSet flagSet ) {
        super( set, flagSet );
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
    
    @Override
    protected void add( Block block, LootTable.Builder table ) {
        super.add( block, table );
        knownBlocks.add( block );
    }
    
    @Override
    protected void generate() {
        dropSelf( DSBlocks.SPRINKLER.get() );
        dropSelf( DSBlocks.NETHERITE_SPRINKLER.get() );
        add( DSBlocks.DEAD_CROP.get(), noDrop() );
        dropOther( DSBlocks.FROZEN_FARMLAND.get(), Blocks.DIRT );
    }
}