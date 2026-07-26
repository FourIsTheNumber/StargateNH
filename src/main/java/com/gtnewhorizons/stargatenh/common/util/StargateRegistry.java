package com.gtnewhorizons.stargatenh.common.util;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class StargateRegistry extends WorldSavedData {

    public static StargateRegistry INSTANCE;
    public static final String DATA_NAME = "StargateRegistry";

    private final BiMap<StargateAddress, BlockPos> registry = HashBiMap.create();

    public StargateRegistry() {
        super(DATA_NAME);
    }

    @SuppressWarnings("unused")
    public StargateRegistry(String name) {
        super(name);
    }

    public void register(StargateAddress addr, BlockPos pos) {
        registry.put(addr, pos);
        markDirty();
    }

    public void unregister(StargateAddress addr) {
        registry.remove(addr);
        markDirty();
    }

    public BlockPos lookup(StargateAddress addr) {
        return registry.get(addr);
    }

    public StargateAddress lookup(BlockPos pos) {
        return registry.inverse()
            .get(pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        registry.clear();
        NBTTagList list = nbt.getTagList("Entries", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);

            int[] sigils = entry.getIntArray("addr");
            StargateAddress addr = new StargateAddress(sigils);

            int x = entry.getInteger("x");
            int y = entry.getInteger("y");
            int z = entry.getInteger("z");

            registry.put(addr, new BlockPos(x, y, z));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();

        for (Map.Entry<StargateAddress, BlockPos> e : registry.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setIntArray("addr", e.getKey().sigils);

            BlockPos pos = e.getValue();
            tag.setInteger("x", pos.x);
            tag.setInteger("y", pos.y);
            tag.setInteger("z", pos.z);

            list.appendTag(tag);
        }

        nbt.setTag("Entries", list);
    }

    public static class RegisterEvent {

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
                MapStorage storage = event.world.mapStorage;
                INSTANCE = (StargateRegistry) storage.loadData(StargateRegistry.class, DATA_NAME);
                if (INSTANCE == null) {
                    INSTANCE = new StargateRegistry();
                    storage.setData(DATA_NAME, INSTANCE);
                }
                INSTANCE.markDirty();
            }
        }
    }
}
