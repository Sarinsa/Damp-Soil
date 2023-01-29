package com.sarinsa.dampsoil.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DSComGeneralConfig {

    public static final Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        Pair<Config, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG = commonPair.getLeft();
        CONFIG_SPEC = commonPair.getRight();
    }

    public static final class Config {

        public final ForgeConfigSpec.BooleanValue cropsDie;
        public final ForgeConfigSpec.IntValue waterRange;
        public final ForgeConfigSpec.IntValue waterEffectiveness;
        public final ForgeConfigSpec.IntValue growthRate;
        public final ForgeConfigSpec.BooleanValue moistureGrowthMul;
        public final ForgeConfigSpec.IntValue boneMealEfficiency;
        public final ForgeConfigSpec.DoubleValue farmlandDryingRate;
        public final ForgeConfigSpec.IntValue sprinklerActivationTime;
        public final ForgeConfigSpec.IntValue sprinklerRadius;
        public final ForgeConfigSpec.IntValue netheriteSprinklerRadius;
        public final ForgeConfigSpec.BooleanValue mobInteractions;
        public final ForgeConfigSpec.BooleanValue disableTrampling;
        public final ForgeConfigSpec.BooleanValue freezeFarmland;
        public final ForgeConfigSpec.BooleanValue vaporiseMoisture;
        public final ForgeConfigSpec.BooleanValue canSprinkleInUltrawarm;

        public final ForgeConfigSpec.BooleanValue requirePiping;
        public final ForgeConfigSpec.BooleanValue sereneSeasonsCompat;
        public final ForgeConfigSpec.BooleanValue sprinklerCoolsPlayer;



        private Config(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("general");

            cropsDie = configBuilder.comment("If enabled, crops will die on dry farmland.")
                            .define("cropsDie", true);

            waterRange = configBuilder.comment("Determines the effective radius of a water block to moisturize nearby farmland.")
                            .defineInRange("waterRange", 1, 1, 7);

            waterEffectiveness = configBuilder.comment("Determines how moisturized farmland can get from nearby water sources. 0 means zero moisture, 7 means max moisture level.")
                    .defineInRange("waterEffectiveness", 4, 1, 7);

            growthRate = configBuilder.comment("Determines the chance for a crop to grow. A value of 4 would equal a 1/4 chance for the crop to grow when it is ticked.")
                            .defineInRange("growthRate", 7, 1, 10);

            moistureGrowthMul = configBuilder.comment("If enabled, crop growth speed will be affected by the level of moisture in the soil. This ranges from no growth in dry farmland to normal growth speed in fully moisturized farmland.")
                    .define("moistureGrowthMul", true);

            boneMealEfficiency = configBuilder.comment("Determines the chance for bone meal to further grow a crop. A value of 4 would equal a 1/4 chance for using bone meal to work.")
                            .defineInRange("boneMealEfficiency", 3, 1, 10);

            farmlandDryingRate = configBuilder.comment("Determines how fast farmland dries up with no water source/sprinkler nearby. A value of 0.05 equals a 5% for farmland to lose a bit of moisture when it is randomly ticked.")
                            .defineInRange("farmlandDryingRate", 0.05, 0.01, 1.0);

            sprinklerActivationTime = configBuilder.comment("Determines how long the sprinkler is active after having been activated (in seconds).")
                            .defineInRange("sprinklerActivationTime", 15, 0, 30);

            sprinklerRadius = configBuilder.comment("Determines the effective radius of the sprinkler for moisturizing nearby farmland.")
                            .defineInRange("sprinklerRadius", 2, 1, 7);

            netheriteSprinklerRadius = configBuilder.comment("Determines the effective radius of the netherite sprinkler for moisturizing nearby farmland.")
                    .defineInRange("netheriteSprinklerRadius", 4, 1, 7);

            mobInteractions = configBuilder.comment("If enabled, the sprinklers will interact with entities in the world in various ways (e.g. hurting water sensitive mobs and extinguishing burning mobs.)")
                            .define("mobInteractions", true);

            disableTrampling = configBuilder.comment("If enabled, wet farmland can not be trampled into dirt by mobs or players.")
                            .define("disableTrampling", true);

            freezeFarmland = configBuilder.comment("If enabled, wet farmland will freeze in cold biomes.")
                            .define("freezeFarmland", true);

            vaporiseMoisture = configBuilder.comment("If enabled, wet farmland will dry out at vanilla speed if in direct sunlight and in a biome with a temperature equal or greater than 1.0. ")
                            .define("vaporiseMoisture", true);

            canSprinkleInUltrawarm = configBuilder.comment("If enabled, sprinklers will function normally in ultra warm dimensions (like the nether). If disabled, the sprinkler will only spew out some sad vapor instead.")
                            .define("canSprinkleInUltrawarm", false);

            configBuilder.pop();

            configBuilder.push("compat");

            requirePiping = configBuilder.comment("Whether sprinklers need to be hooked up with fluid pipes to function.")
                            .define("requirePiping", false);

            sereneSeasonsCompat = configBuilder.comment("If Serene Seasons is installed and this option is enabled, farmland will freeze when it is winter season.")
                            .define("sereneSeasonsCompat", true);

            sprinklerCoolsPlayer = configBuilder.comment("If Tough As Nails is installed and this option is enabled, players can cool down when standing close to an active sprinkler.")
                            .define("sprinklerCoolsPlayer", true);

            configBuilder.pop();
        }
    }
}
