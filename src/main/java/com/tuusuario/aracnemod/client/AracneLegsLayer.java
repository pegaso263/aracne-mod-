package com.tuusuario.aracnemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tuusuario.aracnemod.AracneLegsAnimatable;
import com.tuusuario.aracnemod.AracneLegsModel;
import com.tuusuario.aracnemod.AracneMechanics;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class AracneLegsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
        implements IGeoRenderer<AracneLegsAnimatable> {

    private static final Map<UUID, AracneLegsAnimatable> PLAYER_ANIMATABLES = new HashMap<>();
    private final AracneLegsModel modelProvider = new AracneLegsModel();
    private MultiBufferSource currentBufferSource;

    public AracneLegsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.isInvisible()) return;

        int legs = AracneMechanics.getUnlockedLegs(player.experienceLevel);
        if (legs <= 0) return;

        AracneLegsAnimatable animatable = PLAYER_ANIMATABLES.computeIfAbsent(
                player.getUUID(), id -> new AracneLegsAnimatable());
        animatable.updateState(player.experienceLevel, player.swinging);

        ResourceLocation modelLocation = modelProvider.getModelResource(animatable);
        GeoModel model = modelProvider.getModel(modelLocation);
        if (model == null) return;

        modelProvider.setLivingAnimations(animatable, player.getId());

        this.currentBufferSource = bufferSource;

        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);
        poseStack.translate(0.0D, -0.75D, 0.0D);
        poseStack.scale(0.65F, 0.65F, 0.65F);

        ResourceLocation texture = getTextureLocation(animatable);
        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        for (GeoBone bone : model.topLevelBones) {
            renderRecursively(bone, poseStack, vertexConsumer, packedLight,
                    OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();
        this.currentBufferSource = null;
    }

    @Override
    public GeoModelProvider<AracneLegsAnimatable> getGeoModelProvider() {
        return modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(AracneLegsAnimatable instance) {
        return modelProvider.getTextureResource(instance);
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        return currentBufferSource;
    }
}
