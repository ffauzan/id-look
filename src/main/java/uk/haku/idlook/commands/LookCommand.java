package uk.haku.idlook.commands;

import java.util.*;

import org.slf4j.Logger;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.config.Configuration;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Language;
import emu.grasscutter.utils.Utils;
import uk.haku.idlook.IdLookPlugin;
import uk.haku.idlook.utils.LanguageTools;
import uk.haku.idlook.utils.StringSimilarity;
import uk.haku.idlook.objects.PluginConfig;
import uk.haku.idlook.objects.QueryResult;

@Command(label = "look", usage = "look <keywords>", aliases = { "l",
        "gm" }, permission = "player.look", targetRequirement = Command.TargetRequirement.NONE)
public final class LookCommand implements CommandHandler {
    private static final PluginConfig config = IdLookPlugin.getInstance().getConfiguration();
    private int resultLimit = config.resultLimit;
    private int similarityScoreTreshold = config.scoreTreshold;
    private String targetLanguage = config.language;

    Logger logger = IdLookPlugin.getInstance().getLogger();

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        String lookQuery = String.join(" ", args);
        ArrayList<QueryResult> resultList = new ArrayList<QueryResult>();
        String langCode = getLanguage(sender);

        lookFor(lookQuery, resultList, langCode);

        Collections.sort(resultList);

        sendResult(sender, resultList, lookQuery);
    }

    public String getLanguage(Player player) {
        // If language in plugin config sets to auto, Get player languange
        if (targetLanguage.equals("auto")) {
            if (player == null) {
                return Utils.getLanguageCode(Configuration.LANGUAGE);
            }
            return Utils.getLanguageCode(player.getAccount().getLocale());

        } else if (targetLanguage.equals("server")) {
            return Configuration.DOCUMENT_LANGUAGE;
        } else {
            // Check if value of language in plugin config is valid
            if (LanguageTools.IsLanguageExist(targetLanguage)) {
                return targetLanguage;
            }
        }

        return Configuration.DOCUMENT_LANGUAGE;
    }

    public void sendResult(Player player, List<QueryResult> lookResult, String query) {
        if (lookResult.size() == 0) {
            CommandHandler.sendMessage(player, "Cannot find anything, try different keyword");
            return;
        } else if (lookResult.size() > resultLimit) {
            lookResult = lookResult.subList(0, resultLimit);
        }

        CommandHandler.sendMessage(player, "Result for: " + query);
        lookResult.forEach((data) -> {
            String name = data.Name;
            String itemType = data.ItemType;
            String responseMsg = "Id: " + data.Id + " | Name: " + name + " | Type: " + itemType;
            CommandHandler.sendMessage(player, responseMsg);
        });
        return;
    }

    public void lookFor(String query, ArrayList<QueryResult> lookResult, String langCode) {
        lookForAvatar(query, lookResult, langCode);
        lookForItem(query, lookResult, langCode);
        lookForMonster(query, lookResult, langCode);
        return;
    }

    public void lookForMonster(String query, ArrayList<QueryResult> lookResult, String langCode) {
        // Monster
        GameData.getMonsterDataMap().forEach((id, data) -> {
            Language.TextStrings nameDict = Language.getTextMapKey(data.getNameTextMapHash());
            String name = nameDict.get(langCode);
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, "Monsters", similarityScore.intValue()));
                }
            }
        });
        return;
    }

    public void lookForAvatar(String query, ArrayList<QueryResult> lookResult, String langCode) {
        // Avatars
        GameData.getAvatarDataMap().forEach((id, data) -> {
            Language.TextStrings nameDict = Language.getTextMapKey(data.getNameTextMapHash());
            String name = nameDict.get(langCode);
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, "Avatars", similarityScore.intValue()));
                }
            }
        });
        return;
    }

    public void lookForItem(String query, ArrayList<QueryResult> lookResult, String langCode) {
        // Item
        GameData.getItemDataMap().forEach((id, data) -> {
            Language.TextStrings nameDict = Language.getTextMapKey(data.getNameTextMapHash());
            String name = nameDict.get(langCode);
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, data.getItemType().name(), similarityScore.intValue()));
                }
            }
        });
        return;
    }
}
