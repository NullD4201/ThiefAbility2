package jinu.nulld;

import jinu.nulld.database.ability.AbilityUser;
import jinu.nulld.innerEvents.StateChange;
import jinu.nulld.innerEvents.WorldGuardRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static jinu.nulld.innerEvents.StateChange.*;

public final class Main extends JavaPlugin implements Listener {
    private MySQL SQL;
    public static final Logger LOGGER = Bukkit.getLogger();
    public static Map<String, Boolean> isCitizenAbilityUsed = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.SQL = new MySQL();
        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.info("Database not connected.");
            e.printStackTrace();
        }
        if (SQL.isConnected()) LOGGER.info("Database Connected.");

        getCommand("thab").setExecutor(new AbilityCommand());
        getCommand("공지").setExecutor(new AbilityCommand());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new StateChange(), this);
        getServer().getPluginManager().registerEvents(new WorldGuardRegion(), this);

        isCitizenAbilityUsed.put("mute", false);
        isCitizenAbilityUsed.put("avoid", false);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SQL.disconnect();
        gray.unregister();
        green.unregister();
        if (abilityDB != null) {
            for (AbilityUser user : abilityDB.getUsers()) abilityDB.deleteUser(user.getUserId());
        }
    }
}
