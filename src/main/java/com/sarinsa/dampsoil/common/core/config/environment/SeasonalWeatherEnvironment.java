package com.sarinsa.dampsoil.common.core.config.environment;

import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.PredicateEnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.season.SeasonHooks;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * This environment utilizes Serene Season's hooks to check various weather conditions.
 * If Serene Seasons is not installed, this environment fallbacks to vanilla equivalents.
 * <br><br>
 * When position is not available, this environment always evaluates as false.
 */
public class SeasonalWeatherEnvironment extends PredicateEnumEnvironment<SeasonalWeatherEnvironment.Value> {
    
    @SuppressWarnings( "DataFlowIssue" ) // We ensure context.getBlockPos() != null prior to testing any of these
    public enum Value implements Predicate<EnvironmentContext> {
        CAN_SNOW( context -> {
            final BlockPos pos = context.getBlockPos();
            if( SereneSeasonsHelper.isModLoaded() ) {
                return SeasonHooks.coldEnoughToSnowSeasonal( context.getLevel(), pos );
            }
            else {
                return context.getLevel().getBiome( pos ).get().coldEnoughToSnow( pos );
            }
        } ),
        HAS_PRECIPITATION( context -> {
            final BlockPos pos = context.getBlockPos();
            final LevelAccessor level = context.getLevel();
            if( SereneSeasonsHelper.isModLoaded() && context.getWorldGenLevel() == null ) {
                return SeasonHooks.hasPrecipitationSeasonal( context.getFullLevel(), level.getBiome( pos ) );
            }
            else {
                return level.getBiome( pos ).get().getPrecipitationAt( pos ) != Biome.Precipitation.NONE;
            }
        } );
        
        private final Predicate<EnvironmentContext> PREDICATE;
        
        Value( Predicate<EnvironmentContext> supplier ) { PREDICATE = supplier; }
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) {
            return context.getBlockPos() != null && PREDICATE.test( context );
        }
    }
    
    public SeasonalWeatherEnvironment( SeasonalWeatherEnvironment.Value value, boolean invert ) {
        super( value, invert );
    }
    
    public SeasonalWeatherEnvironment( @Nullable IConfigField<?> field, String value ) {
        super( field, value, SeasonalWeatherEnvironment.Value.values() );
    }
}
