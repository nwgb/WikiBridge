package org.nwgb.wikibridge.Listeners;

import org.bukkit.event.Listener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.nwgb.wikibridge.WikiBridge;
import org.nwgb.wikibridge.Utils.MessageFormatter;
import org.nwgb.wikibridge.Wiki.ArticleData;


public class ChatListener implements Listener {
    private final WikiBridge plugin;
    private final Pattern linkPattern;
    private final String existsColour;
    private final String missingColour;
    private final String dateFormat;
    private final String url;

    public ChatListener(WikiBridge plugin, String existsColour, String missingColour, String dateFormat, String url) {
        this.plugin = plugin;
        this.linkPattern = Pattern.compile("\\[\\[([^\\]]+)\\]\\]"); // i.e. [[article]]
        this.existsColour = existsColour;
        this.missingColour = missingColour;
        this.dateFormat = dateFormat;
        this.url = url;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Matcher matcher = linkPattern.matcher(message);

        if (!matcher.find()) {
            return;
        }

        event.setCancelled(true);
        matcher.reset();

        CompletableFuture.supplyAsync(() -> {
            net.kyori.adventure.text.TextComponent.Builder messageBuilder = net.kyori.adventure.text.Component.text();

            // Include player name in reconstructed message
            messageBuilder.append(net.kyori.adventure.text.Component.text("<"))
                    .append(event.getPlayer().displayName())
                    .append(net.kyori.adventure.text.Component.text("> "));


            int linkEnd = 0;

            // Replace each [[article]] with formatted link
            while (matcher.find()) {
                messageBuilder.append(net.kyori.adventure.text.Component.text(message.substring(linkEnd, matcher.start())));

                String article = matcher.group(1);
                ArticleData articleData = null;
                articleData = plugin.getWikiUtils().getArticle(article);

                messageBuilder.append(MessageFormatter.formatLink(article,
                        articleData,
                        existsColour,
                        missingColour,
                        dateFormat,
                        url
                ));

                linkEnd = matcher.end(); // Update position to closing bracket of link
            }

            messageBuilder.append(net.kyori.adventure.text.Component.text(message.substring(linkEnd)));
            return messageBuilder.build();

        }).thenAccept(finalComponent -> {
            plugin.getServer().getGlobalRegionScheduler().run(plugin, (task) -> {
                // Send message to each player
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.sendMessage(finalComponent);
                }
            });
        });
    }}