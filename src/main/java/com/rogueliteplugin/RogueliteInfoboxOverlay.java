package com.rogueliteplugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import java.awt.Dimension;
import java.awt.Graphics2D;

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

        panelComponent.getChildren().add(LineComponent.builder()
                .left("XP to Next Point:")
                .right(Integer.toString(plugin.getXpToNextPoint()))
                .build());

        if (plugin.getCurrentPoints() == 1)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("You can buy a new pack!")
                    .build());
        }
        if (plugin.getCurrentPoints() > 1)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("You can buy " + plugin.getCurrentPoints() + " new packs!")
                    .build());
        }

        return panelComponent.render(graphics);
    }
    }
