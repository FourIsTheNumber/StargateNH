package com.gtnewhorizons.stargatenh.common.tileentity;

import static com.gtnewhorizons.stargatenh.StargateNH.MODID;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizons.stargatenh.client.ui.UITextures;
import com.gtnewhorizons.stargatenh.common.util.StargateAddress;
import com.gtnewhorizons.stargatenh.common.util.StargateRegistry;

public class TileDialingDevice extends TileEntity implements IGuiHolder<PosGuiData> {

    private final int[] registryAddress = { 0, 0, 0, 0, 0, 0, 0 };
    TileStargateController controller;

    private final int[] dialingAddress = { 0, 0, 0, 0, 0, 0, 0 };

    public void connectGate() {
        if (worldObj.getTileEntity(xCoord + 4, yCoord, zCoord) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord - 4, yCoord, zCoord) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 4) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 4) instanceof TileStargateController c) controller = c;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("panel").size(176, 100);
        if (controller == null) {
            panel.child(
                IKey.lang("stargatenh.gui.dialing_device.link_failed")
                    .asWidget()
                    .marginLeft(5)
                    .marginRight(5)
                    .marginTop(5)
                    .marginBottom(-15));
        } else if (controller.hasAddress) buildDialingUI(panel, syncManager);
        else buildSetupUI(panel, syncManager);

        return panel;
    }

    private void buildSetupUI(ModularPanel panel, PanelSyncManager syncManager) {
        StargateRegistry reg = StargateRegistry.get(worldObj);
        BooleanSyncValue isUnique = new BooleanSyncValue(
            () -> reg.lookup(new StargateAddress(registryAddress)) == null);

        IntSyncValue[] chevrons = new IntSyncValue[7];
        for (int i = 0; i < chevrons.length; i++) {
            int fi = i;
            chevrons[i] = new IntSyncValue(() -> registryAddress[fi], x -> registryAddress[fi] = x).allowC2S();
            syncManager.syncValue("chevron" + i, chevrons[i]);
        }

        syncManager.syncValue("isUnique", isUnique);

        syncManager.registerServerSyncedAction("set_address", $ -> { controller.setAddress(registryAddress); });

        syncManager.registerServerSyncedAction("generate_random", $ -> {
            Random rng = new Random();
            do {
                for (int i = 0; i < registryAddress.length; i++) {
                    registryAddress[i] = rng.nextInt(16);
                }
            } while (reg.lookup(new StargateAddress(registryAddress)) != null);
        });

        panel.child(
            IKey.lang("stargatenh.gui.dialing_device.set_address")
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15));

        Flow sigils = Flow.row();
        panel.child(
            sigils.size(156, 16)
                .marginTop(20)
                .marginLeft(14)
                .childPadding(6));

        for (int i = 0; i < chevrons.length; i++) {
            int fi = i;
            sigils.child(
                new CycleButtonWidget().syncHandler("chevron" + fi)
                    .size(16, 16)
                    .background(UITextures.SIGIL_BG)
                    .hoverBackground(UITextures.SIGIL_BG_ACTIVE)
                    .length(16)
                    .overlay(new DynamicDrawable(() -> UITextures.getSigil(chevrons[fi].getIntValue()))));
        }

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(8)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.generate_random")))
                .overlay(UITextures.OVERLAY_RANDOM)
                .onMousePressed(mouseButton -> {
                    syncManager.callSyncedAction("generate_random");
                    return true;
                }));

        panel.child(
            IKey.lang(
                () -> isUnique.getBoolValue() ? "stargatenh.gui.dialing_device.address.available"
                    : "stargatenh.gui.dialing_device.address.in_use")
                .asWidget()
                .marginLeft(36)
                .marginTop(52));

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(148)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.lock_address")))
                .setEnabledIf(ignored -> isUnique.getBoolValue())
                .overlay(UITextures.OVERLAY_CHECK)
                .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                    syncManager.callSyncedAction("set_address");
                    panel.closeIfOpen();
                })));
    }

    private void buildDialingUI(ModularPanel panel, PanelSyncManager syncManager) {
        panel.child(
            IKey.lang("stargatenh.gui.dialing_device.operational")
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15));

        IntSyncValue[] chevrons = new IntSyncValue[7];
        for (int i = 0; i < chevrons.length; i++) {
            int fi = i;
            chevrons[i] = new IntSyncValue(() -> dialingAddress[fi], x -> dialingAddress[fi] = x).allowC2S();
            syncManager.syncValue("chevron" + i, chevrons[i]);
        }

        Flow sigils = Flow.row();
        panel.child(
            sigils.size(156, 16)
                .marginTop(20)
                .marginLeft(14)
                .childPadding(6));

        for (int i = 0; i < chevrons.length; i++) {
            int fi = i;
            sigils.child(
                new CycleButtonWidget().syncHandler("chevron" + fi)
                    .size(16, 16)
                    .background(UITextures.SIGIL_BG)
                    .hoverBackground(UITextures.SIGIL_BG_ACTIVE)
                    .length(16)
                    .overlay(new DynamicDrawable(() -> UITextures.getSigil(chevrons[fi].getIntValue()))));
        }

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(148)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.dial_address")))
                .overlay(UITextures.OVERLAY_CHECK)
                .syncHandler(
                    new InteractionSyncHandler().allowC2S()
                        .setOnMousePressed(mouseData -> { controller.dialOut(dialingAddress); })));
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(MODID, mainPanel);
    }
}
