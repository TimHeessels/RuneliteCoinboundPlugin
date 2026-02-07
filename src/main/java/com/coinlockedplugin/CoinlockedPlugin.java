package com.coinlockedplugin;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.inject.Inject;
import javax.swing.*;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.coinlockedplugin.save.AccountManager;
import com.coinlockedplugin.save.SaveManager;
import com.coinlockedplugin.data.*;
import com.coinlockedplugin.overlays.*;
import com.google.inject.Provides;
import com.coinlockedplugin.enforcement.*;
import com.coinlockedplugin.pack.PackOption;
import com.coinlockedplugin.pack.UnlockPackOption;
import com.coinlockedplugin.requirements.AppearRequirement;
import com.coinlockedplugin.unlocks.*;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.callback.ClientThread;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;

import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.util.ImageUtil;

import static net.runelite.client.util.QuantityFormatter.formatNumber;

@Slf4j
@PluginDescriptor(name = "Coinbound game mode")
public class CoinlockedPlugin extends Plugin {
    @Inject
    private Client client;

    public Client getClient() {
        return client;
    }

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SkillBlocker skillBlocker;

    @Inject
    private QuestBlocker questBlocker;

    @Inject
    public EquipmentSlotBlocker equipmentSlotBlocker;

    @Inject
    private ShopBlocker shopBlocker;

    @Inject
    private EventBus eventBus;

    @Inject
    private CoinboundConfig config;

    public CoinboundConfig getConfig() {
        return config;
    }

    @Inject
    private ConfigManager configManager;

    @Inject
    private SkillIconManager skillIconManager;

    private final Map<Skill, Integer> previousXp = new EnumMap<>(Skill.class);
    private final EnumMap<Skill, Integer> previousLevel = new EnumMap<>(Skill.class);

    private Map<Skill, Integer> maxAllowedLevel = new EnumMap<>(Skill.class);

    @Inject
    private CoinboundInfoboxOverlay overlay;

    @Inject
    private InventoryBlocker inventoryBlocker;

    @Inject
    private MenuOptionBlocker teleportBlocker;

    @Inject
    private InventoryFillerTooltip inventoryFillerTooltip;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    CardPickOverlay cardPickOverlay;

    @Inject
    ShopLockOverlay shopLockOverlay;

    @Inject
    SpellBookLockOverlay spellTeleportLockOverlay;

    @Inject
    ItemLockOverlay itemLockOverlay;

    @Inject
    ProtectionPrayerLockOverlay protectionPrayerLockOverlay;

    @Inject
    SpecialAttackOverlay specialAttackOverlay;

    @Inject
    private AccountManager accountManager;
    @Inject
    private SaveManager saveManager;

    private CoinboundPanel swingPanel;
    private NavigationButton navButton;

    private final Random random = new Random();

    public SetupStage getSetupStage() {
        return saveManager.get().setupStage;
    }

    public PackChoiceState getPackChoiceState() {
        return saveManager.get().packChoiceState;
    }

    public List<PackOption> currentPackOptions = new ArrayList<>();

    @Getter
    private UnlockRegistry unlockRegistry;

    public Set<String> getUnlockedIds() {
        return saveManager.get().unlockedIds;
    }

    public int getPackBought() {
        return saveManager.get().packsBought;
    }

    public int getCurrentPoints() {
        return saveManager.get().points;
    }

    public boolean isInMemberWorld() {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }

    public int replaceItemID = ItemID.LEAFLET_DROPPER_FLYER;
    public int fillerItemsShort;
    public boolean activeShopIsBlocked;

    public boolean isItemBlocked(int itemId) {
        return false; // TODO: Check if some items need to have a lock icon overlay
    }

    // Formatted string listing new possible unlocks after an unlock is gained
    public String newPossibleUnlocksString = "";

