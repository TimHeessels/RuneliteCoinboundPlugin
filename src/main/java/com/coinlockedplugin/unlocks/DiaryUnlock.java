package com.coinlockedplugin.unlocks;

import com.coinlockedplugin.data.UnlockType;
import com.coinlockedplugin.requirements.AppearRequirement;

import javax.swing.*;
import java.util.List;

public class DiaryUnlock implements Unlock {
    private final String id;
    private final String name;
    private final Icon icon;
    private final String description;
    private final List<AppearRequirement> requirements;

    public DiaryUnlock(String id, String name, Icon icon, String description,
                       List<AppearRequirement> requirements) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.requirements = requirements;
    }

    @Override
    public UnlockType getType() {
        return UnlockType.AchievementDiaries;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public UnlockIcon getIcon() {
        return new ImageUnlockIcon(icon);
    }

    @Override
    public List<AppearRequirement> getRequirements() {
        return requirements;
    }
}
