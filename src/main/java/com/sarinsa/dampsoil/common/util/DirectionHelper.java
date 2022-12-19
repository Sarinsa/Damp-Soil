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
        float xRot = entity.getViewXRot(1.0F) * ((float) Math.PI / 180F);
        return MathHelper.sin(xRot) < 0.0F ? Direction.UP : Direction.DOWN;
    }
}