    //Thanks to c-engineers plugin for the regex patterns to track unlocks from chat messages, I adapted them to my needs
    private static final Pattern COLLECTION_LOG_ITEM_REGEX = Pattern.compile("New item added to your collection log:.*");
    private static final Pattern MUSIC_TRACK_REGEX = Pattern.compile("You have unlocked a new music track:.*");
    private static final String ACHIEVEMENT_DIARY_STRING = "Your Achievement Diary has been updated.";
    private static final Pattern COMBAT_TASK_REGEX = Pattern.compile("CA_ID:\\d+\\|Congratulations, you've completed an? \\w+ combat task:.*");
    private static final Pattern QUEST_REGEX = Pattern.compile("Congratulations, you've completed a quest:.*");
    private static final Set<String> PetMessages = Set.of(
            "You have a funny feeling like you're being followed",
            "You feel something weird sneaking into your backpack",
            "You have a funny feeling like you would have been followed"
    );

    public List<Integer> ALCHEMY_WIDGETS = List.of(14286892, 14286869);
    public List<Integer> teleportWidgetIDs = List.of(
            // Normal spellbook
            14286874, // Lumbridge
            14286871, // Varrock
            14286877, // Falador
            14286879, // House
            14286882, // Camelot
            14286884, // Kourend
            14286889, // Ardougne
            14286891, // Civitas
            14286895, // Watchtower
            14286902, // Trollheim
            14286905, // Ape atoll
            14286928, // Boat
            // Ancient spellbook
            14286945, // Paddewwa
            14286946, // Senntisten
            14286947, // Kharyrll
            14286948, // Lassar
            14286949, // Dareeyak
            14286950, // Carrallangar
            14286921, // Target
            14286951, // Annakarl
            14286952, // Ghorrock
            // Lunar spellbook
            14286961, // Moonclan
            14286962, // Moonclan group
            14286997, // ourania
            14286965, // waterbirth
            14286966, // waterbrith group
            14286969, // barbarian
            14286970, // barbarian group
            14286973, // khazard
            14286974, // khazard group
            14286981, // fishing guild
            14286921, // target
            14286982, // fishing guild group
            14286984, // catherby
            14286985, // catherby group
            14286987, // ice plateau
            14286988, // ice plateau group
            // Arceuus spellbook
            14287000, // library
            14287004, // draynor
            14287016, // battlefront
            14287006, // mind altar
            14287007, // respawn
            14287008, // salve graveyard
            14287009, // frankenstrain castle
            14287010, // west ardy
            14287011, // harmony
            14287012, // cemetary
            14287014, // barrows
            14286921, // target
            14287015 // ape atoll dungeon
    );

    @Provides
    CoinboundConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CoinboundConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        accountManager.start(accountKey -> {
            // swap active save here
            saveManager.onAccountChanged(accountKey);
            loadPackOptionsFromConfig();
        });
        // Setup all unlockable stuff
        unlockRegistry = new UnlockRegistry();
        UnlockDefinitions.registerAll(unlockRegistry, skillIconManager, this);

        overlayManager.add(overlay);
        overlayManager.add(cardPickOverlay);
        overlayManager.add(spellTeleportLockOverlay);
        overlayManager.add(itemLockOverlay);
        overlayManager.add(shopLockOverlay);
        overlayManager.add(protectionPrayerLockOverlay);
        overlayManager.add(specialAttackOverlay);
        cardPickOverlay.start();

        eventBus.register(skillBlocker);
        eventBus.register(questBlocker);
        eventBus.register(equipmentSlotBlocker);

        eventBus.register(shopBlocker);
        eventBus.register(inventoryBlocker);
        eventBus.register(teleportBlocker);
        overlayManager.add(inventoryFillerTooltip);

        RefreshAllBlockers();

        // Build the panel
        swingPanel = new CoinboundPanel(this);
        navButton = NavigationButton.builder()
                .tooltip("Coinlocked")
                .icon(ImageUtil.loadImageResource(getClass(), "/icon.png"))
                .panel(swingPanel)
                .build();

        clientToolbar.addNavigation(navButton);

