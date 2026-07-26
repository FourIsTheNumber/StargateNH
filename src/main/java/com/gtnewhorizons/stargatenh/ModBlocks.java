package com.gtnewhorizons.stargatenh;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.stargatenh.common.block.BlockDialingDevice;
import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargate.ItemBlockStargate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargateController;

import codechicken.nei.api.API;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final Block dialingDeviceBlock = new BlockDialingDevice().setBlockName("dialing_device");

    public static void init() {
        GameRegistry.registerBlock(dialingDeviceBlock, "dialing_device");

        for (StargateBlocks blocks : StargateBlocks.values()) {
            GameRegistry.registerBlock(blocks.stargateBlock, ItemBlockStargate.class, blocks.id + "_stargate_block");
            GameRegistry.registerBlock(blocks.controllerBlock, blocks.id + "_stargate_controller");
            GameRegistry.registerBlock(blocks.formedGateBlock, blocks.id + "_stargate_formed");

            API.hideItem(new ItemStack(blocks.formedGateBlock));
        }
    }

    public enum StargateBlocks {

        Default("default"),
        SplitOrigin("split_origin"),
        HarmonicBreakthrough("harmonic_breakthrough"),
        PolychromeContest("polychrome_contest"),
        DimensionalDuplicity("dimensional_duplicity"),
        // HeavenlyFire("heavenly_fire"),
        ;

        public final String id;
        public final BlockStargate stargateBlock;
        public final BlockStargateController controllerBlock;
        public final BlockFormedGate formedGateBlock;

        StargateBlocks(String id) {
            this.id = id;
            stargateBlock = new BlockStargate(id);
            controllerBlock = new BlockStargateController(this);
            formedGateBlock = new BlockFormedGate(this);
        }
    }
}
