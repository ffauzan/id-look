package uk.haku.idlook.commands;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static emu.grasscutter.Configuration.*;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.AvatarData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import com.google.common.reflect.TypeToken;

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

    private Map<Long, String> map;
    private Int2ObjectMap<AvatarData> avatarMap = GameData.getAvatarDataMap();
    private Int2ObjectMap<ItemData> itemMap = GameData.getItemDataMap();
    private Int2ObjectMap<MonsterData> monsterMap = GameData.getMonsterDataMap();
    

    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        String lookQuery = String.join(" ", args);
        ArrayList<QueryResult> resultList = new ArrayList<QueryResult>();

        final String textMapFile = "TextMap/TextMap" + DOCUMENT_LANGUAGE + ".json";
        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(
                Utils.toFilePath(RESOURCE(textMapFile))), StandardCharsets.UTF_8)) {
            map = Grasscutter.getGsonFactory()
                    .fromJson(fileReader, new TypeToken<Map<Long, String>>() {
                    }.getType());
        } catch (IOException e) {
            Grasscutter.getLogger().warn("Resource does not exist: " + textMapFile);
            map = new HashMap<>();
        }
        
        lookFor(lookQuery, resultList);

        Collections.sort(resultList);

        sendResult(sender, resultList);
    }


    public void sendResult(Player player, List<QueryResult> lookResult) {
        if (lookResult.size() == 0) {
            CommandHandler.sendMessage(player, "Cannot find anything, try different keyword");
            return;
        } else if (lookResult.size() > resultLimit) {
            lookResult = lookResult.subList(0, resultLimit);
        }

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
        monsterMap.forEach((id, data) -> {
            String name = map.get(data.getNameTextMapHash());
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
        avatarMap.forEach((id, data) -> {
            String name = map.get(data.getNameTextMapHash());
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
        itemMap.forEach((id, data) -> {
            String name = map.get(data.getNameTextMapHash());
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
