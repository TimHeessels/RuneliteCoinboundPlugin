package com.coinboundplugin.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.coinboundplugin.CoinboundPlugin;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.api.coords.LocalPoint;

import java.awt.*;

@Singleton
public class FogOverlay extends Overlay {
    private final CoinboundPlugin plugin;
    private final Client client;
    private static final Color FOG_COLOR = new Color(0, 0, 0, 120);

    @Inject
    public FogOverlay(Client client, CoinboundPlugin plugin) {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(Overlay.PRIORITY_LOW);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.gamemodeSetupState != CoinboundPlugin.SetupStage.SetupComplete)
            return null;
        drawSceneFog(graphics);
        return null;
    }

    private void drawSceneFog(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return;
        }

        int plane = player.getWorldLocation().getPlane();

        // Only apply fog on overworld surface (no instances, no upstairs/downstairs)
        if (!plugin.isOverworldSurface(player))
        {
            return;
        }

        WorldView worldView = client.getTopLevelWorldView();
        if (worldView == null)
        {
            return;
        }

        Scene scene = worldView.getScene();
        Tile[][][] tiles = scene.getTiles();

        if (tiles == null || tiles[plane] == null)
        {
            return;
        }

        graphics.setColor(FOG_COLOR);

        for (int x = 0; x < 104; x++)
        {
            for (int y = 0; y < 104; y++)
            {
                Tile tile = tiles[plane][x][y];
                if (tile == null)
                {
                    continue;
                }

                LocalPoint lp = tile.getLocalLocation();
                if (lp == null)
                {
                    continue;
                }

                WorldPoint wp = tile.getWorldLocation();

                boolean inside =
                        Math.abs(wp.getX() - plugin.WorldOrgin.getX()) <= plugin.wanderRadius &&
                                Math.abs(wp.getY() - plugin.WorldOrgin.getY()) <= plugin.wanderRadius;

                if (inside)
                {
                    continue;
                }

                Polygon poly = Perspective.getCanvasTilePoly(client, lp);
                if (poly == null)
                {
                    continue;
                }

                graphics.fillPolygon(poly);
            }
        }
    }
}
