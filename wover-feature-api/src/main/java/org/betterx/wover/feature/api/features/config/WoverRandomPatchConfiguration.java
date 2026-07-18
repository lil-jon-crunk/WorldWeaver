package org.betterx.wover.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WoverRandomPatchConfiguration(
        int tries,
        int xzSpread,
        int ySpread,
        Holder<PlacedFeature> feature
) implements FeatureConfiguration {
    public static final Codec<WoverRandomPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.INT.fieldOf("tries").forGetter(WoverRandomPatchConfiguration::tries),
                    Codec.INT.fieldOf("xz_spread").forGetter(WoverRandomPatchConfiguration::xzSpread),
                    Codec.INT.fieldOf("y_spread").forGetter(WoverRandomPatchConfiguration::ySpread),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(WoverRandomPatchConfiguration::feature)
            )
            .apply(instance, WoverRandomPatchConfiguration::new));
}
