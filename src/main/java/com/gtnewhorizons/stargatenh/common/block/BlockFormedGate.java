package com.gtnewhorizons.stargatenh.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizons.stargatenh.ModBlocks;
import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

/**
 * Stargate blocks will transform into this invisible block when the multi is formed. Metadata is used to track
 * the original block.
 */
public class BlockFormedGate extends BlockContainer {

    public BlockFormedGate() {
        super(Material.iron);
        setBlockName("stargate_formed");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 2;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStargateController();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            ItemStack drop = switch (meta) {
                case 0 -> new ItemStack(ModBlocks.stargateBlock, 1, 0);
                case 1 -> new ItemStack(ModBlocks.stargateBlock, 1, 1);
                case 2 -> new ItemStack(ModBlocks.stargateBlock, 1, 2);
                default -> null;
            };

            if (drop != null) dropBlockAsItem(world, x, y, z, drop);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
