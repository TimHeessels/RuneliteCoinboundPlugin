package com.rogueliteplugin;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;

import java.awt.*;
import java.util.Objects;

public class RogueliteInfoboxOverlay extends Overlay {
    private final RoguelitePlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    public RogueliteInfoboxOverlay(RoguelitePlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(250, 0));

        //Display welcome message on first launch
        if ((long) plugin.getUnlockedIds().size() < 2) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Welcome to the roguelite game mode. You can start the game by reading the rules and then opening a booster pack in the side panel.")
                    .build());
        }
        long peakCoins = plugin.getPeakCoins();
        int packsBought = plugin.getPackBought();

        long previous = plugin.peakCoinsRequiredForPack(packsBought);
        long next = plugin.peakCoinsRequiredForPack(packsBought + 1);

        long barGoal = next - previous;
        long barProgress = Math.max(0, peakCoins - previous);

        barProgress = Math.min(barProgress, barGoal);
        if (plugin.getAvailablePacksToBuy() > 0)
        {
            barProgress = barGoal;
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Next pack at")
                .right("Available packs")
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(next + " gp")
                .right(plugin.getAvailablePacksToBuy() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Progress towards next pack")
                .build());
        ProgressBarComponent bar = new ProgressBarComponent();
        bar.setMinimum(0);
        bar.setMaximum(barGoal);
        bar.setValue(barProgress);
        bar.setPreferredSize(new Dimension(220, 12));
        if (barProgress >= barGoal)
            bar.setForegroundColor(new Color(0, 170, 0));
        else
            bar.setForegroundColor(new Color(255, 152, 31));

        panelComponent.getChildren().add(bar);

        return panelComponent.render(graphics);
    }
}
