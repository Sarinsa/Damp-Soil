package com.sarinsa.dampsoil.api;

import fathertoast.crust.api.config.common.field.IntField;
import net.minecraft.world.entity.Mob;

// TODO - Rework this to store item-type cooldown entries instead of using separate queues

/**
 * The produce cooldown manager is a server-side tick listener that takes care of cooldowns for
 * animal produce, such as milk from cows and eggs from chickens etc.
 * <br><br>
 * The produce cooldown manager instance can be obtained via {@link IDampSoilApi#getProduceCooldownManager()}.
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
