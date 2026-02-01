package com.coinboundplugin;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.inject.Inject;
import javax.swing.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.coinboundplugin.save.AccountManager;
import com.coinboundplugin.save.SaveManager;
import com.coinboundplugin.data.*;
import com.coinboundplugin.overlays.*;
import com.google.inject.Provides;
import com.coinboundplugin.enforcement.*;
import com.coinboundplugin.pack.PackOption;
import com.coinboundplugin.pack.UnlockPackOption;
import com.coinboundplugin.requirements.AppearRequirement;
import com.coinboundplugin.unlocks.*;
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
@PluginDescriptor(
        name = "Coinbound game mode"
)
public class CoinboundPlugin extends Plugin {
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

    public List<PackOption> getCurrentPackOptions() {
        return saveManager.get().currentPackOptions;
    }

    @Getter
    private UnlockRegistry unlockRegistry;

    public Set<String> getUnlockedIds() {
        return saveManager.get().unlockedIds;
    }

    public int getPackBought() {
        return saveManager.get().packsBought;
    }

    public boolean isInMemberWorld() {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }

    public int replaceItemID = ItemID.LEAFLET_DROPPER_FLYER;
    public long currentCoins = 0;
    public int fillerItemsShort;

    public boolean isItemBlocked(int itemId) {
        return false; //TODO: Check if some items need to have a lock icon overlay
    }

    public List<Integer> ALCHEMY_WIDGETS = List.of(14286892, 14286869);
    public List<Integer> teleportWidgetIDs = List.of(
            // Normal spellbook
            14286874, //Lumbridge
            14286871, //Varrock
            14286877, //Falador
            14286879, //House
            14286882, //Camelot
            14286884, //Kourend
            14286889, //Ardougne
            14286891, //Civitas
            14286895, //Watchtower
            14286902, //Trollheim
            14286905, //Ape atoll
            14286928, //Boat
            // Ancient spellbook
            14286945, //Paddewwa
            14286946, //Senntisten
            14286947, //Kharyrll
            14286948, //Lassar
            14286949, //Dareeyak
            14286950, //Carrallangar
            14286921, //Target
            14286951, //Annakarl
            14286952, //Ghorrock
            //Lunar spellbook
            14286961, //Moonclan
            14286962, //Moonclan group
            14286997, //ourania
            14286965, //waterbirth
            14286966, //waterbrith group
            14286969, //barbarian
            14286970, //barbarian group
            14286973, //khazard
            14286974,  //khazard group
            14286981, //fishing guild
            14286921, //target
            14286982, //fishing guild group
            14286984, //catherby
            14286985,  //catherby group
            14286987, //ice plateau
            14286988,  //ice plateau group
            //Arceuus spellbook
            14287000, //library
            14287004, //draynor
            14287016, //battlefront
            14287006, //mind altar
            14287007, //respawn
            14287008, //salve graveyard
            14287009, //frankenstrain castle
            14287010, //west ardy
            14287011, //harmony
            14287012, //cemetary
            14287014, //barrows
            14286921, //target
            14287015 //ape atoll dungeon
    );

    @Provides
    CoinboundConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CoinboundConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        accountManager.start(accountKey ->
        {
            // swap active save here
            saveManager.onAccountChanged(accountKey);
        });
        //Setup all unlockable stuff
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
        loadPackOptionsFromConfig();
        RefreshAllBlockers();

        //Build the panel
        swingPanel = new CoinboundPanel(this);
        navButton = NavigationButton.builder()
                .tooltip("Roguelite")
                .icon(ImageUtil.loadImageResource(getClass(), "/icon.png"))
                .panel(swingPanel)
                .build();

        clientToolbar.addNavigation(navButton);

