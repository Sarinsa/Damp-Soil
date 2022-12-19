package com.sarinsa.dampsoil.common.util.mixin;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@SuppressWarnings("JavadocReference")
public class CommonMixinHooks {

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.CropsBlockMixin#onRandomTick(BlockState, ServerWorld, BlockPos, Random, CallbackInfo)}<br>
     * <br>
     * Checks if the crop block should die if it is on dry farmland,
     * and checks if this random tick should be canceled altogether to
     * slow down crop growth.
     */
    public static void onCropRandomTick(World world, BlockPos pos, CallbackInfo ci) {
        // Kill off crops on dry soil
        if (DSCommonConfig.COMMON.cropsDie.get()) {
            BlockState state = world.getBlockState(pos.below());

            if (state.getBlock() instanceof FarmlandBlock && state.getValue(FarmlandBlock.MOISTURE) < 1) {
                world.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
                world.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundCategory.BLOCKS, 0.65F, 0.5F);
            }
        }
        // Maybe cancel crop growth
        if (world.getRandom().nextDouble() > 1.0 / (float) DSCommonConfig.COMMON.growthRate.get()) {
            ci.cancel();
        }
    }

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.FarmlandBlockMixin#redirectIsWaterNearby(BlockPos, BlockPos, IWorldReader, BlockPos)}<br>
     * <br>
     * @return an Iterable containing the BlockPos bounds to check for water around farmland.
     */
    public static Iterable<BlockPos> getFarmlandCheckBounds(BlockPos origin) {
        final int waterRange = DSCommonConfig.COMMON.waterRange.get();
        return BlockPos.betweenClosed(origin.offset(-waterRange, 0, -waterRange), origin.offset(waterRange, 1, waterRange));
    }

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.FarmlandBlockMixin#onRandomTick(BlockState, ServerWorld, BlockPos, Random, CallbackInfo)}<br>
     * <br>
     * Checks if farmland should cancel its random tick to prevent it from losing
     * moisture. How likely this is to happen depends on the farmlandDryingRate config option.
     */
    public static void onFarmlandTick(BlockState state, Random random, CallbackInfo ci) {
        int moisture = state.getValue(FarmlandBlock.MOISTURE);

        if (moisture > 0 && random.nextDouble() > DSCommonConfig.COMMON.farmlandDryingRate.get())
            ci.cancel();
    }
}
