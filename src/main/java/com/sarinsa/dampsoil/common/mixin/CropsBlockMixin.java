package com.sarinsa.dampsoil.common.mixin;

import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( CropBlock.class )
public abstract class CropsBlockMixin extends BushBlock implements BonemealableBlock {
    
    public CropsBlockMixin( Properties properties ) {
        super( properties );
    }
    
    
    /**
     * Force crops to tick regardless of age so
     * that crops on dry soil die even if mature.
     */
    @Inject(
            method = "isRandomlyTicking",
            at = @At( "HEAD" ),
            cancellable = true
    )
    public void onIsRandomlyTicking( BlockState state, CallbackInfoReturnable<Boolean> ci ) {
        ci.setReturnValue( true );
    }
}
