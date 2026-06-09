package com.sarinsa.dampsoil.common.util;

public class ObjHelper {
    
    /** @throws NullPointerException if any of the given objects are null. */
    public static void nonnull( Object... objects ) {
        if( objects.length < 1 )
            throw new NullPointerException();
        
        for( Object o : objects ) {
            if( o == null )
                throw new NullPointerException();
        }
    }
}
