package com.sarinsa.dampsoil.common.core.config;

import com.sarinsa.dampsoil.common.core.config.environment.SeasonalWeatherEnvironment;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.collection.value.BooleanValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentList;
import fathertoast.crust.api.config.common.value.environment.biome.BiomeTemperatureEnvironment;
import net.minecraft.world.level.block.FarmBlock;

public class IrrigationConfig extends AbstractConfigFile {
    
    public final Farmland FARMLAND;
    public final Sprinklers SPRINKLERS;
    
    /** Builds the config spec that should be used for this config. */
    IrrigationConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName, false,
                "This config contains options related to irrigation."
        );
        EnvironmentListField.describe1of2( SPEC );
        
        FARMLAND = new Farmland( this );
        SPRINKLERS = new Sprinklers( this );
        
        EnvironmentListField.describe2of2( SPEC );
    }
    
    public static class Farmland extends AbstractConfigCategory<IrrigationConfig> {
        
        public final IntField waterRange;
        public final IntField waterMoisture;
        
        public final EnvironmentListField<Boolean> vaporizeConditions;
        public final IntField vaporizeDelay;
        
        public final EnvironmentListField<Boolean> freezeConditions;
        
        public final DoubleField dryingChance;
        public final BooleanField maxMoistureOnTill;
        public final BooleanField denyTrampling;
        
        
        Farmland( IrrigationConfig parent ) {
            super( parent, "farmland",
                    "Options related to farmland blocks." );
            
            waterRange = SPEC.define( new IntField( "water.range", 1, 0, 15,
                    "Determines the effective radius of water blocks to moisturize nearby farmland.",
                    "Can be set to 0 to completely stop water sources from moisturizing farmland. 4 is the vanilla Minecraft value." ) );
            
            waterMoisture = SPEC.define( new IntField( "water.moisture", 4, 1, FarmBlock.MAX_MOISTURE,
                    "Determines the moisture level farmland blocks can get from nearby water blocks.",
                    "0 is minimum moisture level (dry) and " + FarmBlock.MAX_MOISTURE + " is maximum moisture level." ) );
            
            SPEC.newLine();
            
            vaporizeConditions = SPEC.define( new EnvironmentListField<>( "vaporize.conditions", createDefaultVaporizeConditions(),
                    "A list of environment conditions that make farmland lose moisture rapidly when met." ) );
            
            vaporizeDelay = SPEC.define( new IntField( "vaporize.delay", 45, 1, 1000,
                    "The delay (in ticks) between each vaporization tick" ) );
            
            SPEC.newLine();
            
            freezeConditions = SPEC.define( new EnvironmentListField<>( "freeze.conditions", createDefaultFreezeConditions(),
                    "A list of environment conditions that make farmland freeze when met.",
                    "By default, temperature must be 0, " ) );
            
            SPEC.newLine();
            
            dryingChance = SPEC.define( new DoubleField( "drying_chance", 0.05, DoubleField.Range.PERCENT,
                    "Determines the chance for farmland to lose moisture on random tick." ) );
            
            maxMoistureOnTill = SPEC.define( new BooleanField( "max_moisture_on_till", true,
                    "If enabled, tilling dirt will always result in max moisture farmland." ) );
            
            denyTrampling = SPEC.define( new BooleanField( "deny_trampling", true,
                    "If enabled, farmland can not be trampled by entities." ) );
        }
        
        private static EnvironmentList<Boolean> createDefaultVaporizeConditions() {
            return EnvironmentList.builder( BooleanValueCodec.DEFAULT_FALSE )
                    // Evaporate in ultra warm dimensions
                    .entryBuilder( true )
                    .inUltraWarmDimension().or()
                    // Otherwise check temperature, weather condition, time of day and if sky is visible
                    .in( new BiomeTemperatureEnvironment( ComparatorValue.GREATER_OR_EQUAL, 2.0F ) ).and()
                    .canSeeSky().and()
                    .isDay().and()
                    .isNotRaining().build().build();
        }
        
        private static EnvironmentList<Boolean> createDefaultFreezeConditions() {
            return EnvironmentList.builder( BooleanValueCodec.DEFAULT_FALSE )
                    // Evaporate in ultra warm dimensions
                    .entryBuilder( true )
                    .in( new SeasonalWeatherEnvironment( SeasonalWeatherEnvironment.Value.CAN_SNOW, false ) ).and()
                    .belowBlockLight( 12 ).build().build();
        }
    }
    
    public static class Sprinklers extends AbstractConfigCategory<IrrigationConfig> {
        
        public final BooleanField worksInUltrawarm;
        
        public final IntField activeDuration;
        
        public final IntField normalRadius;
        public final IntField netheriteRadius;
        
        public final BooleanField hurtsWaterSensitive;
        public final BooleanField extinguishEntities;
        
        
        Sprinklers( IrrigationConfig parent ) {
            super( parent, "sprinklers",
                    "Options related to sprinklers." );
            
            worksInUltrawarm = SPEC.define( new BooleanField( "works_in_ultrawarm", false,
                    "If true, sprinklers will function normally in ultrawarm dimensions such as the nether.",
                    "By default, sprinklers will just let out some sad steam and do nothing in ultrawarm dimensions." ) );
            
            SPEC.newLine();
            
            activeDuration = SPEC.define( new IntField( "active_duration", 300, IntField.Range.POSITIVE,
                    "The amount of ticks a sprinkler stays active for after being activated." ) );
            
            SPEC.newLine();
            
            normalRadius = SPEC.define( new IntField( "radius.normal", 2, 1, 50,
                    "Determines the radius of normal sprinklers' AoE." ) );
            
            netheriteRadius = SPEC.define( new IntField( "radius.netherite", 5, 1, 50,
                    "Determines the radius of Netherite sprinklers' AoE." ) );
            
            SPEC.newLine();
            
            hurtsWaterSensitive = SPEC.define( new BooleanField( "hurts_water_sensitive", true,
                    "If enabled, water sensitive entities (such as blazes) will " +
                            "take damage when inside an active sprinkler's effective area." ) );
            
            extinguishEntities = SPEC.define( new BooleanField( "extinguish_entities", true,
                    "If enabled, burning entities will be extinguished when inside an active sprinkler's effective area." ) );
        }
    }
}
