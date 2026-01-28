package com.coinboundplugin.requirements;

import com.coinboundplugin.CoinboundPlugin;
import com.coinboundplugin.unlocks.UnlockRegistry;

import java.util.Set;

public class UnlockIDRequirement implements AppearRequirement
{
    private final String requiredUnlockId;
    private final UnlockRegistry unlockRegistry;

    public UnlockIDRequirement(String requiredUnlockId, UnlockRegistry unlockRegistry)
    {
        this.requiredUnlockId = requiredUnlockId;
        this.unlockRegistry = unlockRegistry;
    }

    @Override
    public boolean isMet(CoinboundPlugin coinboundPlugin, Set<String> unlockedIds)
    {
        return unlockedIds.contains(requiredUnlockId);
    }

    @Override
    public String getRequiredUnlockTitle()
    {
        String displayName = requiredUnlockId;
        if (unlockRegistry != null) {
            var unlock = unlockRegistry.getAll().stream()
                    .filter(u -> u.getId().equals(requiredUnlockId))
                    .findFirst();
            if (unlock.isPresent()) {
                displayName = unlock.get().getDisplayName();
            }
        }
        return "Requires " + displayName;
    }
}
