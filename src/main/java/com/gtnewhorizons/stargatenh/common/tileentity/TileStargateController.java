package com.gtnewhorizons.stargatenh.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileStargateController extends TileEntity {

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 3, yCoord, zCoord - 3, xCoord + 2, yCoord + 4, zCoord + 2);
    }
}
