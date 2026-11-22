package com.tuusuario.aracnemod;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class AracneLegsAnimatable implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int playerLevel = 0;
    private boolean attacking = false;
    private float morphProgress = 0.0F;

    public void updateState(int level, boolean attacking) {
        this.playerLevel = level;
        this.attacking = attacking;
    }

    public void tickMorph(boolean hasLegsUnlocked) {
        float target = hasLegsUnlocked ? 1.0F : 0.0F;
        float speed = 0.08F;
        if (morphProgress < target) {
            morphProgress = Math.min(target, morphProgress + speed);
        } else if (morphProgress > target) {
            morphProgress = Math.max(target, morphProgress - speed);
        }
    }

    public float getMorphProgress() {
        return morphProgress;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        if (attacking) {
            controller.setAnimation(new AnimationBuilder()
                    .addAnimation("animation.rachnera_form.attack", EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        controller.setAnimation(new AnimationBuilder()
                .addAnimation("animation.rachnera_form.idle", EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
