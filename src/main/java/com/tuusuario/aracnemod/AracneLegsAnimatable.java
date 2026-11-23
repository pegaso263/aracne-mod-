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
    private boolean moving = false;
    private boolean climbing = false;
    private boolean sneaking = false;
    private float gaitPhase = 0.0F;
    private float morphProgress = 0.0F;
    private final float[] legProgress = new float[]{0.0F, 0.0F, 0.0F, 0.0F};

    public void updateMotionState(int level, boolean attacking, float speedAmount, boolean climbing, boolean sneaking) {
        this.playerLevel = level;
        this.attacking = attacking;
        this.moving = speedAmount > 0.02F;
        this.climbing = climbing;
        this.sneaking = sneaking;

        if (this.moving || this.climbing) {
            float stepRate = this.climbing ? 0.34F : Math.min(0.45F, 0.14F + speedAmount * 0.45F);
            this.gaitPhase += stepRate;
        } else {
            this.gaitPhase += 0.04F;
        }
    }

    public void tickMorph(boolean transformed, int unlockedLegs) {
        float targetAbdomen = (transformed && unlockedLegs > 0) ? 1.0F : 0.0F;
        float speed = 0.10F;

        if (morphProgress < targetAbdomen) {
            morphProgress = Math.min(targetAbdomen, morphProgress + speed);
        } else if (morphProgress > targetAbdomen) {
            morphProgress = Math.max(targetAbdomen, morphProgress - speed);
        }

        for (int i = 0; i < 4; i++) {
            float targetLeg = (transformed && (i + 1) <= unlockedLegs) ? 1.0F : 0.0F;
            if (legProgress[i] < targetLeg) {
                legProgress[i] = Math.min(targetLeg, legProgress[i] + speed);
            } else if (legProgress[i] > targetLeg) {
                legProgress[i] = Math.max(targetLeg, legProgress[i] - speed);
            }
        }
    }

    public float getMorphProgress() {
        return morphProgress;
    }

    public float getLegProgress(int index) {
        if (index < 0 || index >= legProgress.length) return 0.0F;
        return legProgress[index];
    }

    public float getGaitPhase() {
        return gaitPhase;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isClimbing() {
        return climbing;
    }

    public boolean isSneaking() {
        return sneaking;
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
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
