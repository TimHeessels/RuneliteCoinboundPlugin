package com.coinboundplugin.overlays;

import com.google.inject.Inject;
import com.coinboundplugin.CoinboundPlugin;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import java.awt.*;

public class FogMapOverlay extends Overlay {

    private final CoinboundPlugin plugin;
    private final Client client;
    private static final Color BORDER_COLOR = Color.RED;
    private static final Color FOG_COLOR = new Color(0, 0, 0, 120);

    @Inject
    public FogMapOverlay(Client client, CoinboundPlugin plugin) {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.LOW);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.gamemodeSetupState != CoinboundPlugin.SetupStage.SetupComplete)
            return null;
        drawMapFog(graphics);
        return null;
    }

    private void drawMapFog(Graphics2D graphics) {
        Widget map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);
        if (map == null) {
            return;
        }

        WorldMap worldMap = client.getWorldMap();
        float pixelsPerTile = worldMap.getWorldMapZoom();
        Rectangle mapRect = map.getBounds();
        graphics.setClip(mapRect);

        Point center = worldMap.getWorldMapPosition();

        int tilesWide = (int) Math.ceil(mapRect.width / pixelsPerTile);
        int tilesHigh = (int) Math.ceil(mapRect.height / pixelsPerTile);

        int minX = center.getX() - tilesWide / 2;
        int maxX = center.getX() + tilesWide / 2;
        int minY = center.getY() - tilesHigh / 2;
        int maxY = center.getY() + tilesHigh / 2;

        WorldPoint origin = plugin.WorldOrgin;
        int r = plugin.wanderRadius;

        // --- Draw fog ---
        graphics.setColor(FOG_COLOR);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                boolean inside =
                        Math.abs(x - origin.getX()) <= r &&
                                Math.abs(y - origin.getY()) <= r;

                if (inside) {
                    continue;
                }

                int px = (int) ((x - minX) * pixelsPerTile) + mapRect.x;
                int py = mapRect.y + mapRect.height
                        - (int) ((y - minY + 1) * pixelsPerTile);

                graphics.fillRect(
                        px,
                        py,
                        (int) pixelsPerTile + 1,
                        (int) pixelsPerTile + 1
                );
            }
        }

        // --- Draw square boundary ---
        graphics.setColor(BORDER_COLOR);

        drawMapLine(graphics,
                origin.dx(-r).dy(-r),
                origin.dx(r).dy(-r),
                minX, minY, pixelsPerTile, mapRect);

        drawMapLine(graphics,
                origin.dx(r).dy(-r),
                origin.dx(r).dy(r),
                minX, minY, pixelsPerTile, mapRect);

        drawMapLine(graphics,
                origin.dx(r).dy(r),
                origin.dx(-r).dy(r),
                minX, minY, pixelsPerTile, mapRect);

        drawMapLine(graphics,
                origin.dx(-r).dy(r),
                origin.dx(-r).dy(-r),
                minX, minY, pixelsPerTile, mapRect);
    }

    private void drawMapLine(
            Graphics2D g,
            WorldPoint a,
            WorldPoint b,
            int minX,
            int minY,
            float ppt,
            Rectangle rect) {
        int ax = rect.x + (int) ((a.getX() - minX) * ppt);
        int ay = rect.y + rect.height - (int) ((a.getY() - minY) * ppt);

        int bx = rect.x + (int) ((b.getX() - minX) * ppt);
        int by = rect.y + rect.height - (int) ((b.getY() - minY) * ppt);

        g.drawLine(ax, ay, bx, by);
    }
}
