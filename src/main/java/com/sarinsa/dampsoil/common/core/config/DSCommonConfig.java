package com.sarinsa.dampsoil.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DSCommonConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();
    }

    public static final class Common {

        public final ForgeConfigSpec.BooleanValue cropsDie;
        public final ForgeConfigSpec.IntValue waterRange;
        public final ForgeConfigSpec.IntValue growthReductor;
        public final ForgeConfigSpec.IntValue boneMealReductor;
        public final ForgeConfigSpec.IntValue farmlandDryingRate;
        public final ForgeConfigSpec.IntValue sprinklerActivationTime;
        public final ForgeConfigSpec.IntValue sprinklerRadius;
        public final ForgeConfigSpec.IntValue netheriteSprinklerRadius;
        public final ForgeConfigSpec.BooleanValue mobInteractions;

        public final ForgeConfigSpec.BooleanValue requirePiping;



        private Common(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("general");

            cropsDie = configBuilder.comment("If enabled, crops will die on dry farmland.")
                            .define("cropsDie", true);

            waterRange = configBuilder.comment("Determines the effective radius of a water block to moisturize nearby farmland.")
                            .defineInRange("waterRange", 1, 1, 7);

            growthReductor = configBuilder.comment("Determines the chance for a crop to grow. A value of 4 would equal a 1/4 chance for the crop to grow when it is ticked.")
                            .defineInRange("growthReductor", 7, 1, 10);

            boneMealReductor = configBuilder.comment("Determines the chance for bone meal to further grow a crop. A value of 4 would equal a 1/4 chance for using bone meal to work.")
                            .defineInRange("boneMealReductor", 3, 1, 10);

            farmlandDryingRate = configBuilder.comment("Determines how fast farmland dries up with no water source/sprinkler nearby.")
                            .defineInRange("farmlandDryingRate", 5, 1, 100);

            sprinklerActivationTime = configBuilder.comment("Determines how long the sprinkler is active after having been activated (in seconds).")
                            .defineInRange("sprinklerActivationTime", 15, 0, 30);

            sprinklerRadius = configBuilder.comment("Determines the effective radius of the sprinkler for moisturizing nearby farmland.")
                            .defineInRange("sprinklerRadius", 2, 1, 7);

            netheriteSprinklerRadius = configBuilder.comment("Determines the effective radius of the netherite sprinkler for moisturizing nearby farmland.")
                    .defineInRange("sprinklerRadius", 4, 1, 7);

            mobInteractions = configBuilder.comment("If enabled, the sprinklers will interact with entities in the world in various ways (e.g. hurting water sensitive mobs and extinguishing burning mobs.)")
                            .define("mobInteractions", true);

            configBuilder.pop();

            configBuilder.push("compat");

            requirePiping = configBuilder.comment("Whether sprinklers need to be hooked up with fluid pipes to function.")
                            .define("requirePiping", false);

            configBuilder.pop();
        }
    }
}
