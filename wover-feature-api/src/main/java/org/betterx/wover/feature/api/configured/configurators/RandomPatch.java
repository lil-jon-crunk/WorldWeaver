package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import org.betterx.wover.feature.api.features.WoverRandomPatchFeature;
import org.betterx.wover.feature.api.features.config.WoverRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Places a given Feature multiple times in a randomized patch ({@link WoverRandomPatchFeature}).
 * <p>
 * You can set a {@link PlacedFeature} that is randomly placed in the patch.
 */
public interface RandomPatch extends FeatureConfigurator<WoverRandomPatchConfiguration, WoverRandomPatchFeature>, BasePatch<WoverRandomPatchConfiguration, WoverRandomPatchFeature, RandomPatch> {


    /**
     * The feature that should be placed in the patch.
     *
     * @param featureToPlace The feature to place. A {@link PlacedFeatureKey} can be created using
     *                       {@link org.betterx.wover.feature.api.placed.PlacedFeatureManager#createKey(net.minecraft.resources.Identifier)}
     * @return the same instance
     */
    <K extends BasePlacedFeatureKey<K>> RandomPatch featureToPlace(BasePlacedFeatureKey<K> featureToPlace);

    /**
     * The feature that should be placed in the patch.
     *
     * @param featureToPlace The feature to place
     * @return the same instance
     */
    RandomPatch featureToPlace(Holder<PlacedFeature> featureToPlace);
}
