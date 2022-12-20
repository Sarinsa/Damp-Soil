package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(CropBlock.class)
public abstract class CropsBlockMixin extends BushBlock implements BonemealableBlock {

    public CropsBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
        CommonMixinHooks.onCropRandomTick(level, pos, ci);
    }

    /**
     * Force crops to tick regardless of age so
     * that crops on dry soil die even if mature.
     */
    @Inject(at = @At("HEAD"), method = "isRandomlyTicking", cancellable = true)
    public void onIsRandomlyTicking(BlockState state, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(true);
    }
}
