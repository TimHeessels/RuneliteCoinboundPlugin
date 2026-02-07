package com.coinlockedplugin.overlays;

import com.coinlockedplugin.CoinlockedPlugin;
import com.coinlockedplugin.data.SetupStage;
import com.google.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;

import java.awt.*;

public class CoinboundInfoboxOverlay extends Overlay {
    private final CoinlockedPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public CoinboundInfoboxOverlay(CoinlockedPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.getConfig().showOverlay()) {
            return null;
        }

        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(220, 0));

        //Display welcome message on first launch
        if (plugin.getSetupStage() == SetupStage.DropAllItems) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Welcome to the Coinbound game mode. Please drop all items you got from tutorial island.")
                    .build());
            return panelComponent.render(graphics);
        }
        //Go fill up inventory with flyers
        if (plugin.getSetupStage() == SetupStage.GetFlyers || plugin.fillerItemsShort > 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Please go to the Al Kharid flyerer and use the drop-trick to get " + plugin.fillerItemsShort + " more flyers to fill up your inventory.")
                    .build());
            return panelComponent.render(graphics);
        }
        if (plugin.fillerItemsShort < 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("You can drop " + Math.abs(plugin.fillerItemsShort) + " flyers as you have too many.")
                    .build());
        }

        int availablePacks = plugin.getNewPackAvailableCount();
        if (availablePacks > 1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("You have " + availablePacks + " packs available to buy! Press the button at the top of the screen to open them.")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder().build());
        }
        if (availablePacks == 1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("You have a pack available to buy! Press the button at the top of the screen to buy it.")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder().build());
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current points")
                .right("Pack cost")
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(plugin.getCurrentPoints() + "")
                .right(plugin.packCost + "")
                .build());
        panelComponent.getChildren().add(LineComponent.builder().build());

        int pointsTowardsNextPack = plugin.getCurrentPoints() % plugin.packCost;

        // Progress bar
        ProgressBarComponent bar = new ProgressBarComponent();
        bar.setMinimum(0);
        bar.setMaximum(plugin.packCost);
        bar.setValue(pointsTowardsNextPack);
        bar.setForegroundColor(new Color(80, 200, 120));
        bar.setBackgroundColor(new Color(40, 40, 40));

        panelComponent.getChildren().add(bar);


        return panelComponent.render(graphics);
    }
}