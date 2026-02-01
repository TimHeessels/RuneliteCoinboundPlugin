package com.coinboundplugin.requirements;

import com.coinboundplugin.CoinboundPlugin;
import net.runelite.api.Skill;

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
        return plugin.isSkillBracketUnlocked(Skill.ATTACK) || plugin.isSkillBracketUnlocked(Skill.STRENGTH) || plugin.isSkillBracketUnlocked(Skill.DEFENCE) || plugin.isSkillBracketUnlocked(Skill.RANGED) || plugin.isSkillBracketUnlocked(Skill.MAGIC);
    }

    @Override
    public String getRequiredUnlockTitle() {
        return "Combat level " + requiredLevel + " or higher.";
    }
}

