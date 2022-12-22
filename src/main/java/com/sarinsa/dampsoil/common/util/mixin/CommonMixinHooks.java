package com.sarinsa.dampsoil.common.util.mixin;

import com.sarinsa.dampsoil.common.block.FrozenFarmBlock;
import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.core.registry.DSBlocks;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("JavadocReference")
public class CommonMixinHooks {

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.CropsBlockMixin#onRandomTick(BlockState, ServerLevel, BlockPos, RandomSource, CallbackInfo)}<br>
     * <br>
     * Checks if the crop block should die if it is on dry farmland,
     * and checks if this random tick should be canceled altogether to
     * slow down crop growth.
     */
    public static void onCropRandomTick(Level level, BlockPos pos, CallbackInfo ci) {
        // Kill off crops on dry soil
        if (DSCommonConfig.COMMON.cropsDie.get()) {
            BlockState state = level.getBlockState(pos.below());

            if (state.getBlock() instanceof FarmBlock && state.getValue(FarmBlock.MOISTURE) < 1) {
                level.setBlock(pos, DSBlocks.DEAD_CROP.get().defaultBlockState(), 2);
                level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.65F, 0.5F);
            }
        }
        // Maybe cancel crop growth
        if (level.getRandom().nextDouble() > 1.0 / (float) DSCommonConfig.COMMON.growthRate.get()) {
            ci.cancel();
        }
    }

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.FarmlandBlockMixin#redirectIsWaterNearby(BlockPos, BlockPos, LevelReader, BlockPos)}<br>
     * <br>
     * @return an Iterable containing the BlockPos bounds to check for water around farmland.
     */
    public static Iterable<BlockPos> getFarmlandCheckBounds(BlockPos origin) {
        final int waterRange = DSCommonConfig.COMMON.waterRange.get();
        return BlockPos.betweenClosed(origin.offset(-waterRange, 0, -waterRange), origin.offset(waterRange, 1, waterRange));
    }

    /**
     * Called from {@link com.sarinsa.dampsoil.common.mixin.FarmlandBlockMixin#onRandomTick(BlockState, ServerLevel, BlockPos, RandomSource, CallbackInfo)}<br>
     * <br>
     * Checks if farmland should cancel its random tick to prevent it from losing
     * moisture. How likely this is to happen depends on the farmlandDryingRate config option.<br>
     * <br>
     * Also checks if we are in a cold biome and 'freezeFarmland' is enabled in the config, in which case
     * we freeze the farmland and let it preserve the moisture it had.<br>
     * <br>
     * ALSO also, if we are in a biome with a temperature greater than 1.0, and the block is in direct sunlight,
     * evaporate moisture at normal tick speed.
     */
    public static void onFarmlandTick(BlockState state, RandomSource random, BlockPos pos, ServerLevel level, CallbackInfo ci) {
        int moisture = state.getValue(FarmBlock.MOISTURE);

        if (DSCommonConfig.COMMON.freezeFarmland.get()) {
            if (BlockHelper.shouldFreezeFarmlandAt(level, pos)) {
                level.setBlock(pos, DSBlocks.FROZEN_FARMLAND.get().defaultBlockState().setValue(FrozenFarmBlock.MOISTURE, moisture), 2);
                ci.cancel();
                return;
            }
        }
        if (DSCommonConfig.COMMON.vaporiseMoisture.get()) {
            if (BlockHelper.shouldEvaporateAt(level, pos)) {
                level.setBlock(pos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, --moisture), 2);

                for (int i = 0; i < 5; i++) {
                    level.sendParticles(
                            DSParticles.WATER_VAPOR.get(),
                            pos.getX() + random.nextDouble(),
                            pos.getY() + 1.1D,
                            pos.getZ() + random.nextDouble(),
                            1,
                            0.0D,
                            0.01D,
                            0.0D,
                            0.02D
                    );
                }
            }
        }
        if (moisture > 0 && random.nextDouble() > DSCommonConfig.COMMON.farmlandDryingRate.get())
            ci.cancel();
    }
}
