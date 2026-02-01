package com.coinboundplugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("coinboundplugin")
public interface CoinboundConfig extends Config {
    String GROUP = "coinboundplugin";

    //Option to toggle overlay visibility
    @ConfigItem(
            keyName = "showOverlay",
            name = "Show Coinbound Overlay",
            description = "Toggles the visibility of the Coinbound Overlay"
    )
    default boolean showOverlay() {
        return true;
    }
}
