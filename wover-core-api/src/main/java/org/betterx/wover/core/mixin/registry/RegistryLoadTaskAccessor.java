package org.betterx.wover.core.mixin.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLoadTask;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegistryLoadTask.class)
public interface RegistryLoadTaskAccessor<T> {
    @Invoker("readOnlyRegistry")
    Registry<T> wover_readOnlyRegistry();
}
