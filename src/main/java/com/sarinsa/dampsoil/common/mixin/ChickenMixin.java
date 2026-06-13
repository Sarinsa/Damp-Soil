package com.sarinsa.dampsoil.common.mixin;

import com.sarinsa.dampsoil.common.core.config.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( Chicken.class )
public abstract class ChickenMixin extends Animal {
    
    @Shadow
    public int eggTime;
    
    protected ChickenMixin( EntityType<? extends Animal> type, Level level ) {
        super( type, level );
    }
    
    @Inject(
            method = "aiStep",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void onAiStep( CallbackInfo ci ) {
        eggTime = Config.ANIMALS.PRODUCE.chickenEggCooldown.next( random );
        ci.cancel();
    }
}
