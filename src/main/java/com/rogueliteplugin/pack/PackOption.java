package com.rogueliteplugin.pack;

import com.rogueliteplugin.RoguelitePlugin;

import javax.swing.Icon;

public interface PackOption {
    String getDisplayName();

    void onChosen(RoguelitePlugin plugin);
}