package com.zurrtum.create.client.catnip.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface SuperByteBufferRenderState extends CustomGeometryRenderer {
    void submit(PoseStack matrices, SubmitNodeCollector queue);

    void submit(Pose transform, PoseStack matrices, SubmitNodeCollector queue);

    void renderInto(Pose pose, VertexConsumer consumer);

    void submit(RenderType type, PoseStack matrices, SubmitNodeCollector queue);

    default boolean isEmpty() {
        return false;
    }
}
