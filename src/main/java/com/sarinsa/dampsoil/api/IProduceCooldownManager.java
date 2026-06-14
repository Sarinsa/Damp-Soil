package com.sarinsa.dampsoil.api;

import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.world.entity.Mob;

/**
 * The produce watcher is a server-side tick manager responsible for various cooldowns on
 * animals that produce resources, such as milk from cows and eggs from chickens etc.
 * <br><br>
 * The implementation of this can be obtained via {@link IDampSoilApi#getProduceCooldownManager()}.
 * <br><br>
 * Remember, you are of course free to manage cooldowns yourself in any way you'd like, but if you
 * want to utilize Damp Soil's own cooldown system you can do that too and use this.
 */
public interface IProduceCooldownManager {
    
    /**
     * @param cooldownQueue A {@link CooldownQueue} representing the cooldown queue to check.
     * @param mob           The animal to check.
     * @return True if the given animal is on cooldown in the specified timer. Always returns false on client.
     */
    boolean canProduce( Mob mob, CooldownQueue cooldownQueue );
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param mob           The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing the cooldown queue to add a cooldown to.
     *                      Which type to use is completely optional. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldown      The amount of cooldown ticks.
     */
    void setRecentlyProduced( Mob mob, CooldownQueue cooldownQueue, int cooldown );
    
    /**
     * Puts the given entity on cooldown in the specified cooldown queue. Does nothing on client.
     *
     * @param mob           The animal to put on cooldown.
     * @param cooldownQueue A {@link CooldownQueue} representing a cooldown timer to add a cooldown to.
     *                      Which type to use is completely optional. You can choose between 3 different cooldown timers.
     *                      See {@link CooldownQueue} for more information.
     * @param cooldownField An integer random-range config field to sample a cooldown from.
     */
    void setRecentlyProduced( Mob mob, CooldownQueue cooldownQueue, IntField.RandomRange cooldownField );
}
