package com.gtnewhorizons.stargatenh.common.tileentity;

import static com.gtnewhorizons.stargatenh.StargateNH.MODID;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.IntValue;
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

    TileStargateController controller;

    private final int[] dialingAddress = { -1, -1, -1, -1, -1, -1, -1 };
    private final int[] registeredAddress = { 0, 0, 0, 0, 0, 0, 0 };

    public void connectGate() {
        if (worldObj.getTileEntity(xCoord + 4, yCoord, zCoord) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord - 4, yCoord, zCoord) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 4) instanceof TileStargateController c) controller = c;
        else if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 4) instanceof TileStargateController c) controller = c;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("panel").size(176, 100);

        BooleanSyncValue hasAddressSyncer = new BooleanSyncValue(
            () -> controller != null && controller.hasAddress,
            val -> {
                if (controller != null) {
                    controller.hasAddress = val;
                }
            });
        syncManager.syncValue("hasAddress", hasAddressSyncer);

        IntSyncValue[] chevrons = new IntSyncValue[7];
        for (int i = 0; i < chevrons.length; i++) {
            int fi = i;
            chevrons[i] = new IntSyncValue(() -> dialingAddress[fi], x -> dialingAddress[fi] = x).allowC2S();
            syncManager.syncValue("chevron" + i, chevrons[i]);
        }

        IntSyncValue[] controllerChevrons = new IntSyncValue[7];
        for (int i = 0; i < controllerChevrons.length; i++) {
            int fi = i;
            controllerChevrons[i] = new IntSyncValue(() -> {
                if (controller == null || controller.getAddress() == null) return 0;
                return controller.getAddress().sigils[fi];
            }, val -> registeredAddress[fi] = val);
            syncManager.syncValue("controllerChevron" + i, controllerChevrons[i]);
        }

        panel.child(
            IKey.lang("stargatenh.gui.dialing_device.link_failed")
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
                .setEnabledIf($ -> controller == null));

        buildDialingUI(panel, syncManager);
        buildSetupUI(panel, syncManager);

        return panel;
    }

    private void buildSetupUI(ModularPanel panel, PanelSyncManager syncManager) {
        BooleanSyncValue isUnique = new BooleanSyncValue(
            () -> StargateRegistry.INSTANCE.lookup(new StargateAddress(dialingAddress)) == null);

        syncManager.syncValue("isUnique", isUnique);

        panel.child(
            IKey.lang("stargatenh.gui.dialing_device.set_address")
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
                .setEnabledIf($ -> controller != null && !hasAddress()));

        Flow sigils = Flow.row();
        panel.child(
            sigils.size(156, 16)
                .marginTop(20)
                .marginLeft(14)
                .childPadding(6)
                .setEnabledIf($ -> controller != null && !hasAddress()));

        for (int i = 0; i < dialingAddress.length; i++) {
            int fi = i;

            // Use the dialing address value, but being careful to handle -1 (unset).
            // We do not need -1 as a meaningful value here, but have to set it to 0 if we encounter it.
            IntValue.Dynamic value = new IntValue.Dynamic(() -> {
                if (dialingAddress[fi] == -1) {
                    return 0;
                }
                return dialingAddress[fi];
            }, val -> {
                IntSyncValue syncer = syncManager.findSyncHandler("chevron" + fi, IntSyncValue.class);
                syncer.setIntValue(val);
            });

            sigils.child(
                new CycleButtonWidget().value(value)
                    .size(16, 16)
                    .background(UITextures.SIGIL_BG)
                    .hoverBackground(UITextures.SIGIL_BG_ACTIVE)
                    .length(16)
                    .overlay(new DynamicDrawable(() -> {
                        int sigilId = dialingAddress[fi];
                        if (sigilId == -1) {
                            sigilId = 0;
                        }
                        return UITextures.getSigil(sigilId);
                    })));
        }

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(8)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.generate_random")))
                .overlay(UITextures.OVERLAY_RANDOM)
                .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                    if (!syncManager.isClient()) {
                        Random rng = new Random();
                        do {
                            for (int i = 0; i < dialingAddress.length; i++) {
                                dialingAddress[i] = rng.nextInt(16);
                            }
                        } while (StargateRegistry.INSTANCE.lookup(new StargateAddress(dialingAddress)) != null);
                    }
                }))
                .setEnabledIf($ -> controller != null && !hasAddress()));

        panel.child(
            IKey.lang(
                () -> isUnique.getBoolValue() ? "stargatenh.gui.dialing_device.address.available"
                    : "stargatenh.gui.dialing_device.address.in_use")
                .asWidget()
                .marginLeft(36)
                .marginTop(52)
                .setEnabledIf($ -> controller != null && !hasAddress()));

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(148)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.lock_address")))
                .setEnabledIf(ignored -> isUnique.getBoolValue())
                .overlay(UITextures.OVERLAY_CHECK)
                .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseButton -> {
                    if (!syncManager.isClient()) {
                        controller.setAddress(dialingAddress);
                        Arrays.fill(dialingAddress, -1);
                    }
                    panel.closeIfOpen();
                }))
                .setEnabledIf($ -> controller != null && !hasAddress()));
    }

    private void buildDialingUI(ModularPanel panel, PanelSyncManager syncManager) {
        panel.child(
            IKey.lang("stargatenh.gui.dialing_device.operational")
                .asWidget()
                .marginLeft(5)
                .marginRight(5)
                .marginTop(5)
                .marginBottom(-15)
                .setEnabledIf($ -> hasAddress()));

        Flow sigils = Flow.row();
        panel.child(
            sigils.size(156, 16)
                .marginTop(20)
                .marginLeft(14)
                .childPadding(6)
                .setEnabledIf($ -> hasAddress()));

        for (int i = 0; i < dialingAddress.length; i++) {
            int fi = i;

            // Use the dialing address if it has been set by the player, otherwise
            // use the controller's current dialed chevron.
            IntValue.Dynamic value = new IntValue.Dynamic(() -> {
                int sigilId = dialingAddress[fi];
                if (sigilId == -1) {
                    sigilId = registeredAddress[fi];
                }
                return sigilId;
            }, val -> {
                IntSyncValue syncer = syncManager.findSyncHandler("chevron" + fi, IntSyncValue.class);
                syncer.setIntValue(val);
            });

            sigils.child(
                new CycleButtonWidget().value(value)
                    .size(16, 16)
                    .background(UITextures.SIGIL_BG)
                    .hoverBackground(UITextures.SIGIL_BG_ACTIVE)
                    .length(16)
                    .overlay(new DynamicDrawable(() -> {
                        int sigilId = dialingAddress[fi];
                        if (sigilId == -1) {
                            sigilId = registeredAddress[fi];
                        }
                        return UITextures.getSigil(sigilId);
                    })));
        }

        panel.child(
            new ButtonWidget<>().marginTop(48)
                .marginLeft(148)
                .size(18, 18)
                .tooltip(t -> t.add(IKey.lang("stargatenh.tooltip.dialing_device.dial_address")))
                .overlay(UITextures.OVERLAY_CHECK)
                .syncHandler(
                    new InteractionSyncHandler().setOnMousePressed(mouseData -> controller.dialOut(dialingAddress)))
                .setEnabledIf($ -> hasAddress()));
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(MODID, mainPanel);
    }

    private boolean hasAddress() {
        return controller != null && controller.hasAddress;
    }
}
