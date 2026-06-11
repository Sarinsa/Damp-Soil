package com.sarinsa.dampsoil.common.compat.glitchfiend;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

/** Helper class containing convenience stuff for Serene Seasons. */
public class SereneSeasonsHelper {
    
    public static final String MODID = "sereneseasons";
    
    
    /** @return True if the current season is winter. */
    public static boolean isWinter( Level level ) { return isSeason( level, Season.WINTER ); }
    
    /** @return True if the current season is spring. */
    public static boolean isSpring( Level level ) { return isSeason( level, Season.SPRING ); }
    
    /** @return True if the current season is summer. */
    public static boolean isSummer( Level level ) { return isSeason( level, Season.SPRING ); }
    
    /** @return True if the current season is autumn. */
    public static boolean isAutumn( Level level ) { return isSeason( level, Season.SPRING ); }
    
    
    /**
     * @return True if the specified season is the current season.
     * Returns false if not, or if Serene Seasons is not installed.
     */
    private static boolean isSeason( Level level, Season seasonState ) {
        if( ModList.get().isLoaded( MODID ) ) {
            return SeasonHelper.getSeasonState( level ).getSeason() == seasonState;
        }
        return false;
    }
    
    /**
     * @return The corresponding {@link Season} enum from Serene Seasons depending on the value
     * of the passed {@link SeasonRepresentable}.
     */
    public static Season.SubSeason getFromSeasonRepresentable( SeasonRepresentable seasonRepresentable ) {
        return switch( seasonRepresentable ) {
            case EARLY_WINTER -> Season.SubSeason.EARLY_WINTER;
            case MID_WINTER -> Season.SubSeason.MID_WINTER;
            case LATE_WINTER -> Season.SubSeason.LATE_WINTER;
            case EARLY_SPRING -> Season.SubSeason.EARLY_SPRING;
            case MID_SPRING -> Season.SubSeason.MID_SPRING;
            case LATE_SPRING -> Season.SubSeason.LATE_SPRING;
            case EARLY_SUMMER -> Season.SubSeason.EARLY_SUMMER;
            case MID_SUMMER -> Season.SubSeason.MID_SUMMER;
            case LATE_SUMMER -> Season.SubSeason.LATE_SUMMER;
            case EARLY_AUTUMN -> Season.SubSeason.EARLY_AUTUMN;
            case MID_AUTUMN -> Season.SubSeason.MID_AUTUMN;
            case LATE_AUTUMN -> Season.SubSeason.LATE_AUTUMN;
        };
    }
}
