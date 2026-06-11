package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;

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
        
        public final DoubleField boneMealChance;
        public final DoubleField growthChance;
        
        public final BooleanField useMoistureMult;
        
        public final BooleanField killDryCrops;
        
        General( CropsConfig parent ) {
            super( parent, "general",
                    "Farmland options related to crops." );
            
            boneMealChance = SPEC.define( new DoubleField( "bone_meal.chance", 0.35, DoubleField.Range.PERCENT,
                    "Determines the chance for a crop to grow when using bone meal on it.",
                    "Setting this to 0 effectively makes bone meal not work on crops at all." ) );
            
            growthChance = SPEC.define( new DoubleField( "growth_chance", 0.3, DoubleField.Range.PERCENT,
                    "Determines the chance for a crop to grow when it is normally supposed to.",
                    "This setting can in other words be used to effectively slow down the normal growth rate of crops." ) );
            
            SPEC.newLine();
            
            // TODO - make this make sense
            useMoistureMult = SPEC.define( new BooleanField( "moisture_multiplier", true,
                    "If enabled, a crop's growth chance will be affected by the moisture level of the farmland it grows on.",
                    "The lower the moisture level, the lower the growth chance gets.",
                    "The exact algorithm is as follows:",
                    "growth chance = clamp( 0.0, 1.0, moisture / 'growth_chance' )" ) );
            
            SPEC.newLine();
            
            killDryCrops = SPEC.define( new BooleanField( "kill_dry_crops", true,
                    "If enabled, crops in completely dry farmland will shrivel and die." ) );
        }
    }
}
