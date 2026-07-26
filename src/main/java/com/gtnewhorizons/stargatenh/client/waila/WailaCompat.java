package com.gtnewhorizons.stargatenh.client.waila;

import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate;

import mcp.mobius.waila.api.IWailaRegistrar;

@SuppressWarnings("unused")
public class WailaCompat {

    public static void callbackRegister(IWailaRegistrar registrar) {
        // registrar.registerNBTProvider((IWailaDataProvider) ModBlocks.COLLECTOR.get(), BlockCollector.class);
        registrar.registerBodyProvider(StargateDataProvider.INSTANCE, BlockFormedGate.class);
    }
}
