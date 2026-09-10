package com.sarinsa.dampsoil.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName, false,
                "This config contains options that apply to the mod as a whole."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        
        General( MainConfig parent ) {
            super( parent, "general",
                    "General options that apply to the mod as a whole." );
            
            SPEC.comment( "Currently nothing to see here!" );
        }
    }
}
