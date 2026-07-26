package com.gtnewhorizons.stargatenh.client.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;

public class StargateDataProvider implements IWailaDataProvider {

    public static final StargateDataProvider INSTANCE = new StargateDataProvider();

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

        if (accessor.getTileEntity() instanceof TileStargateController controller) {
            currentTip.add(StatCollector.translateToLocal("waila.sgnh.formed_gate"));
            if (controller.hasAddress) {
                currentTip.add(SpecialChars.getRenderString("waila.sgnh.address", controller.getAddressString()));
            } else {
                currentTip.add(StatCollector.translateToLocal("waila.sgnh.not_set"));
            }
        }

        return currentTip;
    }

    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }

    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }
}
