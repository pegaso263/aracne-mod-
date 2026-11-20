package com.tuusuario.aracnemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tuusuario.aracnemod.AracneLegsAnimatable;
import com.tuusuario.aracnemod.AracneLegsModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoObjectRenderer;

public class AracneLegsRenderer extends GeoObjectRenderer<AracneLegsAnimatable> {
    public AracneLegsRenderer() {
        super(new AracneLegsModel());
    }

    @Override
    public RenderType getRenderType(AracneLegsAnimatable animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer,
                                    int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
