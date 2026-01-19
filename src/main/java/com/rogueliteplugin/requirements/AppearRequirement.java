package com.rogueliteplugin.requirements;

import com.rogueliteplugin.RoguelitePlugin;
import java.util.Set;

public interface AppearRequirement
{
    // Convenience bridge for UI / tooltips
    default boolean isMet(RoguelitePlugin plugin, Set<String> unlockedIds)
    {
        return true;
    }

    String getRequiredUnlockTitle();
}