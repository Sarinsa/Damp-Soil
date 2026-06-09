package com.sarinsa.dampsoil.api;

import net.minecraft.world.entity.LivingEntity;

/**
 * The produce watcher is a server-side tick manager responsible for various cooldowns on
 * animals that produce resources, such as milk from cows and eggs from chickens etc.
 * <br><br>
 * The implementation of this can be obtained via {@link IDampSoilApi#getProduceCooldownManager()}.
 * <br><br>
 * Remember, you are of course free to manage cooldowns yourself in any way you'd like, but if you
 * want to utilize Damp Soil's own cooldown system you can do that too and use this.
 * <br><br>
 * <strong>NOTE:</strong> all mobs that should utilize this cooldown system MUST be added to the
 * entity type tag <br><strong>'damp_soil:cooldownable_mobs'</strong>, or else their cooldowns won't be ticked.
 */
public interface IProduceCooldownManager {
    
    /**
     * @param cooldownQueue A {@link CooldownQueue} representing the cooldown timer you wish to check.
     * @param entity        The animal to check.
     * @return True if the given animal is on cooldown in the specified timer. Always returns false on client.
     */
    boolean canProduce( LivingEntity entity, CooldownQueue cooldownQueue );
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param entity        The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing a cooldown timer to add a cooldown to.
     *                      Which type to use is entirely up to you. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldown      The amount of cooldown ticks.
     */
    void setRecentlyProduced( LivingEntity entity, CooldownQueue cooldownQueue, int cooldown );
}
