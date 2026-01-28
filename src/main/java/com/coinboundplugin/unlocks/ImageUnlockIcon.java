package com.coinboundplugin.unlocks;

import javax.swing.*;

public final class ImageUnlockIcon implements UnlockIcon
{
    private final Icon icon;

    public ImageUnlockIcon(Icon icon)
    {
        this.icon = icon;
    }

    public Icon getIcon()
    {
        return icon;
    }
}
