package jinu.nulld.innerEvents;

import jinu.nulld.flow.GameState;
import jinu.nulld.flow.GameStateChangeEvent;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardRegion implements Listener {
    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        String regionName = event.getRegionName();
        if (!GameState.getNowState().equals(GameState.NIGHT)) {
            if (regionName.contains("bank")) {
                String bankType = regionName.replaceFirst("bank", "");

                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ft enterbank " + bankType + " " + event.getPlayer().getName());
            }
        }
    }

    public static Map<String, Integer> roomPeople = new HashMap<>();

    public static boolean lastNight = false;
    public static boolean hackerOn = false;
    @EventHandler
    public void onNIGHT(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.NIGHT)) {
            if (!lastNight) {
                lastNight = true;
                hackerOn = true;
            } else lastNight = false;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (GameState.getNowState().equals(GameState.NIGHT) && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage("§7[§6도둑들§7]§f §c밤에는 채팅이 금지됩니다!");
            event.setCancelled(true);
        }
    }
}
