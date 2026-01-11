package com.rogueliteplugin.unlocks;

import javax.swing.Icon;

public interface Unlock
{
    UnlockType getType();
    String getId();
    String getDisplayName();
    String getDescription();
    UnlockIcon getIcon();
}
