package com.rogueliteplugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RoguelitePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RoguelitePlugin.class);
		RuneLite.main(args);
	}
}