package com.sarinsa.dampsoil.common.compat.glitchfiend;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Deprecated( forRemoval = true )
public class AnimalBreedListener {
    
    /**
     * Prevent animals from breeding if it is not the right
     * season for mating (if enabled in the config).
     */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onPlayerEntityInteract( PlayerInteractEvent.EntityInteract event ) {
        /*
        if( !Config.COMPAT.SERENE_SEASONS.seasonalBreeding.get() )
            return;
        
        if( event.getTarget() instanceof Animal animal && animal.isFood( event.getItemStack() ) ) {
            final SeasonRepresentable[] seasons = Config.COMPAT.SERENE_SEASONS.breedingSeasonsList.get( animal );
            
            if( seasons != null ) {
                Level level = event.getLevel();
                Season.SubSeason subSeason = SeasonHelper.getSeasonState( level ).getSubSeason();
                boolean isBreedingSeason = false;
                
                for( SeasonRepresentable season : seasons ) {
                    if( season.getName().equals( subSeason.name() ) ) {
                        isBreedingSeason = true;
                        break;
                    }
                }
                
                if( !isBreedingSeason ) {
                    spawnSmokeParticles( level, animal, 7 );
                    event.setCanceled( true );
                    event.setCancellationResult( InteractionResult.PASS );
                }
            }
        }
        
         */
    }
    
    /** Helper method for spawning smoke particles around an entity. */
    @SuppressWarnings( "SameParameterValue" )
    protected void spawnSmokeParticles( Level level, LivingEntity entity, int particleCount ) {
        for( int i = 0; i < particleCount; ++i ) {
            double dx = level.random.nextGaussian() * 0.02D;
            double dy = level.random.nextGaussian() * 0.02D;
            double dz = level.random.nextGaussian() * 0.02D;
            
            level.addParticle(
                    ParticleTypes.SMOKE,
                    entity.getRandomX( 1.0D ),
                    entity.getRandomY() + 0.5D,
                    entity.getRandomZ( 1.0D ),
                    dx, dy, dz
            );
        }
    }
}
