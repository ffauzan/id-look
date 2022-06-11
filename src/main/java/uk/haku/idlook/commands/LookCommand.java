package uk.haku.idlook.commands;

import static emu.grasscutter.Configuration.*;
import static emu.grasscutter.utils.Language.translate;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.AvatarData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.data.excels.SceneData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.utils.Utils;
import emu.grasscutter.utils.ConfigContainer.Game;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import uk.haku.idlook.PluginTemplate;
import uk.haku.idlook.utils.StringSimilarity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.reflect.TypeToken;


@Command(label = "look", description = "Look command", 
        usage = "look <keywords>", permission = "player.look")
public final class LookCommand implements CommandHandler {
    private Map<Long, String> map;
    Player sender;

    Int2ObjectMap<AvatarData> avatarMap = GameData.getAvatarDataMap();
    Int2ObjectMap<ItemData> itemMap = GameData.getItemDataMap();
    Int2ObjectMap<SceneData> sceneMap = GameData.getSceneDataMap();
    Int2ObjectMap<MonsterData> monsterMap = GameData.getMonsterDataMap();
    
    int resultLimit = 3;
    double similarityScoreTreshold = 0.5;
    TreeMap<Double, Integer> avatarResult = new TreeMap<>();
    TreeMap<Double, Integer> itemResult = new TreeMap<>();
    TreeMap<Double, Integer> monsterResult = new TreeMap<>();


    @Override public void execute(Player sender, Player targetPlayer, List<String> args) {
        this.sender = sender;
        String lookQuery = String.join(" ", args);

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
        
        lookForAvatar(lookQuery);
        lookForItem(lookQuery);
        lookForMonster(lookQuery);

        
        
    }

    public void lookForMonster(String query) {
        // Monster
        monsterResult.clear();
        monsterMap.forEach((id, data) -> {
            Double similarityScore = StringSimilarity.LevenshteinDistance(query, data.getMonsterName());

            if (similarityScore > similarityScoreTreshold) {
                if (monsterResult.size() < resultLimit) {
                    monsterResult.put(similarityScore, id);
                } else {
                    if (monsterResult.firstKey() < similarityScore) {
                        monsterResult.remove(monsterResult.firstKey());
                        monsterResult.put(similarityScore, id);
                    }
                }
            } 
        });

        if (monsterResult.size() == 0) {
            CommandHandler.sendMessage(sender, "Cannot find anything, try different keyword");
        } else {
            CommandHandler.sendMessage(sender, "Result for: " + query + " in Monster");
            monsterResult.forEach((score, itemId) -> {
                String itemName = monsterMap.get(itemId).getMonsterName();
                String responseMsg = "id: " + itemId.toString() + " name: " + itemName;
                CommandHandler.sendMessage(sender, responseMsg);
            });
        }
    }

    public void lookForAvatar(String query) {
        // Avatar
        avatarMap.clear();
        avatarMap.forEach((id, data) -> {
            Double similarityScore = StringSimilarity.LevenshteinDistance(query, data.getName());

            if (similarityScore > similarityScoreTreshold) {
                if (avatarResult.size() < resultLimit) {
                    avatarResult.put(similarityScore, id);
                } else {
                    if (avatarResult.firstKey() < similarityScore) {
                        avatarResult.remove(avatarResult.firstKey());
                        avatarResult.put(similarityScore, id);
                    }
                }
            } 
        });

        if (avatarResult.size() == 0) {
            CommandHandler.sendMessage(sender, "Cannot find anything, try different keyword");
        } else {
            CommandHandler.sendMessage(sender, "Result for: " + query + " in Character");
            avatarResult.forEach((score, itemId) -> {
                String itemName = avatarMap.get(itemId).getName();
                String responseMsg = "id: " + itemId.toString() + " name: " + itemName;
                CommandHandler.sendMessage(sender, responseMsg);
            });
        }
    }


    public void lookForItem(String query) {
        // Item
        itemMap.clear();
        itemMap.forEach((id, data) -> {
            Double similarityScore = StringSimilarity.LevenshteinDistance(query, map.get(data.getNameTextMapHash()));

            if (similarityScore > similarityScoreTreshold) {
                if (itemResult.size() < resultLimit) {
                    itemResult.put(similarityScore, id);
                } else {
                    if (itemResult.firstKey() < similarityScore) {
                        itemResult.remove(itemResult.firstKey());
                        itemResult.put(similarityScore, id);
                    }
                }
            } 
        });

        if (itemResult.size() == 0) {
            CommandHandler.sendMessage(sender, "Cannot find anything, try different keyword");
        } else {
            CommandHandler.sendMessage(sender, "Result for: " + query + " in Item");
            itemResult.forEach((score, itemId) -> {
                String itemName = map.get(itemMap.get(itemId).getNameTextMapHash());
                String responseMsg = "id: " + itemId.toString() + " name: " + itemName;
                CommandHandler.sendMessage(sender, responseMsg);
            });
        }
    }
}
