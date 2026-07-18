package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.WoverRandomPatchConfiguration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class WoverRandomPatchFeature extends Feature<WoverRandomPatchConfiguration> {
    public WoverRandomPatchFeature() {
        super(WoverRandomPatchConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<WoverRandomPatchConfiguration> context) {
        WoverRandomPatchConfiguration config = context.config();
        BlockPos origin = context.origin();
        var random = context.random();
        int placed = 0;

        for (int i = 0; i < config.tries(); i++) {
            BlockPos pos = origin.offset(
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1),
                    random.nextInt(config.ySpread() + 1) - random.nextInt(config.ySpread() + 1),
                    random.nextInt(config.xzSpread() + 1) - random.nextInt(config.xzSpread() + 1)
            );
            if (config.feature().value().place(context.level(), context.chunkGenerator(), random, pos)) placed++;
        }
        return placed > 0;
    }
}
