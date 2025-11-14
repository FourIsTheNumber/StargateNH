package com.gtnewhorizons.stargatenh.common.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

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
        GL11.glPushMatrix();

        GL11.glTranslated(x + 0.5, y, z + 0.5);
        bindTexture(TEXTURE);

        GL11.glRotatef(180, 0, 1, 0);
        GL11.glScalef(1.0F, 1.0F, 1.0F);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        model.renderAll();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
