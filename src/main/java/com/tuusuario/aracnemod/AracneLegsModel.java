package com.tuusuario.aracnemod;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AracneLegsModel extends AnimatedGeoModel<AracneLegsAnimatable> {
    private static final ResourceLocation MODEL =
            new ResourceLocation("aracnemod", "geo/aracne_legs.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("aracnemod", "textures/entity/aracne_legs.png");
    private static final ResourceLocation ANIMATION =
            new ResourceLocation("aracnemod", "animations/aracne_legs.animation.json");

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

        int unlockedLegs = AracneMechanics.getUnlockedLegs(entity.getPlayerLevel());
        for (int i = 1; i <= 4; i++) {
            IBone legBase = getAnimationProcessor().getBone("leg_base_" + i);
            if (legBase != null) {
                float scale = (i <= unlockedLegs) ? 1.0F : 0.0F;
                legBase.setScaleX(scale);
                legBase.setScaleY(scale);
                legBase.setScaleZ(scale);
            }
        }
    }
}