        log.debug("Coinlocked plugin started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.debug("Coinlocked plugin stopped!");
        previousXp.clear();

        overlayManager.remove(overlay);
        overlayManager.remove(cardPickOverlay);
        overlayManager.remove(spellTeleportLockOverlay);
        overlayManager.remove(itemLockOverlay);
        overlayManager.remove(shopLockOverlay);
        overlayManager.remove(protectionPrayerLockOverlay);
        overlayManager.remove(specialAttackOverlay);
        cardPickOverlay.stop();

        eventBus.unregister(skillBlocker);
        eventBus.unregister(questBlocker);
        eventBus.unregister(equipmentSlotBlocker);
        eventBus.unregister(shopBlocker);
        eventBus.unregister(inventoryBlocker);
        eventBus.unregister(teleportBlocker);
        overlayManager.remove(inventoryFillerTooltip);
        equipmentSlotBlocker.clearAll();
        skillBlocker.clearAll();
        questBlocker.clearAll();

        // TODO: Clear inventory blocker (it clears on panel switch but still)
        clientToolbar.removeNavigation(navButton);

        saveManager.flushNow();
        accountManager.stop();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("coinlockedplugin"))
            return;
        log.debug("Runelite config changes!");

        // Only refresh all content on actual changes
        if (Objects.equals(event.getKey(), "packsBought") || Objects.equals(event.getKey(), "unlockedIds")
                || Objects.equals(event.getKey(), "peakWealth"))
            RefreshAllBlockers();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (!accountManager.isAccountReady())
            return;

        Debug("Container changed");
        if (event.getContainerId() != InventoryID.INVENTORY.getId()) {
            return;
        }
        if (saveManager.get().setupStage == SetupStage.DropAllItems) {
            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            if (inventory != null) {
                int itemCount = (int) Arrays.stream(inventory.getItems())
                        .filter(item -> item.getId() != -1)
                        .count();
                Debug("Items in inventory: " + itemCount);
                if (itemCount < 1) {
                    saveManager.get().setupStage = SetupStage.GetFlyers;
                    saveManager.markDirty();
                    ShowPluginChat(
                            "<col=329114><b>All items dropped! </b></col> Please go to the Al Kharid flyererer and use the drop-trick to get flyers to fill up your inventory.",
                            3924);
                }
            }
            return;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!accountManager.isAccountReady())
            return;

        // Only look at game chat style messages
        ChatMessageType type = event.getType();
        if (type != ChatMessageType.GAMEMESSAGE
                && type != ChatMessageType.SPAM
                && type != ChatMessageType.ENGINE) {
            return;
        }

        String msg = event.getMessage();
        if (msg == null || msg.isBlank())
            return;

