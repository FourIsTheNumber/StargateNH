package com.gtnewhorizons.stargatenh.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizons.stargatenh.ModBlocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateController extends Block {

    public final ModBlocks.StargateBlocks blockGroup;

    public BlockStargateController(ModBlocks.StargateBlocks blockGroup) {
        super(Material.iron);
        this.blockGroup = blockGroup;
        this.setBlockName(blockGroup.id + "_stargate_controller");
    }

    IIcon topIcon;
    IIcon sideIcon;
    IIcon frontIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        topIcon = iconRegister.registerIcon("stargatenh:" + blockGroup.id + "/stargate_top");
        sideIcon = iconRegister.registerIcon("stargatenh:" + blockGroup.id + "/stargate_side");
        frontIcon = iconRegister.registerIcon("stargatenh:" + blockGroup.id + "/controller");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        // Item rendering trick
        if (meta == 0) meta = 3;

        if (side == ForgeDirection.DOWN.ordinal() || side == ForgeDirection.UP.ordinal()) {
            return topIcon;
        }
        if (side == meta) return frontIcon;
        return sideIcon;
    }

    @Override
    public void onPostBlockPlaced(World worldIn, int x, int y, int z, int meta) {
        runStructureCheck(worldIn, x, y, z);
        super.onPostBlockPlaced(worldIn, x, y, z, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        byte meta = switch (MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) {
            case 0 -> 2;
            case 1 -> 5;
            case 2 -> 3;
            default -> 4;
        };
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    public void runStructureCheck(World world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z);

        if (checkBlockAndMeta(world, x, y, z, -2, 0, blockGroup.stargateBlock, 1, facing)
            && checkBlockAndMeta(world, x, y, z, -1, 0, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 1, 0, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 2, 0, blockGroup.stargateBlock, 1, facing)

            && checkBlockAndMeta(world, x, y, z, -2, 1, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 2, 1, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, -2, 2, blockGroup.stargateBlock, 1, facing)
            && checkBlockAndMeta(world, x, y, z, 2, 2, blockGroup.stargateBlock, 1, facing)
            && checkBlockAndMeta(world, x, y, z, -2, 3, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 2, 3, blockGroup.stargateBlock, 0, facing)

            && checkBlockAndMeta(world, x, y, z, -2, 4, blockGroup.stargateBlock, 1, facing)
            && checkBlockAndMeta(world, x, y, z, -1, 4, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 0, 4, blockGroup.stargateBlock, 1, facing)
            && checkBlockAndMeta(world, x, y, z, 1, 4, blockGroup.stargateBlock, 0, facing)
            && checkBlockAndMeta(world, x, y, z, 2, 4, blockGroup.stargateBlock, 1, facing)) {
            formGate(world, x, y, z, facing);
        }
    }

    private void formGate(World world, int x, int y, int z, int facing) {
        setRel(world, x, y, z, facing, -2, 0, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -1, 0, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 1, 0, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 0, blockGroup.formedGateBlock, 1);

        setRel(world, x, y, z, facing, -2, 1, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 1, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, -2, 2, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, 2, 2, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -2, 3, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 3, blockGroup.formedGateBlock, 0);

        setRel(world, x, y, z, facing, -2, 4, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -1, 4, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 0, 4, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, 1, 4, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 4, blockGroup.formedGateBlock, 1);

        world.setBlock(x, y, z, blockGroup.formedGateBlock, facing, 3);
    }

    private void deform(World world, int x, int y, int z, int facing) {
        setRel(world, x, y, z, facing, -2, 0, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -1, 0, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 1, 0, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 0, blockGroup.formedGateBlock, 1);

        setRel(world, x, y, z, facing, -2, 1, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 1, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, -2, 2, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, 2, 2, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -2, 3, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 3, blockGroup.formedGateBlock, 0);

        setRel(world, x, y, z, facing, -2, 4, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, -1, 4, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 0, 4, blockGroup.formedGateBlock, 1);
        setRel(world, x, y, z, facing, 1, 4, blockGroup.formedGateBlock, 0);
        setRel(world, x, y, z, facing, 2, 4, blockGroup.formedGateBlock, 1);
    }

    private void setRel(World world, int x, int y, int z, int facing, int offsetX, int offsetY, Block newBlock,
        int newMeta) {
        BlockPos p = rotate(offsetX, offsetY, facing);
        world.setBlock(x + p.x, y + p.y, z + p.z, newBlock, newMeta, 3);
    }

    private boolean checkBlockAndMeta(World world, int x, int y, int z, int offsetX, int offsetY, Block rBlock,
        int rMeta, int facing) {
        BlockPos p = rotate(offsetX, offsetY, facing);
        Block block = world.getBlock(x + p.x, y + p.y, z + p.z);
        int meta = world.getBlockMetadata(x + p.x, y + p.y, z + p.z);

        return rBlock == block && meta == rMeta;
    }

    private static BlockPos rotate(int dx, int dy, int facing) {
        return switch (facing) {
            case 2 -> // NORTH (-Z)
                new BlockPos(dx, dy, 0);
            case 5 -> // EAST (+X)
                new BlockPos(0, dy, -dx);
            case 3 -> // SOUTH (+Z)
                new BlockPos(-dx, dy, -0);
            case 4 -> // WEST (-X)
                new BlockPos(-0, dy, dx);
            default -> new BlockPos(dx, dy, 0);
        };
    }

    public static class ItemBlockStargateController extends ItemBlock {

        public ItemBlockStargateController(Block block) {
            super(block);
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            ItemBlock block = (ItemBlock) stack.getItem();
            if (block != null && block.field_150939_a instanceof BlockStargateController stargateBlock) {
                if (stargateBlock.blockGroup.isLegacy) {
                    return "tile.legacy_stargate_controller";
                }
            }

            return super.getUnlocalizedName(stack);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            ItemBlock block = (ItemBlock) stack.getItem();
            if (block != null && block.field_150939_a instanceof BlockStargateController stargateBlock) {
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
            if (block != null && block.field_150939_a instanceof BlockStargateController stargateBlock) {
                if (stargateBlock.blockGroup.isLegacy) {
                    tooltip.add(StatCollector.translateToLocal("tooltip." + stargateBlock.blockGroup.id));
                }
            }
            super.addInformation(stack, player, tooltip, advanced);
        }
    }
}
