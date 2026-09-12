package com.sarinsa.dampsoil.common.core.config.environment;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class DSEnvironmentRegister {
    
    /**
     * Registers Damp Soil's Crust environment types.
     * <br><br>
     * Called from {@link com.sarinsa.dampsoil.common.core.DampSoil#onCommonSetup(FMLCommonSetupEvent)}.
     */
    public static void register() {
        CrustEnvironmentRegistry.register( "seasonal_weather", SeasonalWeatherEnvironment::new, SeasonalWeatherEnvironment.class,
                "(!)state",
                "Valid state values: " + TomlHelper.toLiteralList( (Object[]) SeasonalWeatherEnvironment.Value.values() ),
                "Miscellaneous Serene Seasons weather conditions that generally do what their names suggest.",
                "If Serene Seasons is not installed, vanilla equivalent checks are evaluated against instead." );
    }
}
