package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.util.mixin.CommonMixinHooks;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( value = Animal.class, priority = 1001 )
public abstract class AnimalMixin extends AgeableMob {
    
    protected AnimalMixin( EntityType<? extends AgeableMob> type, Level level ) {
        super( type, level );
    }
    
    @Inject(
            method = "canMate",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void inject_canMate( Animal breedTarget, CallbackInfoReturnable<Boolean> cir ) {
        CommonMixinHooks.onCanMate( (Animal) (Object) this, cir );
    }
}
