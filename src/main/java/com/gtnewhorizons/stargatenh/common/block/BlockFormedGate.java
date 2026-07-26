package com.gtnewhorizons.stargatenh.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.gtnewhorizons.stargatenh.ModBlocks;
import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

/**
 * Stargate blocks will transform into this invisible block when the multi is formed. Metadata is used to track
 * the original block.
 */
public class BlockFormedGate extends BlockContainer {

    private final ModBlocks.StargateBlocks blockGroup;

    public BlockFormedGate(ModBlocks.StargateBlocks blockGroup) {
        super(Material.iron);
        this.blockGroup = blockGroup;
        setBlockName(blockGroup.id + "_stargate_formed");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata > 1;
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
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (!(world.getTileEntity(x, y, z) instanceof TileStargateController controller))
            return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        if (controller.facing == 3 || controller.facing == 2) {
            return AxisAlignedBB.getBoundingBox(x - 2, y, z, x + 3, y + 5, z + 1);
        } else {
            return AxisAlignedBB.getBoundingBox(x, y, z - 2, x + 1, y + 5, z + 3);
        }
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            ItemStack drop = switch (meta) {
                case 0 -> new ItemStack(blockGroup.stargateBlock, 1, 0);
                case 1 -> new ItemStack(blockGroup.stargateBlock, 1, 1);
                case 2 -> new ItemStack(blockGroup.controllerBlock, 1, 0);
                default -> null;
            };

            if (drop != null) dropBlockAsItem(world, x, y, z, drop);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collider) {
        super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);

        if (world.isRemote) return;
        if (!(world.getTileEntity(x, y, z) instanceof TileStargateController controller)) return;
        if (!controller.isTransmitting()) return;

        if (collider instanceof EntityLivingBase entity) {
            AxisAlignedBB teleportBox;
            if (controller.facing == 0 || controller.facing == 2) {
                teleportBox = AxisAlignedBB.getBoundingBox(x - 1, y + 1, z, x + 2, y + 4, z + 1);
            } else {
                teleportBox = AxisAlignedBB.getBoundingBox(x, y + 1, z - 1, x + 1, y + 4, z + 2);
            }

            if (teleportBox.intersectsWith(mask)) {
                controller.doTeleport(entity);
            }
        }
    }
}
