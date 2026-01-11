package com.rogueliteplugin.unlocks;

public final class SpriteUnlockIcon implements UnlockIcon
{
    private final int spriteId;

    public SpriteUnlockIcon(int spriteId)
    {
        this.spriteId = spriteId;
    }

    public int getSpriteId()
    {
        return spriteId;
    }
}
