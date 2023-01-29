package com.sarinsa.dampsoil.common.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.sarinsa.dampsoil.common.compat.glitchfiend.AnimalBreedListener;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import com.sarinsa.dampsoil.common.core.DampSoil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.api.season.Season;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DampSoil.MODID)
public class ConfigReloadListener {

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getFileName().equals("dampsoil/breeding_seasons.toml")) {
            updateBreedingSeasons();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getFileName().equals("dampsoil/breeding_seasons.toml")) {
            updateBreedingSeasons();
        }
    }

    @SuppressWarnings("unchecked")
    private static void updateBreedingSeasons() {
        CommentedConfig config = DSComBreedingConfig.CONFIG.getBreedingConfig();

        if (ModList.get().isLoaded(SereneSeasonsHelper.MODID)) {
            AnimalBreedListener.BREEDING_SEASONS.clear();

            for (CommentedConfig.Entry entry : config.entrySet()) {
                ResourceLocation entityName = ResourceLocation.tryParse(entry.getKey());

                if (entityName == null || !ForgeRegistries.ENTITY_TYPES.containsKey(entityName))
                    continue;

                List<Season.SubSeason> subSeasons = new ArrayList<>();

                if (entry.getValue() instanceof List list) {
                    List<String> values = (List<String>) list;

                    for (String s : values) {
                        SeasonRepresentable representable = SeasonRepresentable.getFromName(s);

                        if (representable != null) {
                            subSeasons.add(SereneSeasonsHelper.getFromSeasonRepresentable(representable));
                        }
                    }
                }
                AnimalBreedListener.BREEDING_SEASONS.put(ForgeRegistries.ENTITY_TYPES.getValue(entityName), subSeasons);
            }
        }
    }
}
