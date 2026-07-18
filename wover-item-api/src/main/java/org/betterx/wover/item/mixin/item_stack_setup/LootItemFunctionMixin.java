package org.betterx.wover.item.mixin.item_stack_setup;

import org.betterx.wover.item.api.ItemStackHelper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(LootItemFunction.class)
public interface LootItemFunctionMixin {
    @ModifyVariable(
            method = "decorate",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Consumer<ItemStack> wover_decorate(Consumer<ItemStack> output) {
        return itemStack -> output.accept(ItemStackHelper.callItemStackSetupIfPossible(itemStack));
    }
}
