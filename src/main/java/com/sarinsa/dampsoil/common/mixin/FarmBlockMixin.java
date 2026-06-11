package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.core.config.Config;
import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( value = FarmBlock.class, priority = 500 )
public abstract class FarmBlockMixin extends Block {
    
    @Shadow
    @Final
    public static IntegerProperty MOISTURE;
    
    
    public FarmBlockMixin( BlockBehaviour.Properties properties ) {
        super( properties );
    }
    
    
    @Redirect(
            method = "isNearWater",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"
            )
    )
    private static Iterable<BlockPos> redirect_isWaterNearby( BlockPos pos1, BlockPos pos2, LevelReader worldReader, BlockPos origin ) {
        return CommonMixinHooks.getFarmlandCheckBounds( origin );
    }
    
    @ModifyArg(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 1
            ),
            index = 1
    )
    public BlockState modifyArg_randomTick( BlockState state ) {
        return state.setValue( MOISTURE, Mth.clamp( 0, FarmBlock.MAX_MOISTURE, Config.IRRIGATION.FARMLAND.waterMoisture.get() ) );
    }
    
    @Inject(
            method = "randomTick",
            at = @At( "HEAD" ),
            cancellable = true
    )
    public void inject_randomTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci ) {
        CommonMixinHooks.onFarmlandRandomTick( state, random, pos, level, ci );
    }
    
    @Inject(
            method = "tick",
            at = @At( "HEAD" )
    )
    public void inject_tick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci ) {
        CommonMixinHooks.onFarmlandTick( state, level, pos, random, ci );
    }
}
