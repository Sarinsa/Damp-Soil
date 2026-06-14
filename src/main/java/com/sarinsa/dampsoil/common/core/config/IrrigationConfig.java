package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.value.environment.biome.BiomeTemperatureEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionPropertyEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.PositionEnvironment;
import fathertoast.crust.api.config.common.value.environment.time.DayTimeEnvironment;
import fathertoast.crust.api.config.common.value.environment.time.WeatherEnvironment;
import net.minecraft.world.level.block.FarmBlock;

import java.util.List;

public class IrrigationConfig extends AbstractConfigFile {
    
    public final Farmland FARMLAND;
    public final Sprinklers SPRINKLERS;
    
    /** Builds the config spec that should be used for this config. */
    IrrigationConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options related to irrigation."
        );
        
        SPEC.fileOnlyNewLine();
        SPEC.describeEnvironmentListPart1of2();
        SPEC.fileOnlyNewLine();
        
        FARMLAND = new Farmland( this );
        SPRINKLERS = new Sprinklers( this );
        
        SPEC.fileOnlyNewLine();
        SPEC.describeEnvironmentListPart2of2();
        SPEC.fileOnlyNewLine();
    }
    
    public static class Farmland extends AbstractConfigCategory<IrrigationConfig> {
        
        public final BooleanField maxMoistureOnTill;
        
        public final IntField waterRange;
        public final IntField waterMoisture;
        
        public final DoubleField dryingChance;
        
        public final EnvironmentListField vaporizeConditions;
        public final IntField vaporizeDelay;
        
        public final BooleanField denyTrampling;
        public final BooleanField canFreeze;
        
        
        Farmland( IrrigationConfig parent ) {
            super( parent, "farmland",
                    "Options related to farmland blocks." );
            
            maxMoistureOnTill = SPEC.define( new BooleanField( "max_moisture_on_till", true,
                    "If enabled, tilling dirt will always result in max moisture farmland." ) );
            
            SPEC.newLine();
            
            waterRange = SPEC.define( new IntField( "water.range", 1, 0, 15,
                    "Determines the effective radius of water blocks to moisturize nearby farmland.",
                    "Can be set to 0 to completely stop water sources from moisturizing farmland. 4 is the vanilla Minecraft value." ) );
            
            waterMoisture = SPEC.define( new IntField( "water.moisture", 4, 1, FarmBlock.MAX_MOISTURE,
                    "Determines the moisture level farmland blocks can get from nearby water blocks.",
                    "0 is minimum moisture level (dry) and " + FarmBlock.MAX_MOISTURE + " is maximum moisture level." ) );
            
            SPEC.newLine();
            
            dryingChance = SPEC.define( new DoubleField( "drying_chance", 0.05, DoubleField.Range.PERCENT,
                    "Determines the chance for farmland to lose moisture on random tick." ) );
            
            SPEC.newLine();
            
            vaporizeConditions = SPEC.define( new EnvironmentListField( "vaporize.conditions", createDefaultVaporizeConditions(),
                    "A list of environment conditions that result in farmland losing moisture much quicker.",
                    "By default this includes being in an ultrawarm dimension or being exposed to direct sunlight in a hot biome." ) );
            
            vaporizeDelay = SPEC.define( new IntField( "vaporize.delay", 30, 1, 1000,
                    "The delay (in ticks) between each vaporization tick" ) );
            
            SPEC.newLine();
            
            denyTrampling = SPEC.define( new BooleanField( "deny_trampling", true,
                    "If enabled, wet farmland will not be trampled if jumped on by players or mobs." ) );
            
            canFreeze = SPEC.define( new BooleanField( "can_freeze", true,
                    "If enabled, wet farmland will freeze in cold temperatures." ) );
        }
        
        private static EnvironmentList createDefaultVaporizeConditions() {
            return new EnvironmentList(
                    new EnvironmentEntry( 1.0, List.of(
                            new BiomeTemperatureEnvironment( ComparisonOperator.GREATER_OR_EQUAL, 2.0F ),
                            new PositionEnvironment( PositionEnvironment.Value.CAN_SEE_SKY, false ),
                            new DayTimeEnvironment( DayTimeEnvironment.Value.DAY, false ),
                            new WeatherEnvironment( WeatherEnvironment.Value.CLEAR, false )
                    ) ),
                    new EnvironmentEntry( 1.0, new DimensionPropertyEnvironment( DimensionPropertyEnvironment.Value.ULTRAWARM, false ) )
            ).setRange( DoubleField.Range.NON_NEGATIVE );
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
            
            normalRadius = SPEC.define( new IntField( "radius.normal", 2, 1, 10,
                    "Determines the radius of normal sprinklers' AoE." ) );
            
            netheriteRadius = SPEC.define( new IntField( "radius.netherite", 4, 1, 10,
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
