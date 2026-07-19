package org.betterx.wover.surface.mixin;

import org.betterx.wover.entrypoint.LibWoverSurface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SurfaceRules.Context.class)
public abstract class SurfaceRulesContextMixin {
    private static final AtomicBoolean WOVER_REPORTED_NULL_BIOME = new AtomicBoolean();

    @Shadow
    @Final
    private ChunkAccess chunk;

    @Shadow
    @Final
    private Function<BlockPos, Holder<Biome>> biomeGetter;

    @Shadow
    @Final
    private Set<Holder<Biome>> possibleBiomes;

    @Shadow
    private int blockX;

    @Shadow
    private int blockY;

    @Shadow
    private int blockZ;

    @Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
    private void wover_fallbackBiome(CallbackInfoReturnable<Holder<Biome>> cir) {
        if (cir.getReturnValue() != null) return;

        Holder<Biome> biome = chunk.getNoiseBiome(
                QuartPos.fromBlock(blockX),
                QuartPos.fromBlock(blockY),
                QuartPos.fromBlock(blockZ)
        );

        if (biome == null) {
            Set<Holder<Biome>> chunkBiomes = new HashSet<>();
            chunk.collectBiomesInPalette(chunkBiomes);
            biome = chunkBiomes.stream().filter(holder -> holder != null).findFirst().orElse(null);
        }

        if (biome == null && possibleBiomes != null) {
            biome = possibleBiomes.stream().filter(holder -> holder != null).findFirst().orElse(null);
        }

        if (biome == null) {
            BlockPos center = new BlockPos(
                    chunk.getPos().getMinBlockX() + 8,
                    blockY,
                    chunk.getPos().getMinBlockZ() + 8
            );
            biome = biomeGetter.apply(center);
        }

        if (biome != null) {
            if (WOVER_REPORTED_NULL_BIOME.compareAndSet(false, true)) {
                LibWoverSurface.C.LOG.warn("Biome lookup returned null during surface generation; using a safe fallback biome.");
            }
            cir.setReturnValue(biome);
        }
    }
}
