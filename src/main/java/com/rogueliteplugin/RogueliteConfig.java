package com.rogueliteplugin;

import net.runelite.api.Skill;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.EnumSet;
import java.util.Set;

@ConfigGroup("rogueliteplugin")
public interface RogueliteConfig extends Config
{
	String GROUP = "rogueliteplugin";

	@ConfigItem(
			keyName = "totalXpGained",
			name = "Total XP Gained",
			description = "Total XP counted by the plugin",
			hidden = true
	)
	default long totalXpGained()
	{
		return 0L;
	}

	@ConfigItem(
			keyName = "totalXpGained",
			name = "Total XP Gained",
			description = "Total XP counted by the plugin",
			hidden = true
	)
	void totalXpGained(long value);

	@ConfigItem(
			keyName = "illegalXPGained",
			name = "XP gained in blocked skills",
			description = "Total XP you gained in skills you did not have unlocked",
			hidden = true
	)
	default long illegalXPGained()
	{
		return 0L;
	}

	@ConfigItem(
			keyName = "illegalXPGained",
			name = "XP gained in blocked skills",
			description = "Total XP you gained in skills you did not have unlocked",
			hidden = true
	)
	void illegalXPGained(long value);

	@ConfigItem(
			keyName = "totalPoints",
			name = "Total Points",
			description = "Total points earned"
	)
	default int totalPoints()
	{
		return 55;
	}

	@ConfigItem(
			keyName = "totalPoints",
			name = "Total Points",
			description = "Total points earned",
			hidden = true
	)
	void totalPoints(int value);

	@ConfigItem(
			keyName = "pointsSpent",
			name = "Points Spent",
			description = "Points spent by the user"
	)
	default int pointsSpent()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "pointsSpent",
			name = "Points Spent",
			description = "Points spent by the user",
			hidden = true
	)
	void pointsSpent(int value);

	@ConfigItem(
			keyName = "xpToNextPoint",
			name = "XP to next point",
			description = "How much XP users need to get to gain a point",
			hidden = false
	)
	default int xpToNextPoint()
	{
		return 150;
	}

	@ConfigItem(
			keyName = "xpToNextPoint",
			name = "XP to next point",
			description = "How much XP users need to get to gain a point",
			hidden = false
	)
	void xpToNextPoint(int value);

	/*
	@ConfigItem(
			keyName = "allowedSkills",
			name = "Allowed skills",
			description = "Unlocked skills"
	)
	default Set<Skill> allowedSkills()
	{
		return EnumSet.noneOf(Skill.class);
	}
	 */

	@ConfigItem(
			keyName = "unlockedIds",
			name = "Unlocked Elements",
			description = "Internal unlock tracking"
	)
	default String unlockedIds()
	{
		return "";
	}
}
