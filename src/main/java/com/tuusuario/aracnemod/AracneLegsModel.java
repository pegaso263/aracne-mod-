package com.tuusuario.aracnemod;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AracneLegsModel extends AnimatedGeoModel<AracneLegsAnimatable> {
    private static final ResourceLocation MODEL =
            new ResourceLocation("aracnemod", "geo/rachnera_form.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("aracnemod", "textures/entity/rachnera_form.png");
    private static final ResourceLocation ANIMATION =
            new ResourceLocation("aracnemod", "animations/rachnera_form.animation.json");

    @Override
    public ResourceLocation getModelResource(AracneLegsAnimatable object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AracneLegsAnimatable object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AracneLegsAnimatable object) {
        return ANIMATION;
    }

    @Override
    public void setLivingAnimations(AracneLegsAnimatable entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        float morph = entity.getMorphProgress();
        float gait = entity.getGaitPhase();
        boolean activeMotion = entity.isMoving() || entity.isClimbing();

        IBone abdomen = getAnimationProcessor().getBone("arachne_abdomen");
        if (abdomen != null) {
            abdomen.setScaleX(morph);
            abdomen.setScaleY(morph);
            abdomen.setScaleZ(morph);

            float pitchOffset = entity.isClimbing() ? 0.35F : (entity.isSneaking() ? 0.16F : 0.0F);
            float sway = activeMotion ? (float) Math.sin(gait) * 0.06F : (float) Math.sin(gait * 0.5F) * 0.02F;
            abdomen.setRotationX(abdomen.getRotationX() + pitchOffset);
            abdomen.setRotationY(abdomen.getRotationY() + sway);
        }

        // Gait alternado inspirado en extremidades articuladas (pares 1-4 y 2-3 en contrafase)
        for (int i = 1; i <= 4; i++) {
            float legScale = entity.getLegProgress(i - 1);
            float phaseSign = (i == 1 || i == 4) ? 1.0F : -1.0F;
            float sideSign = (i == 1 || i == 3) ? 1.0F : -1.0F;

            float swingX = activeMotion ? (float) Math.sin(gait + (phaseSign > 0 ? 0.0F : Math.PI)) * 0.38F : 0.0F;
            float liftZ = activeMotion ? Math.max(0.0F, (float) Math.cos(gait + (phaseSign > 0 ? 0.0F : Math.PI))) * 0.22F * sideSign : 0.0F;

            IBone legBase = getAnimationProcessor().getBone("leg_base_" + i);
            if (legBase != null) {
                legBase.setScaleX(legScale);
                legBase.setScaleY(legScale);
                legBase.setScaleZ(legScale);
                legBase.setRotationX(legBase.getRotationX() + swingX * legScale);
                legBase.setRotationZ(legBase.getRotationZ() + liftZ * legScale);
            }

            IBone lowerLeg = getAnimationProcessor().getBone("leg_" + i);
            if (lowerLeg != null) {
                float kneeFlex = activeMotion ? (float) Math.sin(gait * 2.0F + i) * 0.14F : 0.0F;
                lowerLeg.setRotationZ(lowerLeg.getRotationZ() + kneeFlex * legScale);
            }
        }
    }
}
