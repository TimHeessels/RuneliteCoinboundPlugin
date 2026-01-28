package com.coinboundplugin.unlocks;

import com.coinboundplugin.data.UnlockType;
import com.coinboundplugin.requirements.AppearRequirement;

import java.util.List;

public interface Unlock
{
    UnlockType getType();
    String getId();
    String getDisplayName();
    String getDescription();
    UnlockIcon getIcon();

    default List<AppearRequirement> getRequirements()
    {
        return List.of(); // no requirements by default
    }
}
