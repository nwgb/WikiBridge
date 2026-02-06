package org.nwgb.wikibridge.Utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.nwgb.wikibridge.Wiki.ArticleData;
import net.kyori.adventure.text.Component;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Formats wiki links and creates a hover preview
 */
public class MessageFormatter {
    public static TextComponent formatLink(String article, ArticleData articleData, String existsColour, String missingColour, String dateFormat, String url) {
        String preview;
        Component hover;

        preview = articleData.getPreview();

        String articleName = article.replace("_", " ");

        // Make first occurrence of article title bold in the preview
        preview = preview.replaceFirst("(?i)" + java.util.regex.Pattern.quote(articleName), "§l$0§r");

        hover = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                    .deserialize(preview);

        // Add last edit under preview
        String lastEdit = articleData.getLastEdit();
        if (lastEdit != null && !lastEdit.isEmpty()) {
            String formattedDate = formatDate(articleData.getLastEdit(), dateFormat);

            hover = hover.append(Component.newline().append(Component.text("Click to keep reading | Last edit: " + formattedDate, NamedTextColor.GRAY)));
        }

        return Component.text(article)
                .color(TextColor.fromHexString(articleData.exists() ? existsColour : missingColour))
                .decorate(net.kyori.adventure.text.format.TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(hover))
                .clickEvent(ClickEvent.openUrl(url+"/"+article.replace(" ", "_")));
    }

    private static String formatDate(String timestamp, String dateFormat) {
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(timestamp);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            return zdt.format(formatter);
        } catch (Exception e) {
            return timestamp;
        }
    }
}