        if (COLLECTION_LOG_ITEM_REGEX.matcher(msg).matches()) {
            rewardPoint(PointRewards.CollectionLogEntry);
            return;
        }
        if (QUEST_REGEX.matcher(msg).matches()) {
            rewardPoint(PointRewards.QuestComplete);
            return;
        }
        if (COMBAT_TASK_REGEX.matcher(msg).matches()) {
            rewardPoint(PointRewards.CombatAchievement);
            return;
        }
        if (MUSIC_TRACK_REGEX.matcher(msg).matches()) {
            rewardPoint(PointRewards.MusicTrack);
            return;
        }
        if (msg.contains(ACHIEVEMENT_DIARY_STRING)) {
            rewardPoint(PointRewards.DiaryStep);
            return;
        }
        if (PetMessages.contains(msg)) {
            rewardPoint(PointRewards.Pet);
            return;
        }
    }

    void SetupCardButtons() {
        int index = 0;
        for (PackOption option : currentPackOptions) {
            BufferedImage image = getOptionImageOrNull(option);
            if (image == null) {
                // If there is any unlock not found, refund the pack and reset active option.
                refundPack();
                return;
            }
            cardPickOverlay.setButton(index, option.getDisplayName(), option.getDisplayType(), option.getDescription(), image, () -> {
                clientThread.invoke(() -> onPackOptionSelected(option));
            });
            index++;
        }
    }

    private void refundPack() {
        ShowPluginChat(
                "<col=ff0000><b>Pack refund!</b></col> There was an error loading one of the pack options, refunding your pack.",
                2394);
        saveManager.get().packChoiceState = PackChoiceState.NONE;
        saveManager.get().currentPackOptionIds = new ArrayList<>();
        currentPackOptions = new ArrayList<>();
        saveManager.get().packsBought--;
        saveManager.markDirty();
    }

    private BufferedImage getOptionImageOrNull(PackOption option) {
        if (!(option instanceof UnlockPackOption))
            return null;

        Unlock unlock = ((UnlockPackOption) option).getUnlock();
        if (unlock == null)
            return null;

        UnlockIcon icon = unlock.getIcon();
        if (icon == null)
            return null;

        return getBufferedImageFromIcon(icon);
    }

    private BufferedImage getBufferedImageFromIcon(UnlockIcon icon) {
        if (icon == null) {
            return null;
        }
        if (icon instanceof ImageUnlockIcon) {
            Icon swingIcon = ((ImageUnlockIcon) icon).getIcon();
            if (swingIcon instanceof ImageIcon) {
                Image img = ((ImageIcon) swingIcon).getImage();
                if (img instanceof BufferedImage) {
                    return (BufferedImage) img;
                }
                // Convert Image to BufferedImage
                BufferedImage buffered = new BufferedImage(
                        img.getWidth(null),
                        img.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = buffered.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                return buffered;
            }
        }
        return null;
    }

    private void RefreshAllBlockers() {
        skillBlocker.refreshAll();
        equipmentSlotBlocker.refreshAll();
        questBlocker.refreshAll();
        clientThread.invoke(inventoryBlocker::redrawInventory);
        levelCapsDirty = true; // Refreshes the check on level caps for skills
        if (swingPanel != null)
            swingPanel.refresh();
    }

    private void loadPackOptionsFromConfig() {

        Debug("saveManager.get().packChoiceState: " + saveManager.get().packChoiceState);
        if (saveManager.get().packChoiceState != PackChoiceState.PACKGENERATED) {
            return;
        }

        Debug("Player was choosing cards, loading from config");
        Debug("Pack state: " + saveManager.get().packChoiceState);
        Debug("Pack size from IDs: " + saveManager.get().currentPackOptionIds.size());

        currentPackOptions = saveManager.get().currentPackOptionIds.stream()
                .map(id -> {
                    Unlock unlock = unlockRegistry.get(id);
                    if (unlock != null) {
                        return new UnlockPackOption(unlock);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Debug("Pack size from currentPackOptions: " + currentPackOptions.size());

        if (saveManager.get().packChoiceState == null)
            return;
        if (currentPackOptions.size() == 4) {
            // Update unlockable list so we know what unlocks are new after selection
            updateUnlockableList();
            SetupCardButtons();
        } else
            refundPack();
    }

    public void setFillerItemsShortAmount(int amount) {
        if (!accountManager.isAccountReady())
            return;
        fillerItemsShort = amount;
        if (fillerItemsShort <= 0 && saveManager.get().setupStage == SetupStage.GetFlyers) {
            ShowPluginChat("<col=329114><b>Inventory filled! </b></col> You're now 'free' to start your adventure!",
                    3924);
            saveManager.get().setupStage = SetupStage.SetupComplete;
            saveManager.markDirty();
            RefreshAllBlockers();
        }
    }

    private static final int EXPECTED_SKILL_COUNT = 23;
    public boolean statsInitialized = false;

    @Subscribe
    public void onStatChanged(StatChanged event) {
        handleStatChange(event.getSkill(), event.getXp(), event.getLevel());
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted event) {
        if (!event.getCommand().equalsIgnoreCase("coinlocked"))
            return;

        String[] args = event.getArguments();
        if (args.length == 0) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "levelup":
                devSimulateLevelUp(Skill.SAILING);
                break;
        }
    }

    public void devSimulateLevelUp(Skill skill) {
        int xp = previousXp.getOrDefault(skill, 0) + 1000;
        int level = previousLevel.getOrDefault(skill, 1) + 1;
        handleStatChange(skill, xp, level);
    }

    void handleStatChange(Skill skill, int xp, int level) {

        // Ignore hitpoints (always unlocked)
        if (skill == Skill.HITPOINTS)
            return;

        Integer prevXp = previousXp.put(skill, xp);
        Integer prevLevel = previousLevel.put(skill, level);

        Debug(xp + " xp gained in " + skill.getName() + " size: " + previousXp.size());

        if (!statsInitialized && previousXp.size() >= EXPECTED_SKILL_COUNT) {
            statsInitialized = true;

            for (Skill s : Skill.values()) {
                previousLevel.put(s, client.getRealSkillLevel(s));
            }

            if (swingPanel != null) {
                Debug("Refreshing panel due to login");
                swingPanel.refresh();
            }
        }

        // Ignore first-time entries (maps not seeded yet)
        if (prevXp == null || prevLevel == null || !statsInitialized)
            return;

        int deltaXp = xp - prevXp;
        boolean leveledUp = level > prevLevel;

        if (deltaXp <= 0 && !leveledUp)
            return;

        if (!accountManager.isAccountReady())
            return;

        // Ignore XP gained crossing level boundaries
        if (leveledUp) {
            //Give a point for each level up
            for (int i = 0; i < (level - prevLevel); i++)
                rewardPoint(PointRewards.LevelUp);

            if (!isSkillBracketUnlockedAtLevel(skill, level)) {
                ShowPluginChat("<col=ff0000><b>" + skill.getName()
                        + " limit reached!</b></col> Unlock new ranges to continue this skill.", 2394);
            }
        } else if (deltaXp > 0 && !isSkillBracketUnlockedAtLevel(skill, level)) {
            long newValue = deltaXp + saveManager.get().illegalXPGained;
            saveManager.get().illegalXPGained = newValue;
            saveManager.markDirty();

            ShowPluginChat("<col=ff0000><b>" + skill.getName()
                    + " locked!</b></col> You've earned XP in a range you haven't unlocked yet!", 2394);
            ShowPluginChat("You now have a total of " + newValue + " illegal XP.", -1);
            return;
        }
    }

    public void rewardPoint(PointRewards type) {
        saveManager.get().points += type.getPoints();
        saveManager.markDirty();
        ShowPluginChat("<col=329114><b>Gained " + type.getPoints() + " point(s) </b>" + type.getMessage(), -1);
    }

    public int getPossibleUnlockablesCount() {
        return currentPossibleUnlockables.size();
    }

    public int getRestrictedUnlockablesCount() {
        return currentLockedUnlockables.size();
    }

    public void onBuyPackClicked() {
        if (clientThread == null || saveManager.get().packChoiceState == PackChoiceState.PACKGENERATED)
            return;

        if (!accountManager.isAccountReady())
            return;

        clientThread.invoke(() -> {
            if (saveManager.get().points < packCost)
                return;

            if (!statsInitialized) {
                if (swingPanel != null) {
                    swingPanel.refresh();
                }
                return;
            }

            if (!generatePackOptions()) {
                ShowPluginChat(
                        "<col=ff0000><b>Cant make a pack!</b></col> There are 3 or less available cards (with requirements met)!",
                        2394);
                return;
            }
            PackBoughtSuccess();
        });
    }

    private void PackBoughtSuccess() {

        saveManager.get().packsBought++;
        saveManager.markDirty(); // Mark dirty to save on debounce

        // Refresh panel UI
        if (swingPanel != null) {
            swingPanel.refresh();
        }

        client.addChatMessage(
                ChatMessageType.GAMEMESSAGE,
                "",
                "Bought a pack",
                null);
    }

    public void onPackOptionSelected(PackOption option) {
        clientThread.invoke(() -> {
            ShowPluginChat("<col=329114><b>" + option.getDisplayName() + " unlocked! </b></col> ", 2308);
            option.onChosen(this);

            saveManager.get().packChoiceState = PackChoiceState.NONE;
            currentPackOptions = List.of();
            saveManager.get().currentPackOptionIds = new ArrayList<>();

            saveManager.markDirty();
        });
    }

    public int getNewPackAvailableCount() {
        return packCost > 0 ? getCurrentPoints() / packCost : 0;
    }

    public int packCost = 10;

    public boolean canAppearAsPackOption(Unlock unlock) {
        if (unlock == null || unlockRegistry == null) {
            return false;
        }

        if (isUnlocked(unlock)) {
            return false;
        }

        List<AppearRequirement> reqs = unlock.getRequirements();
        if (reqs == null || reqs.isEmpty()) {
            return true;
        }

        for (AppearRequirement req : reqs) {
            try {
                if (!req.isMet(this, this.getUnlockedIds())) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean isSkillBracketUnlocked(Skill skill) {
        if (skill == Skill.HITPOINTS)
            return true;
        int playerLevel = client.getRealSkillLevel(skill);
        return isSkillBracketUnlockedAtLevel(skill, playerLevel);
    }

    public boolean isSkillBracketUnlockedAtLevel(Skill skill, int level) {
        if (skill == Skill.HITPOINTS)
            return true;
        int maxAllowedLevelCached = getMaxAllowedLevelCached(skill);
        Debug("Skill " + skill.getName() + " level " + level + " max allowed: " + maxAllowedLevelCached);
        if (maxAllowedLevelCached < 1)
            return false;
        return level <= maxAllowedLevelCached;
    }

    public int getMaxAllowedLevelCached(Skill skill) {
        rebuildCapsIfNeeded();
        return cachedCaps.getOrDefault(skill, 0);
    }

    private final EnumMap<Skill, Integer> cachedCaps = new EnumMap<>(Skill.class);
    private boolean levelCapsDirty = true;

    private void rebuildCapsIfNeeded() {
        if (!levelCapsDirty)
            return;

        cachedCaps.clear();
        Set<String> unlocked = saveManager.get().unlockedIds;

        for (Skill skill : Skill.values()) {
            int cap = 0;
            for (int end : LEVEL_RANGES) {
                if (unlocked.contains("SKILL_" + skill.name() + "_" + end))
                    cap = end - 1;
            }
            cachedCaps.put(skill, cap);
        }

        levelCapsDirty = false;
    }

    public static final List<Integer> LEVEL_RANGES = List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 99);

    public boolean isUnlocked(String unlockId) {
        Unlock unlock = unlockRegistry.get(unlockId);
        if (unlock == null) {
            return false;
        }

        return isUnlocked(unlock);
    }

    public boolean isUnlocked(Unlock unlock) {
        return saveManager.get().unlockedIds.contains(unlock.getId());
    }

    public void toggleUnlock(String unlockID) {
        if (isUnlocked(unlockID)) {
            if (removeUnlock(unlockID) == null) {
                ShowPluginChat("Account not ready to toggle unlocks.", -1);
            }
        } else {
            if (!unlock(unlockID, ""))
                ShowPluginChat("Account not ready to toggle unlocks.", -1);
        }
    }

    public boolean unlock(String unlockID, String displayName) {
        if (!accountManager.isAccountReady())
            return false;
        if (saveManager.get().unlockedIds.add(unlockID)) {

            List<Unlock> lastLockedUnlockables = new ArrayList<>(currentLockedUnlockables);
            updateUnlockableList(); // Updates both lastPossibleUnlockables and lastLockedUnlockables

            // Build a set of "previously seen" unlock IDs (possible + locked = all known
            // before)
            Set<String> previouslyLockedIds = lastLockedUnlockables.stream()
                    .map(Unlock::getId)
                    .collect(Collectors.toSet());

            List<Unlock> newlyPossible = currentPossibleUnlockables.stream()
                    .filter(u -> previouslyLockedIds.contains(u.getId()))
                    .collect(Collectors.toList());

            newPossibleUnlocksString = "";
            if (newlyPossible.size() == 1)
                newPossibleUnlocksString = "A new card can now appear in the booster packs: ";
            else if (newlyPossible.size() > 1)
                newPossibleUnlocksString = newlyPossible.size() + " new cards can now appear in the booster packs: ";
            if (!newPossibleUnlocksString.equals("")) {
                ShowPluginChat(newPossibleUnlocksString, -1);
                List<String> names = newlyPossible.stream()
                        .map(Unlock::getDisplayName)
                        .collect(Collectors.toList());
                String readableList = readableListChat(names);
                ShowPluginChat(readableList, -1);
                newPossibleUnlocksString += " " + readableList;
            } else
                newPossibleUnlocksString = "This unlock provides no new possible booster pack cards.";

            saveManager.markDirty();
            RefreshAllBlockers();
            return true;
        }
        return false;
    }

    private static String readableListChat(List<String> items) {
        final int maxLength = 80;

        if (items == null)
            return "";

        List<String> cleaned = items.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        if (cleaned.isEmpty())
            return "";

        List<String> used = new ArrayList<>();
        int length = 0;

        for (String item : cleaned) {
            int extra = used.isEmpty() ? item.length()
                    : (used.size() == 1 ? 5 : 2) + item.length(); // " and " or ", "

            if (length + extra > maxLength)
                break;

            used.add(item);
            length += extra;
        }

        int remaining = cleaned.size() - used.size();

        String result;
        if (used.size() == 1) {
            result = used.get(0);
        } else if (used.size() == 2) {
            result = used.get(0) + " and " + used.get(1);
        } else {
            result = String.join(", ", used.subList(0, used.size() - 1))
                    + ", and " + used.get(used.size() - 1);
        }

        if (remaining > 0) {
            String suffix = " and " + remaining + " more";
            if (result.length() + suffix.length() <= maxLength)
                result += suffix;
        }

        return result;
    }

    public String removeUnlock(String unlockID) {
        if (!accountManager.isAccountReady())
            return null;

        if (saveManager.get().unlockedIds.isEmpty())
            return null;

        if (!saveManager.get().unlockedIds.contains(unlockID))
            return null;

        saveManager.get().unlockedIds.remove(unlockID);
        saveManager.markDirty();

        // Refresh UI and blockers
        RefreshAllBlockers();

        // Get the unlock's display name
        Unlock unlock = unlockRegistry.get(unlockID);
        return unlock != null ? unlock.getDisplayName() : unlockID;
    }

    List<Unlock> currentPossibleUnlockables = new ArrayList<>();
    List<Unlock> currentLockedUnlockables = new ArrayList<>();

    void updateUnlockableList() {
        Set<String> unlocked = saveManager.get().unlockedIds;
        Map<Boolean, List<Unlock>> parts = unlockRegistry.getAll().stream()
                .filter(u -> !unlocked.contains(u.getId()))
                .collect(Collectors.partitioningBy(this::canAppearAsPackOption));
        currentPossibleUnlockables = parts.get(true);
        currentLockedUnlockables = parts.get(false);
    }

    /// Generates 4 cards for the pack with the following rules
    /// Prefered only from the list of unlockables that meet requirements
    /// If not enough (<4) add just enough from unlockables that don't meet reqs
    /// If still not enough, can't generate pack.
    private boolean generatePackOptions() {
        updateUnlockableList();
        List<Unlock> pool = new ArrayList<>(currentPossibleUnlockables);

        // Not enough valid unlocks, add just enough locked unlocks so there are four
        int needed = 4 - pool.size();
        if (needed > 0) {
            Collections.shuffle(currentLockedUnlockables);
            pool.addAll(currentLockedUnlockables.subList(0, Math.min(needed, currentLockedUnlockables.size())));
        }

        // Still not enough to make a full pack, return false
        if (pool.size() < 4)
            return false;

        Collections.shuffle(pool);
        int optionCount = Math.min(4, pool.size());
        Set<UnlockType> usedUnlockTypes = new HashSet<>();

        currentPackOptions = new ArrayList<>(optionCount);
        saveManager.get().currentPackOptionIds = new ArrayList<>();
        for (int i = 0; i < optionCount; i++) {
            Unlock unlock = pickUnlockWithDiversityBias(pool, usedUnlockTypes);
            if (unlock == null)
                break;

            pool.remove(unlock);
            usedUnlockTypes.add(unlock.getType());
            currentPackOptions.add(new UnlockPackOption(unlock));
            saveManager.get().currentPackOptionIds.add(unlock.getId());
        }

        saveManager.get().packChoiceState = PackChoiceState.PACKGENERATED;
        SetupCardButtons();

        saveManager.markDirty();
        return true;
    }

    private Unlock pickUnlockWithDiversityBias(
            List<Unlock> candidates,
            Set<UnlockType> usedTypes) {
        List<Unlock> preferred = candidates.stream()
                .filter(u -> !usedTypes.contains(u.getType()))
                .collect(Collectors.toList());

        List<Unlock> pool = preferred.isEmpty() ? candidates : preferred;
        return pool.get(random.nextInt(pool.size()));
    }

    public void Debug(String textToDebug) {
        log.debug(textToDebug);
    }

    public void ShowPluginChat(String message, int soundEffect) {
        client.addChatMessage(
                ChatMessageType.ENGINE,
                "",
                "[<col=6069df>Coinlocked</col>] " + message,
                null);
        if (soundEffect != -1)
            client.playSoundEffect(soundEffect);
    }
}
