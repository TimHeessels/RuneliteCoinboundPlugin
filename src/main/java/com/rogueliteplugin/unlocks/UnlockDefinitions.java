package com.rogueliteplugin.unlocks;

import com.rogueliteplugin.RoguelitePlugin;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SkillIconManager;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

public final class UnlockDefinitions
{
    private UnlockDefinitions() {}

    public static void registerAll(
            UnlockRegistry registry,
            SkillIconManager skillIconManager,
            RoguelitePlugin plugin
    )
    {
        registerSkills(registry, skillIconManager);
        registerAreas(registry);
        registerShops(registry,plugin);
        // registerMinigames(registry);
    }

    private static void registerSkills(
            UnlockRegistry registry,
            SkillIconManager skillIconManager
    )
    {
        for (Skill skill : Skill.values())
        {
            BufferedImage img = skillIconManager.getSkillImage(skill);
            if (img == null)
            {
                continue;
            }

            registry.register(
                    new SkillUnlock(
                            skill
                    )
            );
        }
    }

    private static void registerAreas(UnlockRegistry registry)
    {
        registry.register(
                new AreaUnlock(
                        "VARROCK",
                        "Varrock",
                        IconLoader.load("icon.png"),
                        "Allows access to Varrock."
                )
        );

        registry.register(
                new AreaUnlock(
                        "FALADOR",
                        "Falador",
                        IconLoader.load("icon.png"),
                        "Allows access to Falador."
                )
        );
    }

    private static void registerShops(UnlockRegistry registry,RoguelitePlugin plugin)
    {
        // Basic shops/minimap icons registry
        registry.register(new ShopUnlock(
                "SHOP_GENERAL_STORE",
                "General Store",
                SpriteID.MAP_ICON_GENERAL_STORE,
                "Allows access to general stores."
        ));

        registry.register(new ShopUnlock(
                "SHOP_AXE_SHOP",
                "Axe Shop",
                SpriteID.MAP_ICON_AXE_SHOP,
                "Allows access to axe sales points."
        ));

        registry.register(new ShopUnlock(
                "SHOP_CANDLE_SHOP",
                "Candle Shop",
                SpriteID.MAP_ICON_CANDLE_SHOP,
                "Allows access to candle shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_CHAINMAIL_SHOP",
                "Chainmail Shop",
                SpriteID.MAP_ICON_CHAINMAIL_SHOP,
                "Allows access to chainmail shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_CLOTHES_SHOP",
                "Clothes Shop",
                SpriteID.MAP_ICON_CLOTHES_SHOP,
                "Allows access to clothes shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_CRAFTING_SHOP",
                "Crafting Shop",
                SpriteID.MAP_ICON_CRAFTING_SHOP,
                "Allows access to crafting shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FARMING_SHOP",
                "Farming Shop",
                SpriteID.MAP_ICON_FARMING_SHOP,
                "Allows access to farming shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FISHING_SHOP",
                "Fishing Shop",
                SpriteID.MAP_ICON_FISHING_SHOP,
                "Allows access to fishing shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FOOD_SHOP",
                "Food Shop",
                SpriteID.MAP_ICON_FOOD_SHOP,
                "Allows access to general food shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FOOD_SHOP_CUTLERY",
                "Cutlery Food Shop",
                SpriteID.MAP_ICON_FOOD_SHOP_CUTLERY,
                "Unlocks access to cutlery food shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FOOD_SHOP_FRUIT",
                "Fruit Food Shop",
                SpriteID.MAP_ICON_FOOD_SHOP_FRUIT,
                "Unlocks access to fruit food shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_FUR_TRADER",
                "Fur Trader",
                SpriteID.MAP_ICON_FUR_TRADER,
                "Allows access to fur trading shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_GEM_SHOP",
                "Gem Shop",
                SpriteID.MAP_ICON_GEM_SHOP,
                "Allows access to gem shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_HELMET_SHOP",
                "Helmet Shop",
                SpriteID.MAP_ICON_HELMET_SHOP,
                "Allows access to helmet shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_HUNTER_SHOP",
                "Hunter Shop",
                SpriteID.MAP_ICON_HUNTER_SHOP,
                "Allows access to hunter equipment shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_JEWELLERY_SHOP",
                "Jewellery Shop",
                SpriteID.MAP_ICON_JEWELLERY_SHOP,
                "Allows access to jewellery shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_MACE_SHOP",
                "Mace Shop",
                SpriteID.MAP_ICON_MACE_SHOP,
                "Allows access to mace shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_MAGIC_SHOP",
                "Magic Shop",
                SpriteID.MAP_ICON_MAGIC_SHOP,
                "Allows access to magic item shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_MINING_SHOP",
                "Mining Shop",
                SpriteID.MAP_ICON_MINING_SHOP,
                "Allows access to mining supply shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_PET_SHOP",
                "Pet Shop",
                SpriteID.MAP_ICON_PET_SHOP,
                "Allows access to pet shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_PLATEBODY_SHOP",
                "Platebody Shop",
                SpriteID.MAP_ICON_PLATEBODY_SHOP,
                "Allows access to platebody armour shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_PLATELEGS_SHOP",
                "Platelegs Shop",
                SpriteID.MAP_ICON_PLATELEGS_SHOP,
                "Allows access to platelegs shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_PLATESKIRT_SHOP",
                "Plateskirt Shop",
                SpriteID.MAP_ICON_PLATESKIRT_SHOP,
                "Allows access to plateskirt shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_SCIMITAR_SHOP",
                "Scimitar Shop",
                SpriteID.MAP_ICON_SCIMITAR_SHOP,
                "Allows access to scimitar shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_SHIELD_SHOP",
                "Shield Shop",
                SpriteID.MAP_ICON_SHIELD_SHOP,
                "Allows access to shield shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_SILK_TRADER",
                "Silk Trader",
                SpriteID.MAP_ICON_SILK_TRADER,
                "Allows access to silk trading shops."
        ));

        registry.register(new ShopUnlock(
                "SHOP_SILVER_SHOP",
                "Silver Shop",
                SpriteID.MAP_ICON_SILVER_SHOP,
                "Allows access to silver product shops."
        ));
    }
}
