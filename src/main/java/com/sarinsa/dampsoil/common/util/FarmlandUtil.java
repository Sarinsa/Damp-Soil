package com.sarinsa.dampsoil.common.util;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmlandUtil {

    public static boolean checkForWater(World world, BlockPos pos) {
        final int waterRange = DSCommonConfig.COMMON.waterRange.get();
        boolean hasWater = false;

        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(waterRange, 0, waterRange), pos.offset(-waterRange, 0, -waterRange))) {
            if (world.getBlockState(blockPos).getFluidState().is(FluidTags.WATER)) {
                hasWater = true;
                break;
            }
        }

        // TODO - general compat for fluid transmitters (like pipes and stuff)
        return hasWater;
    }
}
