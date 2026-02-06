package com.coinlockedplugin.data;

import com.coinlockedplugin.requirements.AppearRequirement;
import com.coinlockedplugin.requirements.MaxSkillLevelUnlocked;
import com.coinlockedplugin.requirements.UnlockIDRequirement;
import com.coinlockedplugin.unlocks.UnlockRegistry;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CoinlockedAchievementRequirement {

    private static final Map<String, Function<UnlockRegistry, List<AppearRequirement>>> ACHIEVEMENT_REQUIREMENTS = new HashMap<>();

    public enum DiaryTier {
        EASY("Easy"),
        MEDIUM("Medium"),
        HARD("Hard"),
        ELITE("Elite");

        private final String displayName;

        DiaryTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AchievementDiaries {
        ARDOOUGNE("Ardougne"),
        DESERT("Desert"),
        FALADOR("Falador"),
        FREMENNIK_PROVINCE("Fremennik Province"),
        KANDARIN("Kandarin"),
        KARAMJA("Karamja"),
        KOUREND_KEBOS("Kourend & Kebos"),
        LUMBRIDGE_DRAYNOR("Lumbridge & Draynor"),
        MORYTANIA("Morytania"),
        VARROCK("Varrock"),
        WESTERN_PROVINCES("Western Provinces"),
        WILDERNESS("Wilderness");

        private final String displayName;

        AchievementDiaries(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    static {
        //Ardougne
        register(AchievementDiaries.ARDOOUGNE, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.BIOHAZARD, r),
                new UnlockIDRequirement("Quests" + Quest.RUNE_MYSTERIES, r)
        ));
        register(AchievementDiaries.ARDOOUGNE, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ENLIGHTENED_JOURNEY, r),
                new UnlockIDRequirement("Quests" + Quest.THE_HAND_IN_THE_SAND, r),
                new UnlockIDRequirement("Quests" + Quest.RUNE_MYSTERIES, r),
                new UnlockIDRequirement("Quests" + Quest.TOWER_OF_LIFE, r),
                new UnlockIDRequirement("Quests" + Quest.UNDERGROUND_PASS, r),
                new UnlockIDRequirement("Quests" + Quest.FAIRYTALE_II__CURE_A_QUEEN, r),
                new UnlockIDRequirement("Quests" + Quest.SEA_SLUG, r),
                new UnlockIDRequirement("Quests" + Quest.WATCHTOWER, r)
        ));
        register(AchievementDiaries.ARDOOUGNE, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.LEGENDS_QUEST, r),
                new UnlockIDRequirement("Quests" + Quest.MONKEY_MADNESS_I, r),
                new UnlockIDRequirement("Quests" + Quest.MOURNINGS_END_PART_II, r),
                new UnlockIDRequirement("Quests" + Quest.TOWER_OF_LIFE, r)
        ));
        register(AchievementDiaries.ARDOOUGNE, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DESERT_TREASURE_I, r),
                new UnlockIDRequirement("Quests" + Quest.HAUNTED_MINE, r)
        ));

        //Desert
        register(AchievementDiaries.DESERT, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ICTHLARINS_LITTLE_HELPER, r)
        ));
        register(AchievementDiaries.DESERT, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.EAGLES_PEAK, r),
                new UnlockIDRequirement("Quests" + Quest.ENAKHRAS_LAMENT, r),
                new UnlockIDRequirement("Quests" + Quest.THE_GOLEM, r),
                new UnlockIDRequirement("Quests" + Quest.SPIRITS_OF_THE_ELID, r)
        ));
        register(AchievementDiaries.DESERT, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.CONTACT, r),
                new UnlockIDRequirement("Quests" + Quest.DESERT_TREASURE_I, r),
                new UnlockIDRequirement("Quests" + Quest.DREAM_MENTOR, r),
                new UnlockIDRequirement("Quests" + Quest.THE_FEUD, r),
                new UnlockIDRequirement("KALPHITE_QUEEN", r)
        ));
        register(AchievementDiaries.DESERT, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("PyramidPlunder", r)
        ));

        // Falador
        register(AchievementDiaries.FALADOR, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DORICS_QUEST, r),
                new UnlockIDRequirement("Quests" + Quest.THE_KNIGHTS_SWORD, r)

        ));
        register(AchievementDiaries.FALADOR, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.RECRUITMENT_DRIVE, r)
        ));
        register(AchievementDiaries.FALADOR, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.GRIM_TALES, r),
                new UnlockIDRequirement("Quests" + Quest.HEROES_QUEST, r),
                new UnlockIDRequirement("Quests" + Quest.THE_SLUG_MENACE, r),
                new UnlockIDRequirement("Quests" + Quest.RATCATCHERS, r),
                new UnlockIDRequirement("GIANT_MOLE", r),
                new UnlockIDRequirement("WarriorsGuild", r)

        ));
        register(AchievementDiaries.FALADOR, DiaryTier.ELITE, r -> List.of(

        ));

        // Fremennik Province
        register(AchievementDiaries.FREMENNIK_PROVINCE, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.THE_FREMENNIK_TRIALS, r),
                new UnlockIDRequirement("Quests" + Quest.THE_GIANT_DWARF, r),
                new UnlockIDRequirement("Quests" + Quest.TROLL_STRONGHOLD, r)
        ));
        register(AchievementDiaries.FREMENNIK_PROVINCE, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.BETWEEN_A_ROCK, r),
                new UnlockIDRequirement("Quests" + Quest.EAGLES_PEAK, r),
                new UnlockIDRequirement("Quests" + Quest.HORROR_FROM_THE_DEEP, r),
                new UnlockIDRequirement("Quests" + Quest.FAIRYTALE_II__CURE_A_QUEEN, r),
                new UnlockIDRequirement("Quests" + Quest.THE_GIANT_DWARF, r),
                new UnlockIDRequirement("Quests" + Quest.TROLL_STRONGHOLD, r),
                new UnlockIDRequirement("Quests" + Quest.OLAFS_QUEST, r)
        ));
        register(AchievementDiaries.FREMENNIK_PROVINCE, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.EADGARS_RUSE, r),
                new UnlockIDRequirement("Quests" + Quest.LUNAR_DIPLOMACY, r),
                new UnlockIDRequirement("Quests" + Quest.THRONE_OF_MISCELLANIA, r)
        ));
        register(AchievementDiaries.FREMENNIK_PROVINCE, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("DK_SUPREME", r),
                new UnlockIDRequirement("DK_REX", r),
                new UnlockIDRequirement("DK_PRIME", r)
        ));

        // Kandarin
        register(AchievementDiaries.KANDARIN, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ELEMENTAL_WORKSHOP_I, r)
        ));
        register(AchievementDiaries.KANDARIN, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.WATERFALL_QUEST, r),
                new UnlockIDRequirement("Quests" + Quest.FAIRYTALE_II__CURE_A_QUEEN, r),
                new UnlockIDRequirement("Quests" + Quest.ELEMENTAL_WORKSHOP_II, r)
        ));
        register(AchievementDiaries.KANDARIN, DiaryTier.HARD, r -> List.of(

                new UnlockIDRequirement("Quests" + Quest.DESERT_TREASURE_I, r),
                new UnlockIDRequirement("Quests" + Quest.ELEMENTAL_WORKSHOP_II, r),
                new UnlockIDRequirement("Barbarian_Assault", r)
        ));
        register(AchievementDiaries.KANDARIN, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.LUNAR_DIPLOMACY, r)
        ));

        // Karamja
        register(AchievementDiaries.KARAMJA, DiaryTier.EASY, r -> List.of(

        ));
        register(AchievementDiaries.KARAMJA, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.THE_GRAND_TREE, r),
                new UnlockIDRequirement("Quests" + Quest.SHILO_VILLAGE, r),
                new UnlockIDRequirement("Quests" + Quest.DRAGON_SLAYER_I, r),
                new UnlockIDRequirement("Quests" + Quest.TAI_BWO_WANNAI_TRIO, r)
        ));
        register(AchievementDiaries.KARAMJA, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.LEGENDS_QUEST, r),
                new UnlockIDRequirement("TzHaarFightCave", r)
        ));
        register(AchievementDiaries.KARAMJA, DiaryTier.ELITE, r -> List.of(

        ));

        // Kourend & Kebos
        register(AchievementDiaries.KOUREND_KEBOS, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DRUIDIC_RITUAL, r)
        ));
        register(AchievementDiaries.KOUREND_KEBOS, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.THE_ASCENT_OF_ARCEUUS, r),
                new UnlockIDRequirement("Quests" + Quest.THE_DEPTHS_OF_DESPAIR, r),
                new UnlockIDRequirement("Quests" + Quest.THE_FORSAKEN_TOWER, r),
                new UnlockIDRequirement("Quests" + Quest.THE_QUEEN_OF_THIEVES, r),
                new UnlockIDRequirement("Quests" + Quest.TALE_OF_THE_RIGHTEOUS, r),
                new UnlockIDRequirement("Quests" + Quest.FAIRYTALE_II__CURE_A_QUEEN, r),
                new UnlockIDRequirement("Quests" + Quest.EAGLES_PEAK, r)
        ));
        register(AchievementDiaries.KOUREND_KEBOS, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DREAM_MENTOR, r)
        ));
        register(AchievementDiaries.KOUREND_KEBOS, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("SKOTIZO", r)
                //TODO: Have raids unlocked
        ));

        // Lumbridge & Draynor
        register(AchievementDiaries.LUMBRIDGE_DRAYNOR, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.COOKS_ASSISTANT, r),
                new UnlockIDRequirement("Quests" + Quest.RUNE_MYSTERIES, r)
        ));
        register(AchievementDiaries.LUMBRIDGE_DRAYNOR, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ANIMAL_MAGNETISM, r),
                new UnlockIDRequirement("Quests" + Quest.FAIRYTALE_II__CURE_A_QUEEN, r)
        ));
        register(AchievementDiaries.LUMBRIDGE_DRAYNOR, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ANOTHER_SLICE_OF_HAM, r),
                new UnlockIDRequirement("Quests" + Quest.RECIPE_FOR_DISASTER, r),
                new UnlockIDRequirement("Quests" + Quest.TEARS_OF_GUTHIX, r),
                new UnlockIDRequirement("MageTrainingArena", r)
        ));
        register(AchievementDiaries.LUMBRIDGE_DRAYNOR, DiaryTier.ELITE, r -> List.of(
                //All quests?
        ));

        // Morytania
        register(AchievementDiaries.MORYTANIA, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.NATURE_SPIRIT, r)
        ));
        register(AchievementDiaries.MORYTANIA, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.CABIN_FEVER, r),
                new UnlockIDRequirement("Quests" + Quest.DWARF_CANNON, r),
                new UnlockIDRequirement("Quests" + Quest.GHOSTS_AHOY, r),
                new UnlockIDRequirement("Quests" + Quest.IN_AID_OF_THE_MYREQUE, r)
        ));
        register(AchievementDiaries.MORYTANIA, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DESERT_TREASURE_I, r),
                new UnlockIDRequirement("Quests" + Quest.DWARF_CANNON, r),
                new UnlockIDRequirement("Quests" + Quest.GHOSTS_AHOY, r),
                new UnlockIDRequirement("Quests" + Quest.THE_GREAT_BRAIN_ROBBERY, r)
        ));
        register(AchievementDiaries.MORYTANIA, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.LUNAR_DIPLOMACY, r),
                new UnlockIDRequirement("Quests" + Quest.SHADES_OF_MORTTON, r),
                new UnlockIDRequirement("BARROWS", r)
        ));

        // Varrock
        register(AchievementDiaries.VARROCK, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.RUNE_MYSTERIES, r)
        ));
        register(AchievementDiaries.VARROCK, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.THE_DIG_SITE, r),
                new UnlockIDRequirement("Quests" + Quest.ENLIGHTENED_JOURNEY, r),
                new UnlockIDRequirement("Quests" + Quest.GARDEN_OF_TRANQUILLITY, r),
                new UnlockIDRequirement("Quests" + Quest.GERTRUDES_CAT, r),
                new UnlockIDRequirement("Quests" + Quest.A_SOULS_BANE, r),
                new UnlockIDRequirement("Quests" + Quest.TREE_GNOME_VILLAGE, r)

        ));
        register(AchievementDiaries.VARROCK, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DESERT_TREASURE_I, r)
        ));
        register(AchievementDiaries.VARROCK, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DREAM_MENTOR, r),
                new UnlockIDRequirement("Quests" + Quest.GARDEN_OF_TRANQUILLITY, r)
        ));

        // Western Provinces
        register(AchievementDiaries.WESTERN_PROVINCES, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.BIG_CHOMPY_BIRD_HUNTING, r),
                new UnlockIDRequirement("Quests" + Quest.RUNE_MYSTERIES, r)
        ));
        register(AchievementDiaries.WESTERN_PROVINCES, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.EAGLES_PEAK, r),
                new UnlockIDRequirement("Quests" + Quest.THE_EYES_OF_GLOUPHRIE, r),
                new UnlockIDRequirement("Quests" + Quest.MONKEY_MADNESS_I, r),
                new UnlockIDRequirement("Quests" + Quest.ONE_SMALL_FAVOUR, r)
        ));
        register(AchievementDiaries.WESTERN_PROVINCES, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.RECIPE_FOR_DISASTER, r),
                new UnlockIDRequirement("Quests" + Quest.SWAN_SONG, r),
                new UnlockIDRequirement("Quests" + Quest.MOURNINGS_END_PART_I, r),
                new UnlockIDRequirement("ZULRAH", r)

        ));
        register(AchievementDiaries.WESTERN_PROVINCES, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("THERMY", r),
                new UnlockIDRequirement("PestControl", r)
        ));

        // Wilderness
        register(AchievementDiaries.WILDERNESS, DiaryTier.EASY, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.ENTER_THE_ABYSS, r)
        ));
        register(AchievementDiaries.WILDERNESS, DiaryTier.MEDIUM, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.BETWEEN_A_ROCK, r)
        ));
        register(AchievementDiaries.WILDERNESS, DiaryTier.HARD, r -> List.of(
                new UnlockIDRequirement("Quests" + Quest.DEATH_PLATEAU, r),
                new UnlockIDRequirement("Quests" + Quest.MAGE_ARENA_I, r),
                new UnlockIDRequirement("CHAOS_ELEMENTAL", r),
                new UnlockIDRequirement("CRAZY_ARCHAEOLOGIST", r),
                new UnlockIDRequirement("CHAOS_FANATIC", r),
                new UnlockIDRequirement("SCORPIA", r)
        ));
        register(AchievementDiaries.WILDERNESS, DiaryTier.ELITE, r -> List.of(
                new UnlockIDRequirement("ARTIO", r),
                new UnlockIDRequirement("CALVARION", r),
                new UnlockIDRequirement("SPINDEL", r)
        ));
    }

    private static void register(AchievementDiaries diary, DiaryTier tier, Function<UnlockRegistry, List<AppearRequirement>> requirements) {
        ACHIEVEMENT_REQUIREMENTS.put(diary.name() + "_" + tier.name(), requirements);
    }

    public static List<AppearRequirement> getRequirementsForDiary(AchievementDiaries diary, DiaryTier tier, UnlockRegistry registry) {
        Function<UnlockRegistry, List<AppearRequirement>> factory =
                ACHIEVEMENT_REQUIREMENTS.get(diary.name() + "_" + tier.name());

        if (factory == null) {
            return new ArrayList<>();
        }

        // Ensure callers can safely add/remove requirements (e.g. reqs.add(new MemberRequirement()))
        return new ArrayList<>(factory.apply(registry));
    }
}
