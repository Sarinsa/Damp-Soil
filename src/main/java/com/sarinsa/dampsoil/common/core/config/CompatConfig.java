package com.sarinsa.dampsoil.common.core.config;

import com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.collection.EntityMapField;
import fathertoast.crust.api.config.common.value.collection.EntityMap;
import fathertoast.crust.api.config.common.value.collection.value.ArrayValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.EnumValueCodec;
import net.minecraft.world.entity.EntityType;

import static com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable.*;

@SuppressWarnings( "UnstableApiUsage" )
public class CompatConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final SereneSeasons SERENE_SEASONS;
    public final ToughAsNails TOUGH_AS_NAILS;
    
    /** Builds the config spec that should be used for this config. */
    CompatConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains compatibility options for various mods."
        );
        SPEC.comment(
                "NOTE! Every category in this config file (except 'general') is associated with a mod that Damp Soil adds compatibility features for.",
                "If for example you edit the 'Serene Seasons' category without having the 'Serene Seasons' mod installed, nothing will happen."
        );
        SPEC.newLine( 2 );
        
        GENERAL = new General( this );
        SERENE_SEASONS = new SereneSeasons( this );
        TOUGH_AS_NAILS = new ToughAsNails( this );
    }
    
    
    public static class General extends AbstractConfigCategory<CompatConfig> {
        
        public final BooleanField sprinklerRequiresPiping;
        
        General( CompatConfig parent ) {
            super( parent, "general",
                    "Farmland compatibility options that are not associated with any specific mods." );
            
            sprinklerRequiresPiping = SPEC.define( new BooleanField( "sprinkler.requires_piping", true,
                    "If enabled, sprinklers will need to be hooked up with fluid pipes to function." ) );
        }
    }
    
    public static class SereneSeasons extends AbstractConfigCategory<CompatConfig> {
        
        public final BooleanField seasonalBreeding;
        public final EntityMapField<SeasonRepresentable[]> breedingSeasonsList;
        
        SereneSeasons( CompatConfig parent ) {
            super( parent, "serene_seasons",
                    "Options related to compatibility and additional functionality with the Serene Seasons mod." );
            
            seasonalBreeding = SPEC.define( new BooleanField( "breeding_seasons.enabled", true,
                    "If enabled, animals can only breed during their configured mating season(s), as specified by the field below." ) );
            
            breedingSeasonsList = SPEC.define( new EntityMapField<>( "breeding_seasons.list", createDefaultBreedingSeasonsList(),
                    "A list of entities that can only breed during specific seasons.",
                    "Additional values after the entity key should be the names of the valid breeding seasons.",
                    "There are 4 main seasons to pick from (SPRING, SUMMER, AUTUMN and WINTER), with 3 sub-seasons each (EARLY, MID, LATE)." ) );
        }
        
        // TODO - Include more vanilla animals
        private EntityMap<SeasonRepresentable[]> createDefaultBreedingSeasonsList() {
            return new EntityMap.Builder<>( ArrayValueCodec.of( 0, SeasonRepresentable.class, EnumValueCodec.of( SeasonRepresentable.EARLY_SPRING ) ) )
                    .put( EntityType.COW, new SeasonRepresentable[] { MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER, EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN } )
                    .put( EntityType.PIG, new SeasonRepresentable[] { LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER } )
                    .put( EntityType.CHICKEN, new SeasonRepresentable[] { EARLY_SPRING, MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER } )
                    .put( EntityType.SHEEP, new SeasonRepresentable[] { EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN } )
                    .put( EntityType.RABBIT, new SeasonRepresentable[] { EARLY_SPRING, MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER, EARLY_AUTUMN } )
                    .put( EntityType.HORSE, new SeasonRepresentable[] { MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER } )
                    .build();
        }
    }
    
    public static class ToughAsNails extends AbstractConfigCategory<CompatConfig> {
        
        public final BooleanField sprinklerCoolsPlayers;
        
        ToughAsNails( CompatConfig parent ) {
            super( parent, "tough_as_nails",
                    "Options related to compatibility and additional functionality with the Tough As Nails mod." );
            
            sprinklerCoolsPlayers = SPEC.define( new BooleanField( "sprinkler.cools_players", true,
                    "If enabled, active sprinklers will cool down players standing within its effective area." ) );
        }
    }
}
