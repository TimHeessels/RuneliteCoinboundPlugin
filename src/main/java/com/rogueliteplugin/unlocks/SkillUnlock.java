package com.rogueliteplugin.unlocks;

import net.runelite.api.Skill;
import javax.swing.Icon;

public class SkillUnlock implements Unlock
{
    private final Skill skill;

    public SkillUnlock(Skill skill)
    {
        this.skill = skill;
    }

    @Override
    public UnlockType getType()
    {
        return UnlockType.SKILL;
    }

    @Override
    public String getId()
    {
        return "SKILL_" + skill.name();
    }

    @Override
    public String getDisplayName()
    {
        return skill.getName();
    }

    @Override
    public String getDescription()
    {
        return "Allows training " + skill.getName() + ".";
    }

    @Override
    public UnlockIcon getIcon()
    {
        return null; // skills are represented elsewhere
    }
}
