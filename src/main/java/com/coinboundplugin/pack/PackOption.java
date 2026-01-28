package com.coinboundplugin.pack;

import com.coinboundplugin.CoinboundPlugin;

public interface PackOption {
    String getDisplayName();

    String getDisplayType();

    void onChosen(CoinboundPlugin plugin);
}