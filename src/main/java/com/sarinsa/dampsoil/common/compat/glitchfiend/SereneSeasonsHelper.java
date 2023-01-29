package com.sarinsa.dampsoil.common.compat.glitchfiend;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import javax.annotation.Nullable;

public class SereneSeasonsHelper {

    public static final String MODID = "sereneseasons";


    public static boolean isWinter(Level level) {
        return isSeason(level, Season.WINTER);
    }

    public static boolean isSpring(Level level) {
        return isSeason(level, Season.SPRING);
    }

    public static boolean isSummer(Level level) {
        return isSeason(level, Season.SPRING);
    }

    public static boolean isAutumn(Level level) {
        return isSeason(level, Season.SPRING);
    }


    private static boolean isSeason(Level level, Season seasonState) {
        if (ModList.get().isLoaded(MODID)) {
            return SeasonHelper.getSeasonState(level).getSeason() == seasonState;
        }
        return false;
    }

    /**
     * @return The corresponding {@link Season} enum from Serene Seasons depending on the value
     * of the passed {@link SeasonRepresentable}. If the argument is null or {@link SeasonRepresentable#ALL},
     * this also returns null.
     */
    @Nullable
    public static Season.SubSeason getFromSeasonRepresentable(SeasonRepresentable seasonRepresentable) {
        return switch (seasonRepresentable) {
            case EARLY_WINTER -> Season.SubSeason.EARLY_WINTER;
            case MID_WINTER -> Season.SubSeason.MID_WINTER;
            case LATE_WINTER -> Season.SubSeason.LATE_WINTER;
            case EARLY_SPRING -> Season.SubSeason.EARLY_SPRING;
            case MID_SPRING -> Season.SubSeason.MID_SPRING;
            case LATE_SPRING -> Season.SubSeason.LATE_SPRING;
            case EARLY_SUMMER -> Season.SubSeason.EARLY_SUMMER;
            case LATE_SUMMER -> Season.SubSeason.LATE_SUMMER;
            case EARLY_AUTUMN -> Season.SubSeason.EARLY_AUTUMN;
            case MID_AUTUMN -> Season.SubSeason.MID_AUTUMN;
            case LATE_AUTUMN -> Season.SubSeason.LATE_AUTUMN;
            default -> null;
        };
    }
}
