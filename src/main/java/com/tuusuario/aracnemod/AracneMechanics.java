package com.tuusuario.aracnemod;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.UUID;

public class AracneMechanics {
    private static final UUID SPEED_MOD_UUID = UUID.fromString("8f3b2c10-9e4a-4b2c-9a11-1234567890ab");

    /** Cada 2 niveles desbloquea 1 pata, hasta 4 patas. */
    public static int getUnlockedLegs(int level) {
        if (level < 2) return 0;
        return Math.min(4, level / 2);
    }

    public static void tickPlayer(Player player) {
        if (player.level.isClientSide) return;

        int legs = getUnlockedLegs(player.experienceLevel);

        // 1. Escalar paredes desde 1 pata (nivel 2+)
        if (legs >= 1 && isTouchingWall(player)) {
            double climbSpeed = legs >= 4 ? 0.24D : 0.18D;
            if (player.zza > 0 || player.isJumping()) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x, Math.max(motion.y, climbSpeed), motion.z);
                player.fallDistance = 0.0F;
            }
        }

        // 2. Reducción de daño por caída (parcial desde 1 pata, total en 4 patas)
        if (legs >= 4) {
            player.fallDistance = 0.0F;
        } else if (legs >= 1 && player.fallDistance > 3.0F) {
            player.fallDistance = Math.max(0.0F, player.fallDistance - (legs * 1.5F));
        }

        // 3. Velocidad pasiva desde 2 patas (nivel 4+)
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MOD_UUID);
            if (legs >= 2) {
                double bonus = (legs - 1) * 0.06D; // +6%, +12%, +18%
                speedAttr.addTransientModifier(new AttributeModifier(
                        SPEED_MOD_UUID,
                        "Aracne agility bonus",
                        bonus,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
        }
    }

    /** Habilidad activa (3+ patas): zarpazo / embestida frontal. */
    public static void triggerAttackAbility(Player player) {
        int legs = getUnlockedLegs(player.experienceLevel);
        if (legs < 3 || player.level.isClientSide) return;

        Vec3 look = player.getLookAngle().normalize();
        double dashStrength = legs >= 4 ? 1.15D : 0.85D;
        player.push(look.x * dashStrength, 0.25D, look.z * dashStrength);
        player.hurtMarked = true;

        AABB hitBox = player.getBoundingBox().inflate(2.2D).expandTowards(look.scale(2.0D));
        List<LivingEntity> targets = player.level.getEntitiesOfClass(
                LivingEntity.class,
                hitBox,
                entity -> entity != player && entity.isAlive()
        );

        float damage = legs >= 4 ? 7.0F : 4.5F;
        for (LivingEntity target : targets) {
            target.hurt(net.minecraft.world.damagesource.DamageSource.playerAttack(player), damage);
            target.knockback(0.6D, -look.x, -look.z);
        }
    }

    private static boolean isTouchingWall(Player player) {
        return player.horizontalCollision;
    }
}
