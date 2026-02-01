package com.coinboundplugin.unlocks;

import com.coinboundplugin.data.UnlockType;
import com.coinboundplugin.requirements.AppearRequirement;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class SkillRangeUnlock implements Unlock {
    private final Skill skill;
    private final ImageUnlockIcon icon;
    private final List<AppearRequirement> requirements;
    private final int start;
    private final int end;

    public SkillRangeUnlock(Skill skill, int start, int end, SkillIconManager skillIconManager, List<AppearRequirement> requirements) {
        this.skill = skill;
        this.requirements = requirements;
        this.start = start;
        this.end = end;

        BufferedImage img = skillIconManager.getSkillImage(skill);

        if (img != null) {
            this.icon = new ImageUnlockIcon(new ImageIcon(img));
        } else {
            this.icon = null;
        }
    }

    @Override
    public UnlockType getType() {
        return UnlockType.Skills;
    }

    @Override
    public String getId() {
        return "SKILL_" + skill.name() + "_" + end;
    }

    @Override
    public String getDisplayName() {
        return skill.getName() + " Levels " + start + "-" + end;
    }

    @Override
    public UnlockIcon getIcon() {
        return icon;
    }

    @Override
    public String getDescription() {
        return "Unlocks levels " + start + " to " + end + " of the " + skill.getName() + " skill.";
    }

    @Override
    public List<AppearRequirement> getRequirements() {
        return requirements;
    }
}
