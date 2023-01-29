package com.sarinsa.dampsoil.common.compat.glitchfiend;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class SereneSeasonsHelper {

    private static final String MODID = "sereneseasons";


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
}
