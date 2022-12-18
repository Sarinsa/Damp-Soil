package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CropsBlock.class)
public abstract class CropsBlockMixin extends BushBlock implements IGrowable {

    public CropsBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        CommonMixinHooks.onCropRandomTick(world, pos, ci);
    }
}
