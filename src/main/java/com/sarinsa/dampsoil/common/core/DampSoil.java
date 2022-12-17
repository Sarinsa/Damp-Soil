package com.sarinsa.dampsoil.common.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DampSoil.MODID)
public class DampSoil {

    public static final String MODID = "dampsoil";

    private static final Logger LOGGER = LogManager.getLogger(MODID);


    public DampSoil() {

    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
