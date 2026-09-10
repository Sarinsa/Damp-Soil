package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.collection.BlockStateMapField;
import fathertoast.crust.api.config.common.field.collection.BlockStateSetField;
import fathertoast.crust.api.config.common.value.collection.BlockStateMap;
import fathertoast.crust.api.config.common.value.collection.BlockStateSet;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public class CropsConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    CropsConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName, false,
                "This config contains options that apply to crops."
        );
        BlockStateMapField.describe( SPEC );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<CropsConfig> {
        
        public final BlockStateMapField<Double> boneMealChances;
        public final BlockStateMapField<Double> growthChances;
        public final BlockStateSetField diesWithoutWater;
        
        public final BooleanField useMoistureMult;
        
        General( CropsConfig parent ) {
            super( parent, "general",
                    "Farmland options related to crops." );
            
            boneMealChances = SPEC.define( new BlockStateMapField<>( "bone_meal_chances", createDefaultBoneMealChances(),
                    "A list of blocks that can have bone meal used on them by players, such as crops or saplings.",
                    "The additional value after the block state key is the chance for bone meal to successfully grow the block." ) );
            
            growthChances = SPEC.define( new BlockStateMapField<>( "growth_chances", createDefaultGrowthChances(),
                    "A list of blocks that grow naturally, such as crops or saplings.",
                    "The additional value after the block state key is the chance for the block to grow when randomly ticked.",
                    "In other words, this list can be used to effectively reduce growth rate for crops etc.",
                    "Note that this only does anything for blocks that fires Forge's BlockEvent.CropGrowEvent.Pre event " +
                            "or SaplingGrowTreeEvent." ) );
            
            SPEC.newLine();
            
            diesWithoutWater = SPEC.define( new BlockStateSetField( "dies_without_water.list", createDefaultDiesWithoutWater(),
                    "A list of crop blocks that shrivel up and die when their soil dries up.",
                    "Note that this only does anything for blocks that fire the Forge BlockEvent.CropGrowEvent.Pre event.",
                    "It is also important to note that Damp Soil can currently only read the moisture level of farmland blocks." ) );
            
            SPEC.newLine();
            
            useMoistureMult = SPEC.define( new BooleanField( "moisture_multiplier", true,
                    "If enabled, a crop's growth chance changes depending on the moisture level in the farmland it is planted on.",
                    "The lower the moisture level, the lower the growth chance gets, coming to a complete reduction of 100% if the soil is completely dry.",
                    "The exact algorithm is as follows:",
                    ChatFormatting.AQUA + "chance = ((1.0 / max moisture) * current moisture) * growth chance" ) );
        }
        
        private static BlockStateMap<Double> createDefaultBoneMealChances() {
            return new BlockStateMap.Builder<>( DoubleValueCodec.PERCENT )
                    .putTag( BlockTags.CROPS, 0.35 )
                    .put( Blocks.MELON_STEM, 0.15 )
                    .put( Blocks.PUMPKIN_STEM, 0.15 )
                    .buildWithDefault( 0.25 );
        }
        
        private static BlockStateMap<Double> createDefaultGrowthChances() {
            return new BlockStateMap.Builder<>( DoubleValueCodec.PERCENT )
                    .putTag( BlockTags.CROPS, 0.2 )
                    .putTag( BlockTags.SAPLINGS, 0.05 )
                    .put( Blocks.SUGAR_CANE, 0.07 )
                    .put( Blocks.MELON_STEM, 0.1 )
                    .put( Blocks.PUMPKIN_STEM, 0.1 )
                    .put( Blocks.COCOA, 0.15 )
                    .put( Blocks.SWEET_BERRY_BUSH, 0.3 )
                    .put( Blocks.CACTUS, 0.05 )
                    .put( Blocks.NETHER_WART, 0.03 )
                    .buildWithDefault( 0.25 );
        }
        
        private static BlockStateSet createDefaultDiesWithoutWater() {
            return new BlockStateSet.Builder<>()
                    .addTag( BlockTags.CROPS )
                    .addTag( BlockTags.SAPLINGS )
                    .build();
        }
    }
}
