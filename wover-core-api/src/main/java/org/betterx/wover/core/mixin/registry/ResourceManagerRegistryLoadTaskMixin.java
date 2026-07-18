package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ResourceManagerRegistryLoadTask.class)
public abstract class ResourceManagerRegistryLoadTaskMixin<T> {
    @Unique
    private RegistryOps.RegistryInfoLookup wover_registryInfoLookup;

    @Shadow
    protected abstract Registry<T> readOnlyRegistry();

    @Inject(method = "load", at = @At("HEAD"))
    private void wover_captureRegistryInfo(
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        wover_registryInfoLookup = registryInfoLookup;
    }

    @Inject(
            method = "lambda$load$3",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/RegistryLoadTask;registerElements(Ljava/util/stream/Stream;)V",
                    shift = At.Shift.AFTER
            )
    )
    @SuppressWarnings("unchecked")
    private void wover_bootstrap(Map<?, ?> loadedEntries, CallbackInfo ci) {
        Registry<T> registry = readOnlyRegistry();
        DatapackRegistryBuilderImpl.bootstrap(
                wover_registryInfoLookup,
                registry.key(),
                (WritableRegistry<T>) registry
        );
    }
}
