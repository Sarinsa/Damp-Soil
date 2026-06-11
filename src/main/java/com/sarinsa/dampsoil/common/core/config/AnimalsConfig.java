package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.IntField;

public class AnimalsConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final Produce PRODUCE;
    
    
    /** Builds the config spec that should be used for this config. */
    AnimalsConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options related to animals."
        );
        
        GENERAL = new General( this );
        PRODUCE = new Produce( this );
    }
    
    
    public static class General extends AbstractConfigCategory<AnimalsConfig> {
        
        public final BooleanField denyBabyFeeding;
        
        General( AnimalsConfig parent ) {
            super( parent, "general",
                    "Farmland options related to animals." );
            
            denyBabyFeeding = SPEC.define( new BooleanField( "babies.deny_feeding", true,
                    "If enabled, babies cannot be fed to speed up their growth." ) );
        }
    }
    
    public static class Produce extends AbstractConfigCategory<AnimalsConfig> {
        
        public final IntField.RandomRange chickenEggCooldown;
        
        public final IntField.RandomRange cowMilkCooldown;
        
        public final IntField.RandomRange mooshroomStewCooldown;
        
        
        Produce( AnimalsConfig parent ) {
            super( parent, "produce",
                    "Options related animal produce and cooldowns." );
            
            chickenEggCooldown = new IntField.RandomRange( SPEC, "chicken.egg_cooldown", 10000, 14000, 20, Integer.MAX_VALUE,
                    "The minimum and maximum (inclusive) cooldown in ticks to put chickens on after laying an egg." );
            
            SPEC.newLine();
            
            cowMilkCooldown = new IntField.RandomRange( SPEC, "cow.milk_cooldown", 200, 500, 0, Integer.MAX_VALUE,
                    "The minimum and maximum (inclusive) cooldown in ticks to put cows on after they have been milked." );
            
            SPEC.newLine();
            
            mooshroomStewCooldown = new IntField.RandomRange( SPEC, "mooshroom.stew_cooldown", 200, 500, 0, Integer.MAX_VALUE,
                    "The minimum and maximum (inclusive) cooldown in ticks to put put mooshrooms on after they have been \"milked\"" );
        }
    }
}
