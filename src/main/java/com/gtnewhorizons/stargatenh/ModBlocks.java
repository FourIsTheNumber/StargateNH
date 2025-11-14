package com.gtnewhorizons.stargatenh;

import net.minecraft.block.Block;

import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargate;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final Block stargateBlock = new BlockStargate().setBlockName("stargate_block");
    public static final Block formedGateFakeBlock = new BlockFormedGate().setBlockName("formed_stargate");

    public static void init() {
        GameRegistry.registerBlock(stargateBlock, BlockStargate.ItemBlockStargate.class, "stargate_block");
        GameRegistry.registerBlock(formedGateFakeBlock, "formed_stargate");
    }
}
