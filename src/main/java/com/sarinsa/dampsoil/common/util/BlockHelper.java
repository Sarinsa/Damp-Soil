package com.sarinsa.dampsoil.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;

public class BlockHelper {

    /**
     * @return True if the given BlockPos is at a place
     * where wet farmland should dry out quickly in direct
     * sunlight.
     */
    public static boolean shouldEvaporateAt(Level level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.FARMLAND) && !FarmBlock.isNearWater(level, pos)) {
            int moisture = level.getBlockState(pos).getValue(FarmBlock.MOISTURE);

            if (moisture > 0) {
                if (level.dimensionType().ultraWarm())
                    return true;

                return level.getBiome(pos).get().getBaseTemperature() >= 1.0F && level.isDay() && !level.isRaining() && level.canSeeSky(pos);
            }
        }
        return false;
    }

    /**
     * @return True if the given BlockPos is at a place where wet farmland should
     * freeze.
     */
    public static boolean shouldFreezeFarmlandAt(Level level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.FARMLAND)) {
            int moisture = level.getBlockState(pos).getValue(FarmBlock.MOISTURE);
            return !level.getBiome(pos).get().warmEnoughToRain(pos) && level.getBrightness(LightLayer.BLOCK, pos) < 10 && pos.getY() > 30 && moisture > 0;
        }
        return false;
    }
}
