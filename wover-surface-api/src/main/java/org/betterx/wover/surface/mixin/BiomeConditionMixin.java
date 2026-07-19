package org.betterx.wover.surface.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Minecraft 26.2 may evaluate biome surface rules before a biome is available from the
 * generation context. Vanilla's direct holder set throws for a null lookup, which leaves the
 * integrated server waiting forever for a failed spawn chunk. An unknown biome cannot match a
 * biome-specific rule, so treating it as a non-match preserves the rule semantics and lets the
 * unconditional fallback surface rule run.
 */
@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$BiomeConditionSource$1BiomeCondition")
public abstract class BiomeConditionMixin {
    @Redirect(
            method = "compute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/HolderSet;contains(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private boolean wover_nullBiomeDoesNotMatch(HolderSet<Biome> biomes, Holder<Biome> biome) {
        return biome != null && biomes.contains(biome);
    }
}
