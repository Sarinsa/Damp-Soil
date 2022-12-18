package com.sarinsa.dampsoil.common.util.mixin;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CommonMixinHooks {

    public static void onCropRandomTick(World world, BlockPos pos, CallbackInfo ci) {
        // Kill off crops on dried soil
        if (DSCommonConfig.COMMON.cropsDie.get()) {
            BlockState state = world.getBlockState(pos.below());

            if (state.getBlock() instanceof FarmlandBlock && state.getValue(FarmlandBlock.MOISTURE) < 1)
                world.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
        }
        // Maybe cancel crop growth
        if (world.getRandom().nextDouble() > 1.0 / (float) DSCommonConfig.COMMON.growthReductor.get()) {
            ci.cancel();
        }
    }
}
