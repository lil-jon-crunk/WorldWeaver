package org.betterx.wover.block.mixin.client;

import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.client.trait.ChestRenderTrait;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.state.properties.ChestType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChestRenderer.class)
public abstract class SheetsMixin {
    @Unique
    private ChestRenderTrait.ChestMaterialSet wover_materials;

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD")
    )
    private void wover_beforeSubmit(
            ChestRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        final var chestRenderTrait = ClientBlockTraits
                .CHEST_RENDERER
                .getRuntimeTraits(level.getBlockState(state.blockPos).getBlock());

        if (chestRenderTrait != null && !chestRenderTrait.isEmpty()) {
            wover_materials = chestRenderTrait.getFirst().getMaterial();
        }
    }

    @Redirect(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Sheets;chooseSprite(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/sprite/SpriteId;"
            )
    )
    private SpriteId wover_chooseSprite(ChestRenderState.ChestMaterialType material, ChestType chestType) {
        if (wover_materials == null) return Sheets.chooseSprite(material, chestType);
        return switch (chestType) {
            case LEFT -> wover_materials.left();
            case RIGHT -> wover_materials.right();
            case SINGLE -> wover_materials.single();
        };
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("RETURN")
    )
    private void wover_afterSubmit(
            ChestRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
        wover_materials = null;
    }
}
