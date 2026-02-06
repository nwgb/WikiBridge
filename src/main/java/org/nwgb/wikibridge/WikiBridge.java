package org.nwgb.wikibridge;

import org.bukkit.plugin.java.JavaPlugin;
import org.nwgb.wikibridge.Listeners.ChatListener;
import org.nwgb.wikibridge.Wiki.WikiClient;

public final class WikiBridge extends JavaPlugin {
    private WikiClient wikiClient;
    private Config config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = new Config(this);

        this.wikiClient = new WikiClient(
                config.getWikiName(),
                config.getApiEndpoint(),
                config.getMaxPreviewChars(),
                config.getMissingPreview(),
                this.getLogger()
        );

        getServer().getPluginManager().registerEvents(new ChatListener(this,
                        config.getExistsColour(),
                        config.getMissingColour(),
                        config.getDateFormat(),
                        config.getURL()
                ), this
        );
        getLogger().info("WikiBridge enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("WikiBridge disabled.");
    }

    public WikiClient getWikiUtils() {
        return wikiClient;
    }
}
