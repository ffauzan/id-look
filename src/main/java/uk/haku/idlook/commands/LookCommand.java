package uk.haku.idlook.commands;

import java.util.*;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.player.Player;

import uk.haku.idlook.IdLookPlugin;
import uk.haku.idlook.utils.StringSimilarity;
import uk.haku.idlook.objects.PluginConfig;
import uk.haku.idlook.objects.QueryResult;


@Command(label = "look", description = "Look command", 
        usage = "look <keywords>", aliases = {"l", "gm"}, permission = "player.look", targetRequirement = Command.TargetRequirement.NONE)
public final class LookCommand implements CommandHandler {
    private static final PluginConfig config = IdLookPlugin.getInstance().getConfiguration();
    private int resultLimit = config.resultLimit;
    private int similarityScoreTreshold = config.scoreTreshold;

    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        String lookQuery = String.join(" ", args);
        ArrayList<QueryResult> resultList = new ArrayList<QueryResult>();

        lookFor(lookQuery, resultList);

        Collections.sort(resultList);

        sendResult(sender, resultList, lookQuery);
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


    public void lookFor(String query, ArrayList<QueryResult> lookResult) {
        lookForAvatar(query, lookResult);
        lookForItem(query, lookResult);
        lookForMonster(query, lookResult);
        return;
    }


    public void lookForMonster(String query, ArrayList<QueryResult> lookResult) {
        // Monster
        GameData.getMonsterDataMap().forEach((id, data) -> {
            String name = IdLookPlugin.getInstance().getItemTextMap().get(data.getNameTextMapHash());
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, "Monsters", similarityScore.intValue()));
                }
            }
        });
        return;
    }


    public void lookForAvatar(String query, ArrayList<QueryResult> lookResult) {
        // Avatars
        GameData.getAvatarDataMap().forEach((id, data) -> {
            String name = IdLookPlugin.getInstance().getItemTextMap().get(data.getNameTextMapHash());
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, "Avatars", similarityScore.intValue()));
                }
            }
        });
        return;
    }


    public void lookForItem(String query, ArrayList<QueryResult> lookResult) {
        // Item
        GameData.getItemDataMap().forEach((id, data) -> {
            String name = IdLookPlugin.getInstance().getItemTextMap().get(data.getNameTextMapHash());
            if (name != null) {
                Double similarityScore = StringSimilarity.Fuzzy(query, name);
                if (similarityScore > similarityScoreTreshold) {
                    lookResult.add(new QueryResult(id, name, data.getItemTypeString(), similarityScore.intValue()));
                }
            }
        });
        return;
    }
}
