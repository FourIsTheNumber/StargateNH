package com.gtnewhorizons.stargatenh.client.waila;

import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderAddress implements IWailaTooltipRenderer {

    public static void register() {
        ModuleRegistrar.instance()
            .registerTooltipRenderer("waila.sgnh.address", new TTRenderAddress());
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);

        FontRenderer galactic = Minecraft.getMinecraft().standardGalacticFontRenderer;

        String address = EnumChatFormatting.WHITE + params[0];
        galactic.drawStringWithShadow(address, 0, 0, OverlayConfig.fontcolor);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        FontRenderer galactic = Minecraft.getMinecraft().standardGalacticFontRenderer;
        return new Dimension(galactic.getStringWidth(params[0]), 8);
    }
}
