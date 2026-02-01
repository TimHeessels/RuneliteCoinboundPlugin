package com.coinboundplugin.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Singleton
public class SaveStorage
{
    private static final String FILE_NAME = "save.json";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path baseDir;

    @Inject
    public SaveStorage()
    {
        this.baseDir = RUNELITE_DIR.toPath().resolve("coinboundPlugin");
    }

    public SaveData load(String accountKey)
    {
        if (accountKey == null || accountKey.isBlank())
            return new SaveData();

        Path file = getSaveFile(accountKey);

        if (!Files.exists(file))
            return new SaveData();

        try (Reader r = Files.newBufferedReader(file))
        {
            SaveData data = gson.fromJson(r, SaveData.class);
            return data != null ? data : new SaveData();
        }
        catch (Exception e)
        {
            return new SaveData();
        }
    }

    public void save(String accountKey, SaveData data)
    {
        if (accountKey == null || accountKey.isBlank() || data == null)
            return;

        data.lastUpdatedEpochMs = System.currentTimeMillis();

        Path dir = baseDir.resolve(accountKey);
        Path file = dir.resolve(FILE_NAME);
        Path tmp  = dir.resolve(FILE_NAME + ".tmp");

        try
        {
            Files.createDirectories(dir);

            try (Writer w = Files.newBufferedWriter(tmp))
            {
                gson.toJson(data, w);
            }

            try
            {
                Files.move(tmp, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ex)
            {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception e)
        {
            // log if you want
        }
    }

    private Path getSaveFile(String accountKey)
    {
        return baseDir.resolve(accountKey).resolve(FILE_NAME);
    }
}