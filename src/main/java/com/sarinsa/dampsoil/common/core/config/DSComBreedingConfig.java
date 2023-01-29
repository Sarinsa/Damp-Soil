package com.sarinsa.dampsoil.common.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.sarinsa.dampsoil.common.compat.glitchfiend.SeasonRepresentable.*;

public class DSComBreedingConfig {

    public static final DSComBreedingConfig.Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        Pair<DSComBreedingConfig.Config, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(DSComBreedingConfig.Config::new);
        CONFIG = commonPair.getLeft();
        CONFIG_SPEC = commonPair.getRight();
    }


    public static final class Config {

        public final ForgeConfigSpec.BooleanValue enableBreedingSeasons;
        private final ForgeConfigSpec.ConfigValue<CommentedConfig> breedingSeasons;

        private Config(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.comment("Note: this config is only used if Serene Seasons is installed.");
            configBuilder.comment("This config holds information about various animals and which season they can be bred in.");
            configBuilder.push("breeding_seasons");

            enableBreedingSeasons = configBuilder.comment("If set to true, animals will only be able to breed during their configured mating season(s).")
                    .define("enableBreedingSeasons", true);

            breedingSeasons = configBuilder.comment("Each entry here must contain the following:")
                    .comment("The registry name of the animal (meaning the name of the mod the animal is from, then the name of the animal), for instance \"minecraft:cow\".")
                    .comment("A valid list of season names that represent what seasons this animal can be bred in.")
                    .comment("")
                    .comment("Valid seasons are: EARLY_SPRING, MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER, EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN, EARLY_WINTER, MID_WINTER, LATE_WINTER and ALL")
                            .define("breedingSeasons", buildDefaultValues());

            configBuilder.pop();
        }


        public CommentedConfig getBreedingConfig() {
            return breedingSeasons.get();
        }

        private CommentedConfig buildDefaultValues() {
            CommentedConfig config = TomlFormat.newConfig();

            List<String> cow = List.of(MID_SPRING.getName(), LATE_SPRING.getName(), EARLY_SUMMER.getName(), MID_SUMMER.getName(), LATE_SUMMER.getName(), EARLY_AUTUMN.getName(), MID_AUTUMN.getName(), LATE_AUTUMN.getName());
            List<String> pig = List.of(LATE_SPRING.getName(), EARLY_SUMMER.getName(), MID_SUMMER.getName(), LATE_SUMMER.getName());
            List<String> chicken = List.of(EARLY_SPRING.getName(), MID_SPRING.getName(), LATE_SPRING.getName(), EARLY_SUMMER.getName(), MID_SUMMER.getName());
            List<String> sheep = List.of(EARLY_AUTUMN.getName(), MID_AUTUMN.getName(), LATE_AUTUMN.getName());
            List<String> rabbit = List.of(EARLY_SPRING.getName(), MID_SPRING.getName(), LATE_SPRING.getName(), EARLY_SUMMER.getName(), MID_SUMMER.getName(), LATE_SUMMER.getName(), EARLY_AUTUMN.getName());
            List<String> horse = List.of(MID_SPRING.getName(), LATE_SPRING.getName(), EARLY_SUMMER.getName(), MID_SUMMER.getName(), LATE_SUMMER.getName());

            config.add(entityName(EntityType.COW), cow);
            config.add(entityName(EntityType.PIG), pig);
            config.add(entityName(EntityType.CHICKEN), chicken);
            config.add(entityName(EntityType.SHEEP), sheep);
            config.add(entityName(EntityType.RABBIT), rabbit);
            config.add(entityName(EntityType.HORSE), horse);

            return config;
        }

        private String entityName(EntityType<?> entityType) {
            ResourceLocation name = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            return name == null ? "null" : name.toString();
        }
    }
}
