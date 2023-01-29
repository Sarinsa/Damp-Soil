package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FarmBlock.class, priority = 500)
public abstract class FarmlandBlockMixin extends Block {

    @Shadow @Final public static IntegerProperty MOISTURE;

    public FarmlandBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Redirect(method = "isNearWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"))
    private static Iterable<BlockPos> redirectIsWaterNearby(BlockPos pos1, BlockPos pos2, LevelReader worldReader, BlockPos origin) {
        return CommonMixinHooks.getFarmlandCheckBounds(origin);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public boolean redirectRandomTick(ServerLevel serverLevel, BlockPos pos, BlockState state, int flag) {
        return serverLevel.setBlock(pos, state.setValue(MOISTURE, DSCommonConfig.COMMON.waterEffectiveness.get()), flag);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        CommonMixinHooks.onFarmlandTick(state, random, pos, level, ci);
    }
}
