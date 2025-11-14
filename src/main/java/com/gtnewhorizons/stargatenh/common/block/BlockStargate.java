package com.gtnewhorizons.stargatenh.common.block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizons.stargatenh.ModBlocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargate extends Block {

    public BlockStargate() {
        super(Material.iron);
    }

    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 3; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < 3; i++) {
            icons[i] = iconRegister.registerIcon("stargatenh:stargate_block_" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= icons.length) {
            meta = 0;
        }
        return icons[meta];
    }

    @Override
    public void onPostBlockPlaced(World worldIn, int x, int y, int z, int meta) {
        Set<BlockPos> checked = new HashSet<>();
        checked.add(new BlockPos(x, y, z));
        checkStructure(worldIn, x, y, z, checked);
        super.onPostBlockPlaced(worldIn, x, y, z, meta);
    }

    public void checkStructure(World world, int x, int y, int z, Set<BlockPos> checked) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 2) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int nx = x + dir.offsetX;
                int ny = y + dir.offsetY;
                int nz = z + dir.offsetZ;

                if (world.getBlock(nx, ny, nz) == this && checked.add(new BlockPos(nx, ny, nz))) {
                    checkStructure(world, nx, ny, nz, checked);
                }
            }
        } else {
            if (checkBlockAndMeta(world, x, y, z - 2, this, 1) && checkBlockAndMeta(world, x, y, z - 1, this, 0)
                && checkBlockAndMeta(world, x, y, z + 1, this, 0)
                && checkBlockAndMeta(world, x, y, z + 2, this, 1)) {
                formGate(world, x, y, z);
            }
        }
    }

    private void formGate(World world, int x, int y, int z) {
        world.setBlock(x, y, z - 2, ModBlocks.formedGateFakeBlock, 1, 3);
        world.setBlock(x, y, z - 1, ModBlocks.formedGateFakeBlock, 0, 3);
        world.setBlock(x, y, z + 1, ModBlocks.formedGateFakeBlock, 0, 3);
        world.setBlock(x, y, z + 2, ModBlocks.formedGateFakeBlock, 1, 3);

        world.setBlock(x, y, z, ModBlocks.formedGateFakeBlock, 2, 3);
    }

    private boolean checkBlockAndMeta(World world, int x, int y, int z, Block block, int meta) {
        return world.getBlock(x, y, z) == block && world.getBlockMetadata(x, y, z) == meta;
    }

    public static class ItemBlockStargate extends ItemBlock {

        public ItemBlockStargate(Block block) {
            super(block);
            this.setHasSubtypes(true);
            this.setMaxDamage(0);
        }

        @Override
        public int getMetadata(int damage) {
            return damage;
        }

        @Override
        public String getUnlocalizedName(final ItemStack stack) {
            return this.getUnlocalizedName() + "." + stack.getItemDamage();
        }
    }
}
