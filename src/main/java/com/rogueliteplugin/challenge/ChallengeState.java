package com.rogueliteplugin.challenge;

public class ChallengeState {
    private final Challenge challenge;
    private long progress;
    private final long goal;

    public ChallengeState(Challenge challenge, long goal) {
        this.challenge = challenge;
        this.goal = goal;
        this.progress = 0;
    }

    public void increment(long amount) {
        progress = Math.min(goal, progress + amount);
    }

    public void CompleteGoal() {
        progress = goal;
    }

    public boolean isComplete() {
        return progress >= goal;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public long getProgress() {
        return progress;
    }

    public long getGoal() {
        return goal;
    }
}

