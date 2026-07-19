package org.betterx.wover.events.mixin.world_registry;

import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @ModifyArgs(
            method = "lambda$load$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
                    ordinal = 0)
    )
    private static void wover_captureRegistryPairSecond(Args args) {
        final ResourceManager resourceManager = args.get(0);
        final List<HolderLookup.RegistryLookup<?>> list = args.get(1);

        // this is called when a new world is first created on the server
        // we generate a temporary RegistryAccess here, as it is no longer generated in the WorldLoader.load method
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess = RegistryLayer.createRegistryAccess();
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess2 = layeredRegistryAccess.replaceFrom(
                RegistryLayer.WORLDGEN,
                RegistryDataLoader.load(resourceManager, list, RegistryDataLoader.WORLDGEN_REGISTRIES, Util.backgroundExecutor()).join()
        );
        RegistryAccess.Frozen frozen = layeredRegistryAccess2.getAccessForLoading(RegistryLayer.DIMENSIONS);

        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(frozen, OnRegistryReady.Stage.LOADING);
    }

    //this is the place a new Registry access gets first instantiated
    //either when a new Datapack was added to a world on the create-screen
    //or because we did start world loading
    @ModifyArg(method = "lambda$load$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/util/List;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/server/permissions/PermissionSet;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), index = 1)
    private static LayeredRegistryAccess<RegistryLayer> wover_captureRegistry(LayeredRegistryAccess<RegistryLayer> layered) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                layered.getAccessForLoading(RegistryLayer.RELOADABLE),
                OnRegistryReady.Stage.PREPARATION
        );
        return layered;
    }
}
