package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.collection.BlockStateMapField;
import fathertoast.crust.api.config.common.value.collection.BlockStateMap;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings( "UnstableApiUsage" )
public class CropsConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    CropsConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options that apply to crops."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<CropsConfig> {
        
        public final BlockStateMapField<Double> boneMealChances;
        
        public final DoubleField growthChance;
        public final BooleanField useMoistureMult;
        
        public final BooleanField killDryCrops;
        
        General( CropsConfig parent ) {
            super( parent, "general",
                    "Farmland options related to crops." );
            
            boneMealChances = SPEC.define( new BlockStateMapField<>( "bone_meal_chance.list", createDefaultBoneMealChances(),
                    "A list of blocks that are considered crops (or just growable with bone meal).",
                    "The additional value after the block state key is the chance for bone meal to grow the crop.",
                    "In other words, this list can be used to effectively reduce bone meal effectiveness on specific crops." ) );
            
            SPEC.newLine();
            
            growthChance = SPEC.define( new DoubleField( "growth_chance", 0.3, DoubleField.Range.PERCENT,
                    "Determines the chance for a crop to grow naturally (on random tick).",
                    "This setting can in other words be used to slow down the normal growth rate of crops.",
                    "Note that this only does anything for blocks that fire the Forge BlockEvent.CropGrowEvent.Pre event." ) );
            
            useMoistureMult = SPEC.define( new BooleanField( "moisture_multiplier", true,
                    "If enabled, a crop's growth chance changes depending on the moisture level in the farmland it is planted on.",
                    "The lower the moisture level, the lower the growth chance gets, coming to a complete reduction of 0% if the soil is completely dry.",
                    "The exact algorithm is as follows:",
                    "final growth chance = ((1.0 / max moisture) * current moisture) * growth chance" ) );
            
            SPEC.newLine();
            
            killDryCrops = SPEC.define( new BooleanField( "kill_dry_crops", true,
                    "If enabled, crops in completely dry farmland will shrivel and die." ) );
        }
        
        private static BlockStateMap<Double> createDefaultBoneMealChances() {
            return new BlockStateMap.Builder<>( DoubleValueCodec.PERCENT )
                    .putTag( BlockTags.CROPS, BlockStatePropertyMap.EMPTY, 0.35 )
                    .put( Blocks.MELON_STEM, BlockStatePropertyMap.EMPTY, 0.2 )
                    .put( Blocks.PUMPKIN_STEM, BlockStatePropertyMap.EMPTY, 0.2 )
                    .build();
        }
    }
}
