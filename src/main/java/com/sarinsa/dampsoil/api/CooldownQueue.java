package com.sarinsa.dampsoil.api;

import javax.annotation.Nullable;

/**
 * The produce cooldown manager implementation has three different produce cooldown timers
 * and each of the values in this enum corresponds to one of these timers.
 * <br><br>
 * If you have an animal that can produce multiple resources, you can add a cooldown for one resource
 * to the first timer, and the other resource to the third.
 * <br><br>
 * For example, internally, Damp Soil uses two different timers for Mooshrooms. One for milk cooldown and one for mushroom stew cooldown.
 */
public enum CooldownQueue {
    
    FIRST( "FirstCooldown" ),
    SECOND( "SecondCooldown" ),
    THIRD( "ThirdCooldown" );
    
    CooldownQueue( String tagName ) {
        this.tagName = tagName;
    }
    
    final String tagName;
    
    /** @return This enum constant's NBT tag name. */
    public String getTagName() {
        return tagName;
    }
    
    /**
     * @return The enum constant associated with the specified NBT tag name.
     * Returns null if no match is found.
     */
    @Nullable
    public static CooldownQueue getFromName( String tagName ) {
        for( CooldownQueue type : values() ) {
            if( type.getTagName().equals( tagName ) )
                return type;
        }
        return null;
    }
}
