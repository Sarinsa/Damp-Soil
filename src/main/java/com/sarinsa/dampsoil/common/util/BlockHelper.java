package com.sarinsa.dampsoil.common.util;

import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import com.sarinsa.dampsoil.common.core.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraftforge.fml.ModList;
import sereneseasons.season.SeasonHooks;

public class BlockHelper {
    
    /**
     * @return True if the given block position is at a place
     * where wet farmland should dry out quickly when exposed to direct sunlight.
     */
    public static boolean shouldEvaporateAt( Level level, BlockPos pos ) {
        if( level.getBlockState( pos ).is( Blocks.FARMLAND ) && !FarmBlock.isNearWater( level, pos ) ) {
            int moisture = level.getBlockState( pos ).getValue( FarmBlock.MOISTURE );
            
            if( moisture > 0 ) {
                return Config.IRRIGATION.FARMLAND.vaporizeConditions.getOrElseIfLoaded( level, pos, 0.0 ) > 0.0;
            }
        }
        return false;
    }
    
    /**
     * @return True if the given block position is at a place
     * where wet farmland should freeze.
     */
    public static boolean shouldFreezeFarmlandAt( Level level, BlockPos pos ) {
        boolean canSnow = (!level.getBiome( pos ).get().warmEnoughToRain( pos )
                || (ModList.get().isLoaded( SereneSeasonsHelper.MODID ) && SeasonHooks.coldEnoughToSnowSeasonal( level, level.getBiome( pos ), pos )));
        
        return canSnow && level.getBrightness( LightLayer.BLOCK, pos ) < 10;
    }
}
