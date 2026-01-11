package com.rogueliteplugin.unlocks;

import javax.swing.Icon;

public class AreaUnlock implements Unlock
{
    private final String id;
    private final String name;
    private final Icon icon;
    private final String description;

    public AreaUnlock(String id, String name, Icon icon, String description)
    {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    @Override
    public UnlockType getType()
    {
        return UnlockType.AREA;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public UnlockIcon getIcon()
    {
        return new ImageUnlockIcon(icon);
    }
}
