package com.gtnewhorizons.stargatenh.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.stargatenh.common.tileentity.TileStargateController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderStargateTESR extends TileEntitySpecialRenderer {

    private static final ResourceLocation MODEL = new ResourceLocation("stargatenh", "models/stargate.obj");
    private static final ResourceLocation TEXTURE = new ResourceLocation("stargatenh", "textures/models/stargate.png");

    private final IModelCustom model;

    public RenderStargateTESR() {
        model = AdvancedModelLoader.loadModel(MODEL);
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileStargateController controller)) return;

        int facing = controller.facing;
        if (facing == -1) facing = controller.setFacing();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 2.5, z + 0.5);

        float angle = switch (facing) {
            case 2 -> 90F;
            case 4 -> 180F;
            case 3 -> -90F;
            default -> 0F;
        };
        GL11.glRotatef(angle, 0F, 1F, 0F);

        bindTexture(TEXTURE);
        model.renderOnly("static");

        float ringAngle = controller.prevRingRotation
            + (controller.ringRotation - controller.prevRingRotation) * partialTicks;
        GL11.glRotatef(ringAngle % 360f, 1F, 0F, 0F);

        model.renderOnly("spinners");

        GL11.glPopMatrix();
    }
}
