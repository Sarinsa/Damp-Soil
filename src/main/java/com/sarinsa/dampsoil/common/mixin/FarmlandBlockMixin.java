package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = FarmlandBlock.class, priority = 500)
public abstract class FarmlandBlockMixin extends Block {

    public FarmlandBlockMixin(Properties properties) {
        super(properties);
    }

    @Redirect(method = "isNearWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;betweenClosed(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;"))
    private static Iterable<BlockPos> redirectIsWaterNearby(BlockPos pos1, BlockPos pos2, IWorldReader worldReader, BlockPos origin) {
        return CommonMixinHooks.getFarmlandCheckBounds(origin);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        CommonMixinHooks.onFarmlandTick(state, random, ci);
    }
}
