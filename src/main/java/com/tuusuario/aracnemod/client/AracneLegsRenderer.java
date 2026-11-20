package com.tuusuario.aracnemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tuusuario.aracnemod.AracneLegsAnimatable;
import com.tuusuario.aracnemod.AracneLegsModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

public class AracneLegsRenderer extends GeoReplacedEntityRenderer<AracneLegsAnimatable, AbstractClientPlayer> {
    public AracneLegsRenderer(AracneLegsAnimatable animatable) {
        super(null, new AracneLegsModel(), animatable);
    }

    @Override
    public RenderType getRenderType(AracneLegsAnimatable animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, VertexConsumer buffer,
                                    int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
