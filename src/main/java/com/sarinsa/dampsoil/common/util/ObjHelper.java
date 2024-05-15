package com.sarinsa.dampsoil.common.util;

public class ObjHelper {

    public static void nonnull(Object... objects) {
        if (objects.length < 1)
            throw new NullPointerException();

        for (Object o : objects) {
            if (o == null)
                throw new NullPointerException();
        }
    }
}
