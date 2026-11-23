package com.tuusuario.aracnemod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AracneMechanics {
    private static final UUID SPEED_MOD_UUID = UUID.fromString("8f3b2c10-9e4a-4b2c-9a11-1234567890ab");
    private static final UUID DAMAGE_MOD_UUID = UUID.fromString("3c7a1e20-5b8d-4f1a-8c22-9876543210fe");
    private static final Map<UUID, Boolean> TRANSFORMED_PLAYERS = new HashMap<>();

    public static int getUnlockedLegs(int level) {
        if (level < 1) return 0;
        return Math.min(4, level);
    }

    public static boolean isTransformed(Player player) {
        if (player == null) return false;
        return TRANSFORMED_PLAYERS.getOrDefault(player.getUUID(), false);
    }

    public static boolean toggleTransformation(Player player) {
        if (player == null) return false;
        boolean next = !isTransformed(player);
        TRANSFORMED_PLAYERS.put(player.getUUID(), next);
        return next;
    }

    public static void tickPlayer(Player player) {
        boolean active = isTransformed(player);
        int legs = active ? getUnlockedLegs(player.experienceLevel) : 0;

        // Escala de paredes y agarre tipo rig/extremidad aracnida
        if (legs >= 1 && player.horizontalCollision) {
            double climbSpeed = legs >= 4 ? 0.25D : 0.19D;
            Vec3 motion = player.getDeltaMovement();
            if (player.isShiftKeyDown()) {
                player.setDeltaMovement(motion.x, 0.0D, motion.z);
            } else {
                player.setDeltaMovement(motion.x, Math.max(motion.y, climbSpeed), motion.z);
            }
            player.fallDistance = 0.0F;
        }

        if (player.level.isClientSide) return;

        // Amortiguacion de caida progresiva
        if (legs >= 4) {
            player.fallDistance = 0.0F;
        } else if (legs >= 1 && player.fallDistance > 3.0F) {
            player.fallDistance = Math.max(0.0F, player.fallDistance - (legs * 1.5F));
        }

        // Velocidad de desplazamiento al desplegar extremidades (nivel 2+)
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MOD_UUID);
            if (legs >= 2) {
                double bonus = (legs - 1) * 0.06D;
                speedAttr.addTransientModifier(new AttributeModifier(
                        SPEED_MOD_UUID,
                        "Aracne agility bonus",
                        bonus,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }
        }

        // Bono aditivo sin sobrescribir el golpe principal de la espada
        AttributeInstance damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.removeModifier(DAMAGE_MOD_UUID);
            if (legs >= 3) {
                double extraDamage = (legs >= 4) ? 3.0D : 1.5D;
                damageAttr.addTransientModifier(new AttributeModifier(
                        DAMAGE_MOD_UUID,
                        "Aracne limb bonus",
                        extraDamage,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    public static void triggerAttackAbility(Player player) {
        if (!isTransformed(player)) return;
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

        float splashDamage = legs >= 4 ? 3.5F : 2.0F;
        for (LivingEntity target : targets) {
            if (target.hurtTime == 0) {
                target.hurt(DamageSource.playerAttack(player), splashDamage);
                target.invulnerableTime = 0;
                target.knockback(0.4D, -look.x, -look.z);
            }
        }
    }
}
