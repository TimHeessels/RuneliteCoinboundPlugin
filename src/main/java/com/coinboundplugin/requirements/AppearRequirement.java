package com.coinboundplugin.requirements;

import com.coinboundplugin.CoinboundPlugin;
import java.util.Set;

public interface AppearRequirement
{
    // Convenience bridge for UI / tooltips
    default boolean isMet(CoinboundPlugin plugin, Set<String> unlockedIds)
    {
        return true;
    }

    String getRequiredUnlockTitle();
}