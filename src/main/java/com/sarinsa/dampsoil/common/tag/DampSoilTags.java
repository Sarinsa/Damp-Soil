package com.sarinsa.dampsoil.common.tag;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@SuppressWarnings( "unused" )
public final class DampSoilTags {
    
    public static class EntityTypes {
        private static TagKey<EntityType<?>> modTag( String name ) {
            return TagKey.create( Registries.ENTITY_TYPE, DampSoil.rl( name ) );
        }
        
        private static TagKey<EntityType<?>> forgeTag( String name ) {
            return TagKey.create( Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath( "forge", name ) );
        }
    }
    
    public static class Blocks {
        
        public static final TagKey<Block> DEAD_CROP_MAY_PLACE_ON = modTag( "dead_crop_may_place" );
        
        private static TagKey<Block> modTag( String name ) {
            return TagKey.create( Registries.BLOCK, DampSoil.rl( name ) );
        }
        
        private static TagKey<Block> forgeTag( String name ) {
            return TagKey.create( Registries.BLOCK, ResourceLocation.fromNamespaceAndPath( "forge", name ) );
        }
    }
    
    public static class Items {
        private static TagKey<Item> modTag( String name ) {
            return TagKey.create( Registries.ITEM, DampSoil.rl( name ) );
        }
        
        private static TagKey<Item> forgeTag( String name ) {
            return TagKey.create( Registries.ITEM, ResourceLocation.fromNamespaceAndPath( "forge", name ) );
        }
    }
    
    // Utility class
    private DampSoilTags() { }
}
