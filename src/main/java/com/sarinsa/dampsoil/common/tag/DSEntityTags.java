package com.sarinsa.dampsoil.common.tag;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class DSEntityTags {

    public static final TagKey<EntityType<?>> COOLDOWNABLE_MOBS = modTag("cooldownable_mobs");


    private static TagKey<EntityType<?>> modTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, DampSoil.resLoc(name));
    }

    private static TagKey<EntityType<?>> forgeTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", name));
    }

    public static void init() {}
    private DSEntityTags() {}
}
