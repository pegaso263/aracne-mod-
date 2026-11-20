package com.tuusuario.aracnemod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(AracneMod.MODID)
public class AracneMod {
    public static final String MODID = "aracnemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AracneMod() {
        GeckoLib.initialize();
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }
}
