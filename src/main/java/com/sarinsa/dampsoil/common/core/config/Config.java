package com.sarinsa.dampsoil.common.core.config;

import com.sarinsa.dampsoil.common.core.DampSoil;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <br><br>
 * Contains references to the main configs in this mod, which in turn provide direct 'getter' access to each
 * configurable value.
 */
public class Config {
    
    public static MainConfig MAIN;
    public static IrrigationConfig IRRIGATION;
    public static CropsConfig CROPS;
    public static AnimalsConfig ANIMALS;
    public static CompatConfig COMPAT;
    
    /**
     * Called from {@link DampSoil#onCommonSetup(FMLCommonSetupEvent)} to
     * enqueue config initialization on the main thread.
     */
    public static void initialize() {
        final ConfigManager cfgManager = ConfigManager.create( "DampSoil", DampSoil.MODID );
        
        MAIN = new MainConfig( cfgManager, "main" );
        MAIN.SPEC.initialize();
        
        IRRIGATION = new IrrigationConfig( cfgManager, "irrigation" );
        IRRIGATION.SPEC.initialize();
        
        CROPS = new CropsConfig( cfgManager, "crops" );
        CROPS.SPEC.initialize();
        
        ANIMALS = new AnimalsConfig( cfgManager, "animals" );
        ANIMALS.SPEC.initialize();
        
        COMPAT = new CompatConfig( cfgManager, "compat" );
        COMPAT.SPEC.initialize();
    }
}
