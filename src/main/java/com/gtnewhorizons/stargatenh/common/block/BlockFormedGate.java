package com.gtnewhorizons.stargatenh.common.block;

import static com.gtnewhorizons.stargatenh.common.util.StructureUtil.checkBlockAndMeta;
import static com.gtnewhorizons.stargatenh.common.util.StructureUtil.deformRel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizons.stargatenh.ModBlocks;
import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

/**
 * Stargate blocks will transform into this invisible block when the multi is formed. Metadata is used to track
 * the original block.
 */
public class BlockFormedGate extends BlockContainer {

    public final ModBlocks.StargateBlocks blockGroup;

    public BlockFormedGate(ModBlocks.StargateBlocks blockGroup) {
        super(Material.iron);
        this.blockGroup = blockGroup;
        setBlockName(blockGroup.id + "_stargate_formed");
        setBlockTextureName("stargatenh:stargate_formed");
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
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        ItemStack drop = switch (meta) {
            case 0 -> new ItemStack(blockGroup.stargateBlock, 1, 0);
            case 1 -> new ItemStack(blockGroup.stargateBlock, 1, 1);
            default -> new ItemStack(blockGroup.controllerBlock, 1, 0);
        };
        drops.add(drop);
        return drops;
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 2) return Item.getItemFromBlock(blockGroup.stargateBlock);
        return Item.getItemFromBlock(blockGroup.controllerBlock);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
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

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (meta > 1) deform(worldIn, x, y, z, meta);
        else {
            Set<BlockPos> checked = new HashSet<>();
            checked.add(new BlockPos(x, y, z));
            findController(worldIn, x, y, z, checked);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public void findController(World world, int x, int y, int z, Set<BlockPos> checked) {
        // Hard limit so nothing horrible happens
        if (checked.size() > 20) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 1) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int nx = x + dir.offsetX;
                int ny = y + dir.offsetY;
                int nz = z + dir.offsetZ;

                Block neighbor = world.getBlock(nx, ny, nz);

                if (neighbor instanceof BlockFormedGate controller && world.getBlockMetadata(nx, ny, nz) > 1) {
                    controller.runDeformCheck(world, nx, ny, nz);
                } else if (neighbor == this && checked.add(new BlockPos(nx, ny, nz))) {
                    findController(world, nx, ny, nz, checked);
                }
            }
        }
    }

    public void runDeformCheck(World world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z);

        if (!checkBlockAndMeta(world, x, y, z, -2, 0, blockGroup.formedGateBlock, 1, facing)
            || !checkBlockAndMeta(world, x, y, z, -1, 0, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 1, 0, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 2, 0, blockGroup.formedGateBlock, 1, facing)

            || !checkBlockAndMeta(world, x, y, z, -2, 1, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 2, 1, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, -2, 2, blockGroup.formedGateBlock, 1, facing)
            || !checkBlockAndMeta(world, x, y, z, 2, 2, blockGroup.formedGateBlock, 1, facing)
            || !checkBlockAndMeta(world, x, y, z, -2, 3, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 2, 3, blockGroup.formedGateBlock, 0, facing)

            || !checkBlockAndMeta(world, x, y, z, -2, 4, blockGroup.formedGateBlock, 1, facing)
            || !checkBlockAndMeta(world, x, y, z, -1, 4, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 0, 4, blockGroup.formedGateBlock, 1, facing)
            || !checkBlockAndMeta(world, x, y, z, 1, 4, blockGroup.formedGateBlock, 0, facing)
            || !checkBlockAndMeta(world, x, y, z, 2, 4, blockGroup.formedGateBlock, 1, facing)) {
            deform(world, x, y, z, facing);
        }
    }

    private void deform(World world, int x, int y, int z, int facing) {
        deformRel(world, x, y, z, facing, -2, 0);
        deformRel(world, x, y, z, facing, -1, 0);
        deformRel(world, x, y, z, facing, 0, 0);
        deformRel(world, x, y, z, facing, 1, 0);
        deformRel(world, x, y, z, facing, 2, 0);

        deformRel(world, x, y, z, facing, -2, 1);
        deformRel(world, x, y, z, facing, 2, 1);
        deformRel(world, x, y, z, facing, -2, 2);
        deformRel(world, x, y, z, facing, 2, 2);
        deformRel(world, x, y, z, facing, -2, 3);
        deformRel(world, x, y, z, facing, 2, 3);

        deformRel(world, x, y, z, facing, -2, 4);
        deformRel(world, x, y, z, facing, -1, 4);
        deformRel(world, x, y, z, facing, 0, 4);
        deformRel(world, x, y, z, facing, 1, 4);
        deformRel(world, x, y, z, facing, 2, 4);
    }

    public static class ItemBlockFormedGate extends ItemBlock {

        public ItemBlockFormedGate(Block block) {
            super(block);
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            ItemBlock block = (ItemBlock) stack.getItem();
            if (block != null && block.field_150939_a instanceof BlockFormedGate stargateBlock) {
                if (stargateBlock.blockGroup.isLegacy) {
                    return "tile.legacy_stargate_formed";
                }
            }

            return super.getUnlocalizedName(stack);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            ItemBlock block = (ItemBlock) stack.getItem();
            if (block != null && block.field_150939_a instanceof BlockFormedGate stargateBlock) {
                if (stargateBlock.blockGroup.isLegacy) {
                    return StatCollector.translateToLocalFormatted(
                        getUnlocalizedName(stack) + ".name",
                        StatCollector.translateToLocal("affix." + stargateBlock.blockGroup.id));
                }
            }
            return super.getItemStackDisplayName(stack);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
            ItemBlock block = (ItemBlock) stack.getItem();
            if (block != null && block.field_150939_a instanceof BlockFormedGate stargateBlock) {
                if (stargateBlock.blockGroup.isLegacy) {
                    tooltip.add(StatCollector.translateToLocal("tooltip." + stargateBlock.blockGroup.id));
                }
            }
            super.addInformation(stack, player, tooltip, advanced);
        }
    }
}
