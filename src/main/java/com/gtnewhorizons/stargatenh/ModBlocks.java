package com.gtnewhorizons.stargatenh;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.stargatenh.common.block.BlockDialingDevice;
import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate;
import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate.ItemBlockFormedGate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargate.ItemBlockStargate;
import com.gtnewhorizons.stargatenh.common.block.BlockStargateController;
import com.gtnewhorizons.stargatenh.common.block.BlockStargateController.ItemBlockStargateController;

import codechicken.nei.api.API;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final Block dialingDeviceBlock = new BlockDialingDevice().setBlockName("dialing_device");

    public static void init() {
        GameRegistry.registerBlock(dialingDeviceBlock, "dialing_device");

        for (StargateBlocks blocks : StargateBlocks.values()) {
            GameRegistry.registerBlock(blocks.stargateBlock, ItemBlockStargate.class, blocks.id + "_stargate_block");
            GameRegistry.registerBlock(
                blocks.controllerBlock,
                ItemBlockStargateController.class,
                blocks.id + "_stargate_controller");
            GameRegistry
                .registerBlock(blocks.formedGateBlock, ItemBlockFormedGate.class, blocks.id + "_stargate_formed");

            API.hideItem(new ItemStack(blocks.formedGateBlock));
        }
    }

    public enum StargateBlocks {

        Default("default", false),
        SplitOrigin("split_origin", true),
        PolychromeContest("polychrome_contest", true),
        DimensionalDuplicity("dimensional_duplicity", true),
        HarmonicBreakthrough("harmonic_breakthrough", true),
        HeavenlyFire("heavenly_fire", true),;

        public final String id;
        public final boolean isLegacy;
        public final BlockStargate stargateBlock;
        public final BlockStargateController controllerBlock;
        public final BlockFormedGate formedGateBlock;

        StargateBlocks(String id, boolean isLegacy) {
            this.id = id;
            this.isLegacy = isLegacy;
            stargateBlock = new BlockStargate(this);
            controllerBlock = new BlockStargateController(this);
            formedGateBlock = new BlockFormedGate(this);
        }
    }
}
