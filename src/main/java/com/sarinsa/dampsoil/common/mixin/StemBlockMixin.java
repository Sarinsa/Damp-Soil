package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StemBlock.class)
public abstract class StemBlockMixin extends BushBlock {

    public StemBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
        CommonMixinHooks.onCropRandomTick(level, pos, ci);
    }
}