        log.debug("Roguelite plugin started!");
    }

    @Override
    protected void shutDown() throws Exception {
        log.debug("Roguelite plugin stopped!");
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

        //TODO: Clear inventory blocker (it clears on panel switch but still)
        clientToolbar.removeNavigation(navButton);

        saveManager.flushNow();
        accountManager.stop();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("coinboundplugin"))
            return;
        log.debug("Runelite config changes!");

        //Only refresh all content on actual changes
        if (Objects.equals(event.getKey(), "packsBought") || Objects.equals(event.getKey(), "unlockedIds") || Objects.equals(event.getKey(), "peakWealth"))
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
                    ShowPluginChat("<col=329114><b>All items dropped! </b></col> Please go to the Al Kharid flyererer and use the drop-trick to get flyers to fill up your inventory.", 3924);
                }
            }
            return;
        }

        if (saveManager.get().setupStage != SetupStage.SetupComplete)
            return;

        currentCoins = getCoinsInInventory();
        Debug("Current coins: " + currentCoins);

        long prevPeak = saveManager.get().peakWealth;
        if (currentCoins <= prevPeak)
            return;

        int packIndex = getPackBought() + getAvailablePacksToBuy() + 1;

        int reached = 0;
        long lastReq = 0;

        // count how many thresholds are now reachable
        while (currentCoins >= (lastReq = peakCoinsRequiredForPack(packIndex))) {
            // only count ones you haven't already "passed" before
            if (lastReq > prevPeak)
                reached++;

            packIndex++;
        }

        if (reached > 1) {
            saveManager.get().peakWealth = currentCoins;
            ShowPluginChat(
                    "<col=329114><b>" + reached + " new brackets reached!</b></col> Latest: "
                            + formatNumber(lastReq) + " gp. You can open new booster packs!",
                    3924
            );
            saveManager.markDirty();
        } else if (reached == 1) {
            saveManager.get().peakWealth = currentCoins;
            ShowPluginChat(
                    "<col=329114><b>New bracket reached!</b></col> "
                            + formatNumber(lastReq) + " gp. You can open new booster pack!",
                    3924
            );
            saveManager.markDirty();
        }
    }

    public String getLastUnlockDisplayName() {
        String returnName = saveManager.get().lastUnlockedName;
        if (Objects.equals(returnName, ""))
            return "None";
        return returnName;
    }

    void SetupCardButtons() {
        int index = 0;
        for (PackOption option : getCurrentPackOptions()) {
            Unlock unlock = ((UnlockPackOption) option).getUnlock();
            UnlockIcon icon = unlock.getIcon();
            BufferedImage image = getBufferedImageFromIcon(icon);

            SetupCardButton(index, option.getDisplayName(), option.getDisplayType(), option.getDescription(), image, option);
            index++;
        }
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
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g = buffered.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                return buffered;
            }
        }
        return null;
    }

    void SetupCardButton(int buttonIndex, String unlockName, String typeName, String description, BufferedImage image, PackOption option) {
        cardPickOverlay.setButton(buttonIndex, unlockName, typeName, description, image, () -> {
            clientThread.invoke(() -> onPackOptionSelected(option));
        });
    }

    private void RefreshAllBlockers() {
        skillBlocker.refreshAll();
        equipmentSlotBlocker.refreshAll();
        questBlocker.refreshAll();
        clientThread.invoke(inventoryBlocker::redrawInventory);
        levelCapsDirty = true; //Refreshes the check on level caps for skills
        if (swingPanel != null)
            swingPanel.refresh();
    }

    private void loadPackOptionsFromConfig() {
        if (saveManager.get().packChoiceState != PackChoiceState.PACKGENERATED) {
            return;
        }

        Debug("Player was choosing cards, loading from config");

        if (saveManager.get().packChoiceState != null)
            SetupCardButtons();
    }

    public void setFillerItemsShortAmount(int amount) {
        if (!accountManager.isAccountReady())
            return;
        fillerItemsShort = amount;
        if (fillerItemsShort <= 0 && saveManager.get().setupStage == SetupStage.GetFlyers) {
            ShowPluginChat("<col=329114><b>Inventory filled! </b></col> You're now 'free' to start your adventure!", 3924);
            saveManager.get().setupStage = SetupStage.SetupComplete;
            saveManager.markDirty();
            RefreshAllBlockers();
        }
    }

    public long getCoinsInInventory() {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        Debug("inventory: " + inventory);
        if (inventory == null) {
            return 0;
        }

        for (Item item : inventory.getItems()) {
            if (item.getId() == ItemID.COINS) {
                Debug("coins found: ");
                return item.getQuantity();
            }
        }

        return 0;
    }


    private static final int EXPECTED_SKILL_COUNT = 23;
    public boolean statsInitialized = false;

    @Subscribe
    public void onStatChanged(StatChanged event) {
        handleStatChange(event.getSkill(), event.getXp(), event.getLevel());
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted event) {
        if (!event.getCommand().equalsIgnoreCase("coinbound"))
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

        //Ignore hitpoints (always unlocked)
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

        //Ignore XP gained crossing level boundaries
        if (leveledUp) {
            if (!isSkillBracketUnlockedAtLevel(skill, level)) {
                ShowPluginChat("<col=ff0000><b>" + skill.getName() + " limit reached!</b></col> Unlock new ranges to continue this skill.", 2394);
            }
        } else if (deltaXp > 0 && !isSkillBracketUnlockedAtLevel(skill, level)) {
            long newValue = deltaXp + saveManager.get().illegalXPGained;
            saveManager.get().illegalXPGained = newValue;
            saveManager.markDirty();

            ShowPluginChat("<col=ff0000><b>" + skill.getName() + " locked!</b></col> You've earned XP in a range you haven't unlocked yet!", 2394);
            ShowPluginChat("You now have a total of " + newValue + " illegal XP.", -1);
            return;
        }
    }


    public long peakCoinsRequiredForPack(int packIndex) {
        double A = 0.45;
        double B = 3.1;
        double C = 6.0;

        return (long) Math.floor(A * Math.pow(packIndex + C, B));
    }

    public int getTotalUnlockedPacks(long peakCoins) {
        int pack = 1;

        while (true) {
            if (peakCoins < peakCoinsRequiredForPack(pack)) {
                return pack - 1;
            }
            pack++;
        }
    }

    public int getAvailablePacksToBuy() {
        int unlocked = getTotalUnlockedPacks(saveManager.get().peakWealth);
        return Math.max(0, unlocked - saveManager.get().packsBought);
    }

    public void onBuyPackClicked() {
        if (clientThread == null || saveManager.get().packChoiceState == PackChoiceState.PACKGENERATED)
            return;

        if (!accountManager.isAccountReady())
            return;

        clientThread.invoke(() ->
        {
            if (getAvailablePacksToBuy() < 1)
                return;

            if (!statsInitialized) {
                if (swingPanel != null) {
                    swingPanel.refresh();
                }
                return;
            }

            if (!generatePackOptions()) {
                ShowPluginChat("<col=ff0000><b>Cant make a pack!</b></col> There are 3 or less available cards (with requirements met)!", 2394);
                return;
            }
            PackBoughtSuccess();
        });
    }

    private void PackBoughtSuccess() {

        //TEMP: TODO TESTING NEW SAVE SYSTEM
        saveManager.get().packsBought++;
        saveManager.markDirty(); //Mark dirty to save on debounce

        // Refresh panel UI
        if (swingPanel != null) {
            swingPanel.refresh();
        }

        client.addChatMessage(
                ChatMessageType.GAMEMESSAGE,
                "",
                "Bought a pack",
                null
        );
    }

    public void onPackOptionSelected(PackOption option) {
        clientThread.invoke(() ->
        {
            ShowPluginChat("<col=329114><b>" + option.getDisplayName() + " unlocked! </b></col> ", 2308);
            option.onChosen(this);

            saveManager.get().packChoiceState = PackChoiceState.NONE;
            saveManager.get().currentPackOptions = List.of();

            saveManager.markDirty();
        });
    }

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
        Debug("Checking if skill " + skill.getName() + " is unlocked for level " + level + " (max allowed: " + maxAllowedLevelCached + ")");
        return level <= maxAllowedLevelCached;
    }

    public int getMaxAllowedLevelCached(Skill skill) {
        rebuildCapsIfNeeded();
        return cachedCaps.getOrDefault(skill, 1);
    }

    private final EnumMap<Skill, Integer> cachedCaps = new EnumMap<>(Skill.class);
    private boolean levelCapsDirty = true;

    private void rebuildCapsIfNeeded() {
        if (!levelCapsDirty) return;

        cachedCaps.clear();
        Set<String> unlocked = saveManager.get().unlockedIds;

        for (Skill skill : Skill.values()) {
            int cap = 1;
            for (int end : LEVEL_RANGES) {
                if (unlocked.contains("SKILL_" + skill.name() + "_" + end))
                    cap = end - 1;
            }
            cachedCaps.put(skill, cap);
        }

        levelCapsDirty = false;
    }

    public static final List<Integer> LEVEL_RANGES =
            List.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 99);

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
            saveManager.get().lastUnlockedName = displayName;
            saveManager.markDirty();
            RefreshAllBlockers();
            return true;
        }
        return false;
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

    private boolean generatePackOptions() {
        List<Unlock> locked = unlockRegistry.getAll().stream()
                .filter(u -> !saveManager.get().unlockedIds.contains(u.getId()))
                .filter(this::canAppearAsPackOption)
                .collect(Collectors.toList());

        Collections.shuffle(locked);

        int optionCount = Math.min(4, locked.size());
        Set<UnlockType> usedUnlockTypes = new HashSet<>();

        saveManager.get().currentPackOptions = new ArrayList<>(optionCount);
        for (int i = 0; i < optionCount; i++) {
            Unlock unlock = pickUnlockWithDiversityBias(locked, usedUnlockTypes);
            if (unlock == null)
                break;

            usedUnlockTypes.add(unlock.getType());
            saveManager.get().currentPackOptions.add(new UnlockPackOption(unlock));
        }

        int count = saveManager.get().currentPackOptions.size();

        if (count < 4)
            return false;

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
                "[<col=6069df>Roguelite Mode</col>] " + message,
                null
        );
        if (soundEffect != -1)
            client.playSoundEffect(soundEffect);
    }
}

