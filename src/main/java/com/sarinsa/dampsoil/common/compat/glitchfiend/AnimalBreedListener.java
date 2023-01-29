package com.sarinsa.dampsoil.common.compat.glitchfiend;

import com.sarinsa.dampsoil.common.core.config.DSComBreedingConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalBreedListener {

    /**
     * A Map containing what seasons animals can breed in. Updated on config reload from {@link com.sarinsa.dampsoil.common.core.config.ConfigReloadListener}
     */
    public static Map<EntityType<?>, List<Season.SubSeason>> BREEDING_SEASONS = new HashMap<>();


    /**
     * Prevent animals from breeding if it is not the right
     * season for mating (if enabled in the config).
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!DSComBreedingConfig.CONFIG.enableBreedingSeasons.get())
            return;

        if (event.getTarget() instanceof Animal animal) {
            if (animal.isFood(event.getItemStack()) && BREEDING_SEASONS.containsKey(animal.getType())) {
                List<Season.SubSeason> seasons = BREEDING_SEASONS.get(animal.getType());
                Level level = event.getLevel();

                if (!seasons.contains(SeasonHelper.getSeasonState(level).getSubSeason())) {
                    spawnSmokeParticles(level, animal);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.PASS);
                }
            }
        }
    }

    protected void spawnSmokeParticles(Level level, LivingEntity entity) {
        for(int i = 0; i < 7; ++i) {
            double xVel = level.random.nextGaussian() * 0.02D;
            double yVel = level.random.nextGaussian() * 0.02D;
            double zVel = level.random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.SMOKE, entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), xVel, yVel, zVel);
        }
    }
}
