package com.coinboundplugin.save;

import com.coinboundplugin.data.PackChoiceState;
import com.coinboundplugin.data.SetupStage;
import com.coinboundplugin.pack.PackOption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveData
{
    public int version = 1;

    // pack UI / state
    public List<PackOption> currentPackOptions = new ArrayList<>();
    public PackChoiceState packChoiceState = PackChoiceState.NONE;
    public SetupStage setupStage = SetupStage.DropAllItems;

    // progression
    public long illegalXPGained = 0L;
    public int packsBought = 0;
    public long peakWealth = 0L;

    public String lastUnlockedName = "";
    public Set<String> unlockedIds = new HashSet<>();

    public long lastUpdatedEpochMs = 0L;

    public SaveData()
    {
        //Initial unlocks
        unlockedIds.add("SKILL_HITPOINTS"); //Unlock hit points by default as it would suck to unlock a combat skill and still not be able to use it
    }
}
