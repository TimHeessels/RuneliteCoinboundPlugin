package com.coinboundplugin.save;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.AccountHashChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Singleton
public class AccountManager
{
    private final Client client;
    private final ClientThread clientThread;
    private final EventBus eventBus;

    @Getter
    private Long accountHash; // null until known

    @Getter
    private String playerName; // may be null briefly

    @Getter
    private boolean ready;

    private Consumer<String> onAccountReadyOrChanged;

    @Inject
    public AccountManager(Client client, ClientThread clientThread, EventBus eventBus)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.eventBus = eventBus;
    }

    public void start(Consumer<String> onAccountReadyOrChanged)
    {
        this.onAccountReadyOrChanged = onAccountReadyOrChanged;
        eventBus.register(this);

        // Initialize from current state (useful during plugin reload)
        clientThread.invokeLater(this::refreshFromClientState);
    }

    public void stop()
    {
        eventBus.unregister(this);
        onAccountReadyOrChanged = null;
        clear();
    }

    public String getAccountKey()
    {
        if (accountHash == null)
            return null;

        // stable folder-friendly key
        return "account_" + Long.toUnsignedString(accountHash);
    }

    public boolean isAccountReady()
    {
        return ready && accountHash != null && client.getGameState() == GameState.LOGGED_IN;
    }

    @Subscribe
    public void onAccountHashChanged(AccountHashChanged ev)
    {
        // accountHash changes when switching characters/accounts
        Long newHash = client.getAccountHash();
        if (Objects.equals(accountHash, newHash))
            return;

        accountHash = newHash;
        playerName = null; // will be re-detected
        ready = false;

        // We might not yet have a local player on the same tick, so refresh shortly.
        clientThread.invokeLater(this::refreshFromClientState);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged ev)
    {
        GameState gs = ev.getGameState();

        // When returning to login screen, clear lock.
        if (gs == GameState.LOGIN_SCREEN)
        {
            clear();
            return;
        }

        // When logging in, try to finalize readiness.
        if (gs == GameState.LOGGED_IN)
        {
            clientThread.invokeLater(this::refreshFromClientState);
        }
    }

    /**
     * If your RuneLite version has a PlayerChanged event you like, you can add it.
     * This tick-based refresh is the simplest and generally works fine.
     */
    private void refreshFromClientState()
    {
        // Only consider "ready" while logged in.
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            ready = false;
            return;
        }

        if (accountHash == null)
        {
            // Some RL builds may not have account hash immediately at login.
            // Stay unready until AccountHashChanged arrives.
            ready = false;
            return;
        }

        Player p = client.getLocalPlayer();
        if (p != null)
        {
            playerName = p.getName();
        }

        // We consider ready once we have an account hash and are logged in.
        // playerName is optional.
        boolean newReady = true;

        if (!ready && newReady)
        {
            ready = true;
            fireAccountReadyOrChanged();
            return;
        }

        // If already ready, still check if playerName changed after being null
        // (common right after login).
        if (ready && playerName != null)
        {
            // optional: if you want a callback when name first appears
            // fireAccountReadyOrChanged();
        }
    }

    private void fireAccountReadyOrChanged()
    {
        if (onAccountReadyOrChanged == null)
            return;

        String key = getAccountKey();
        if (key == null)
            return;

        try
        {
            onAccountReadyOrChanged.accept(key);
        }
        catch (Exception e)
        {
            log.warn("Account callback threw", e);
        }
    }

    private void clear()
    {
        accountHash = null;
        playerName = null;
        ready = false;
    }
}