package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(CropsBlock.class)
public abstract class CropsBlockMixin extends BushBlock implements IGrowable {

    public CropsBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        CommonMixinHooks.onCropRandomTick(world, pos, ci);
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
