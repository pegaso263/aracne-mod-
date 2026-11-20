package com.tuusuario.aracnemod.event;

import com.tuusuario.aracnemod.AracneMod;
import com.tuusuario.aracnemod.capability.AracneData;
import com.tuusuario.aracnemod.capability.AracneDataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AracneMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(AracneDataProvider.ARACNE_DATA).isPresent()) {
                event.addCapability(new ResourceLocation(AracneMod.MODID, "properties"), new AracneDataProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(AracneDataProvider.ARACNE_DATA).ifPresent(oldStore -> {
                event.getEntity().getCapability(AracneDataProvider.ARACNE_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AracneData.class);
    }

    @SubscribeEvent
    public static void onPlayerLevelUp(PlayerXpEvent.LevelChange event) {
        Player player = event.getEntity();
        if (player.level.isClientSide) return;

        int newLevel = player.experienceLevel + event.getLevels();

        player.getCapability(AracneDataProvider.ARACNE_DATA).ifPresent(data -> {
            if (newLevel >= 5 && data.getPhase() < 1) {
                data.setPhase(1);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 1: Escalar paredes (+10% velocidad)."));
            }
            if (newLevel >= 10 && data.getSpiderLegs() < 1) {
                data.setPhase(2);
                data.setSpiderLegs(1);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 2: 1 pata de araña en la espalda."));
            }
            if (newLevel >= 15 && data.getSpiderLegs() < 2) {
                data.setPhase(3);
                data.setSpiderLegs(2);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 3: 2 patas en la espalda."));
            }
            if (newLevel >= 20 && data.getSpiderLegs() < 3) {
                data.setSpiderLegs(3);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 3: 3 patas en la espalda."));
            }
            if (newLevel >= 25 && data.getSpiderLegs() < 4) {
                data.setSpiderLegs(4);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 3: 4 patas en la espalda."));
            }
            if (newLevel >= 30 && data.getPhase() < 4) {
                data.setPhase(4);
                player.sendSystemMessage(Component.literal("§5[Aracne] Fase 4: Transformación desbloqueada."));
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;

        player.getCapability(AracneDataProvider.ARACNE_DATA).ifPresent(data -> {
            if (data.getPhase() >= 1 && player.horizontalCollision) {
                Vec3 motion = player.getDeltaMovement();
                if (!player.isShiftKeyDown()) {
                    player.setDeltaMovement(motion.x, 0.22D, motion.z);
                } else {
                    player.setDeltaMovement(motion.x, 0.0D, motion.z);
                }
                player.fallDistance = 0.0F;
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level.isClientSide) return;

        if (event.getTarget() instanceof LivingEntity target) {
            player.getCapability(AracneDataProvider.ARACNE_DATA).ifPresent(data -> {
                int legs = data.getSpiderLegs();
                if (legs > 0) {
                    float extraDamage = legs * 1.5F;
                    target.invulnerableTime = 0;
                    target.hurt(DamageSource.playerAttack(player), extraDamage);
                }
            });
        }
    }
}
