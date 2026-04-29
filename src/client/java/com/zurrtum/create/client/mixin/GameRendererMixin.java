package com.zurrtum.create.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.zurrtum.create.client.flywheel.impl.event.RenderContextHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V"))
    private void renderLevel(
        LevelRenderer instance,
        GraphicsResourceAllocator resourceAllocator,
        DeltaTracker deltaTracker,
        boolean renderOutline,
        CameraRenderState cameraState,
        Matrix4fc modelViewMatrix,
        GpuBufferSlice terrainFog,
        Vector4f fogColor,
        boolean shouldRenderSky,
        ChunkSectionsToRender chunkSectionsToRender,
        Operation<Void> original,
        @Local Matrix4f projectionMatrix
    ) {
        RenderContextHolder holder = (RenderContextHolder) instance;
        holder.flywheel$updateRenderContext(modelViewMatrix, projectionMatrix, mainCamera, deltaTracker);
        original.call(
            instance,
            resourceAllocator,
            deltaTracker,
            renderOutline,
            cameraState,
            modelViewMatrix,
            terrainFog,
            fogColor,
            shouldRenderSky,
            chunkSectionsToRender
        );
        holder.flywheel$resetRenderContext();
    }

    @Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At("HEAD"), cancellable = true)
    private void guardBeforeRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.level == null || minecraft.player == null) {
            ci.cancel();
        }
    }

        /**
         * Guard against null player/level during render extraction.
         * Prevents Camera.getCameraEntityPartialTicks() from crashing when trying to access null level.
         */
        @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
        private void guardNullPlayerDuringExtract(DeltaTracker deltaTracker, boolean doRender, CallbackInfo ci) {
            // Only cancel when a level exists but the player is null (true disconnection state).
            // Do not cancel when both are null (menus/title) to avoid black screen.
            if (minecraft.level != null && minecraft.player == null) {
                ci.cancel();
            }
        }
}
