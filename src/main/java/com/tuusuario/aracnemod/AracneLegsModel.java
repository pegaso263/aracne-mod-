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
        int unlockedLegs = AracneMechanics.getUnlockedLegs(entity.getPlayerLevel());

        IBone abdomen = getAnimationProcessor().getBone("arachne_abdomen");
        if (abdomen != null) {
            abdomen.setScaleX(morph);
            abdomen.setScaleY(morph);
            abdomen.setScaleZ(morph);
        }

        for (int i = 1; i <= 4; i++) {
            IBone legBase = getAnimationProcessor().getBone("leg_base_" + i);
            if (legBase != null) {
                float legScale = (i <= unlockedLegs) ? morph : 0.0F;
                legBase.setScaleX(legScale);
                legBase.setScaleY(legScale);
                legBase.setScaleZ(legScale);
            }
        }
    }
}
