package com.rogueliteplugin.unlocks;

import java.util.*;

public class UnlockRegistry
{
    private final Map<String, Unlock> unlocks = new HashMap<>();

    public void register(Unlock unlock)
    {
        unlocks.put(unlock.getId(), unlock);
    }

    public Unlock get(String id)
    {
        return unlocks.get(id);
    }

    public Collection<Unlock> getAll()
    {
        return unlocks.values();
    }
}
