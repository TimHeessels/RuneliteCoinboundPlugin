package com.coinboundplugin.requirements;

import com.coinboundplugin.CoinboundPlugin;

import java.util.Set;

public class CombatRequirement implements AppearRequirement {
    private final int requiredLevel;
    private final int maxCombatLevel;

    public CombatRequirement(int requiredLevel, int maxCombatLevel) {
        this.requiredLevel = requiredLevel;
        this.maxCombatLevel = maxCombatLevel;
    }

    @Override
    public boolean isMet(CoinboundPlugin plugin, Set<String> unlockedIds) {
        if (plugin.getClient().getLocalPlayer().getCombatLevel() < requiredLevel)
            return false;
        if (plugin.getClient().getLocalPlayer().getCombatLevel() > maxCombatLevel)
            return false;
        return plugin.isUnlocked("SKILL_ATTACK") || plugin.isUnlocked("SKILL_STRENGTH") || plugin.isUnlocked("SKILL_DEFENCE") || plugin.isUnlocked("SKILL_RANGED") || plugin.isUnlocked("SKILL_MAGIC");
    }


    @Override
    public String getRequiredUnlockTitle() {
        return "Combat level " + requiredLevel + " or higher.";
    }
}

