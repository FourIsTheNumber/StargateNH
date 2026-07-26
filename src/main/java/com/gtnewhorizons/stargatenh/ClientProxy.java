package com.gtnewhorizons.stargatenh;

import com.gtnewhorizons.stargatenh.client.render.RenderStargateTESR;
import com.gtnewhorizons.stargatenh.client.waila.TTRenderAddress;
import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage(
                "Waila",
                "register",
                "com.gtnewhorizons.stargatenh.client.waila.WailaCompat.callbackRegister");
        }
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileStargateController.class, new RenderStargateTESR());
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("Waila")) {
            TTRenderAddress.register();
        }
        super.postInit(event);
    }
}
