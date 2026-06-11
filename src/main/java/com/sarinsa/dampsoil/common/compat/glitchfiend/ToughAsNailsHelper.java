package com.sarinsa.dampsoil.common.compat.glitchfiend;

import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import toughasnails.api.temperature.TemperatureHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Helper class containing convenience stuff for Tough As Nails. */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE, modid = DampSoil.MODID )
public class ToughAsNailsHelper {
    
    public static final String TAN_MODID = "toughasnails";
    
    private static final Map<UUID, Boolean> SPRINKLED_PLAYERS = new HashMap<>();
    
    /** Marks the specified player as being sprinkled. */
    public static void sprinklePlayer( Player player ) {
        if( ModList.get().isLoaded( TAN_MODID ) ) {
            if( TemperatureHelper.isTemperatureEnabled() ) {
                SPRINKLED_PLAYERS.put( player.getUUID(), true );
            }
        }
    }
    
    /** @return True if the given player is marked as sprinkled. */
    public static boolean isPlayerSprinkled( Player player ) {
        return SPRINKLED_PLAYERS.containsKey( player.getUUID() );
    }
    
    @SubscribeEvent
    public static void onLevelTick( TickEvent.LevelTickEvent event ) {
        if( event.level.dimension() == Level.OVERWORLD && event.phase == TickEvent.Phase.START ) {
            SPRINKLED_PLAYERS.clear();
        }
    }
}
