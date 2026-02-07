package com.coinlockedplugin.data;


public enum PointRewards {
    LevelUp(2, "for leveling up"),
    CollectionLogEntry(2, "for getting a new collection log entry"),
    CombatAchievement(2, "for getting a new combat achievement"),
    Pet(10, "for getting a pet"),
    MusicTrack(1, "for unlocking a music track"),
    QuestComplete(5, "for completing a quest"),
    DiaryStep(2, "for completing a diary step");

    private final int points;
    private final String message;

    PointRewards(int points, String message) {
        this.points = points;
        this.message = message;
    }

    public int getPoints() {
        return points;
    }

    public String getMessage() {
        return message;
    }
}
