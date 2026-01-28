package com.coinboundplugin.requirements;

import com.coinboundplugin.CoinboundPlugin;

import java.util.Set;

public class MemberRequirement implements AppearRequirement {

    public MemberRequirement() {

    }

    @Override
    public boolean isMet(CoinboundPlugin plugin, Set<String> unlockedIds) {
        return plugin.isInMemberWorld();
    }

    @Override
    public String getRequiredUnlockTitle() {
        return "Membership Required";
    }
}

