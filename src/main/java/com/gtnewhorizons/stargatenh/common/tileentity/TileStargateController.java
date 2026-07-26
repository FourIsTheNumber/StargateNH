package com.gtnewhorizons.stargatenh.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizons.stargatenh.common.util.StargateAddress;
import com.gtnewhorizons.stargatenh.common.util.StargateRegistry;

public class TileStargateController extends TileEntity {

    public int facing = -1;
    public boolean hasAddress = false;
    private StargateAddress address = null;
    // Position of block this gate is dialed TO
    private BlockPos dialing = null;
    // Whether or not this gate is being dialed TO
    private boolean dialed = false;

    // Animation vars
    public enum DialPhase {
        IDLE,
        SPINNING,
        LOCKED,
        CONNECTED
    }

    public DialPhase dialPhase = DialPhase.IDLE;
    private int[] dialAddress;
    private int addressIndex;
    private float animStartAngle;
    private float animDelta;
    private long animStartTick;
    private int animTotalTicks;
    private int lockTicksRemaining;

    public static final float SYMBOL_STEP = 360f / 16;

    private static final float[] CHEVRON_ANGLES = { 308.57f, 257.14f, 205.71f, 154.29f, 102.86f, 51.43f, 0f };

    public int setFacing() {
        facing = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        return facing;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord - 3, yCoord, zCoord - 3, xCoord + 2, yCoord + 4, zCoord + 2);
    }

    public float ringRotation;
    public float prevRingRotation;

    public boolean isTransmitting() {
        return dialing != null;
    }

    public boolean dialOut(int[] sigils) {
        if (dialed) return false;

        StargateAddress sga = new StargateAddress(sigils);
        if (sga.equals(address)) return false;

        StargateRegistry reg = StargateRegistry.get(worldObj);
        BlockPos dialedGate = reg.lookup(sga);
        if (dialedGate == null) return false;

        TileStargateController gate = ((TileStargateController) worldObj
            .getTileEntity(dialedGate.x, dialedGate.y, dialedGate.z));
        if (!gate.getDialed(address)) return false;

        dialing = dialedGate;
        dialAddress = sigils;
        startSpinToSymbol(sigils[0], 0);
        return true;
    }

    public boolean getDialed(StargateAddress incoming) {
        dialed = true;
        return true;
    }

    public void doTeleport(Entity entity) {
        if (entity instanceof EntityLivingBase living) {
            living.setPositionAndUpdate(dialing.x, dialing.y + 1.5, dialing.z);
        } else {
            entity.setPosition(dialing.x, dialing.y + 1.5, dialing.z);
        }
    }

    public void setAddress(int[] sigils) {
        if (hasAddress) return;

        StargateRegistry reg = StargateRegistry.get(worldObj);
        StargateAddress sga = new StargateAddress(sigils);

        if (reg.lookup(sga) == null) {
            reg.register(sga, new BlockPos(xCoord, yCoord, zCoord));
            hasAddress = true;
            address = sga;
        }
    }

    private static float shortestDelta(float from, float to) {
        float delta = ((to - from) % 360f + 360f) % 360f;
        if (delta > 180f) delta -= 360f;
        return delta;
    }

    private static float smootherStep(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t * t * t * (t * (6f * t - 15f) + 10f);
    }

    @Override
    public void updateEntity() {
        prevRingRotation = ringRotation;

        if (dialPhase == DialPhase.SPINNING) {
            long elapsed = worldObj.getWorldTime() - animStartTick;
            float t = Math.min(1f, (float) elapsed / animTotalTicks);
            ringRotation = animStartAngle + animDelta * smootherStep(t);

            if (t >= 1f) {
                ringRotation = animStartAngle + animDelta;
                dialPhase = DialPhase.LOCKED;
                lockTicksRemaining = 25;
            }

        } else if (dialPhase == DialPhase.LOCKED) {
            lockTicksRemaining--;
            if (lockTicksRemaining <= 0) {
                addressIndex++;
                if (addressIndex >= dialAddress.length) {
                    dialPhase = DialPhase.CONNECTED;
                } else {
                    startSpinToSymbol(dialAddress[addressIndex], addressIndex);
                }
            }
        }
    }

    private static float targetRotationFor(int addressIndex, int chevronIndex) {
        float symbolAngle = addressIndex * SYMBOL_STEP;
        float chevronAngle = CHEVRON_ANGLES[chevronIndex];
        return chevronAngle + symbolAngle;
    }

    private void startSpinToSymbol(int addressIndex, int chevronIndex) {
        float target = targetRotationFor(addressIndex, chevronIndex);
        animStartAngle = ringRotation;
        animDelta = shortestDelta(ringRotation, target);
        animTotalTicks = Math.max(20, (int) (Math.abs(animDelta) / 3f));
        animStartTick = worldObj.getWorldTime();
        dialPhase = DialPhase.SPINNING;
    }

    public String getAddressString() {
        return address.toString();
    }
}
