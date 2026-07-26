package com.gtnewhorizons.stargatenh.common.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizons.stargatenh.common.block.BlockFormedGate;

public class StructureUtil {

    public static void setRel(World world, int x, int y, int z, int facing, int offsetX, int offsetY, Block newBlock,
        int newMeta) {
        BlockPos p = rotate(offsetX, offsetY, facing);
        world.setBlock(x + p.x, y + p.y, z + p.z, newBlock, newMeta, 3);
    }

    public static void deformRel(World world, int x, int y, int z, int facing, int offsetX, int offsetY) {
        BlockPos p = rotate(offsetX, offsetY, facing);
        p.x = p.x + x;
        p.y = p.y + y;
        p.z = p.z + z;
        if (!(world.getBlock(p.x, p.y, p.z) instanceof BlockFormedGate gate)) return;

        int meta = world.getBlockMetadata(p.x, p.y, p.z);
        world.setBlock(
            p.x,
            p.y,
            p.z,
            (meta > 1) ? gate.blockGroup.controllerBlock : gate.blockGroup.stargateBlock,
            meta,
            3);
    }

    public static boolean checkBlockAndMeta(World world, int x, int y, int z, int offsetX, int offsetY, Block rBlock,
        int rMeta, int facing) {
        BlockPos p = rotate(offsetX, offsetY, facing);
        Block block = world.getBlock(x + p.x, y + p.y, z + p.z);
        int meta = world.getBlockMetadata(x + p.x, y + p.y, z + p.z);

        return rBlock == block && meta == rMeta;
    }

    public static BlockPos rotate(int dx, int dy, int facing) {
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
}
