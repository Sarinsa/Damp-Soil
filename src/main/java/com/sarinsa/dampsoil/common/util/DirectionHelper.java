package com.sarinsa.dampsoil.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class DirectionHelper {

    /**
     * Helper method for getting the given entity's nearest
     * vertical looking direction.
     */
    public static Direction getVerticalLookingDir(Entity entity) {
        float f = entity.getViewXRot(1.0F) * ((float)Math.PI / 180F);
        float f2 = MathHelper.sin(f);
        return f2 < 0.0F ? Direction.UP : Direction.DOWN;
    }
}
