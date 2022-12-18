package com.sarinsa.dampsoil.common.util;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class FarmlandUtil {

    public static boolean checkForWater(IWorldReader world, BlockPos pos) {
        final int waterRange = DSCommonConfig.COMMON.waterRange.get();
        boolean hasWater = false;

        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-waterRange, 0, -waterRange), pos.offset(waterRange, 1, waterRange))) {
            if (world.getBlockState(blockPos).getFluidState().is(FluidTags.WATER)) {
                hasWater = true;
                break;
            }
        }
        return hasWater;
    }
}
