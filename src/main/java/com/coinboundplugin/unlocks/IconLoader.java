package com.coinboundplugin.unlocks;

import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public final class IconLoader
{
    private IconLoader() {}

    public static ImageIcon load(String name)
    {
        BufferedImage img = ImageUtil.loadImageResource(
                IconLoader.class,
                "/icons/" + name
        );
        return img == null ? null : new ImageIcon(img);
    }
}
