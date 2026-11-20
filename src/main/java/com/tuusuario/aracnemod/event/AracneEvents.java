package com.tuusuario.aracnemod.event;

import com.tuusuario.aracnemod.AracneMechanics;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "aracnemod")
public class AracneEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player != null) {
            AracneMechanics.tickPlayer(event.player);
        }
    }
}
