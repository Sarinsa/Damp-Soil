package com.sarinsa.dampsoil.common.compat.glitchfiend;

import toughasnails.api.temperature.IPlayerTemperatureModifier;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public class TempModifiers {

    private static final IPlayerTemperatureModifier sprinklerMod = (player, temperatureLevel) -> {
        if (ToughAsNailsHelper.isPlayerSprinkled(player)) {
            return SereneSeasonsHelper.isWinter(player.level) ? TemperatureLevel.ICY : TemperatureLevel.COLD;
        }
        return temperatureLevel;
    };


    public static void register() {
        TemperatureHelper.registerPlayerTemperatureModifier(sprinklerMod);
    }
}
