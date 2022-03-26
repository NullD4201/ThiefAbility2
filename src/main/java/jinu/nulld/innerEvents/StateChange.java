package jinu.nulld.innerEvents;

import jinu.nulld.AbilityCommand;
import jinu.nulld.Main;
import jinu.nulld.database.ability.AbilityDB;
import jinu.nulld.database.ability.AbilityUser;
import jinu.nulld.database.judge.JudgeDB;
import jinu.nulld.database.judge.JudgeData;
import jinu.nulld.database.judge.JudgeUser;
import jinu.nulld.database.vote.VoteDB;
import jinu.nulld.database.vote.VoteData;
import jinu.nulld.database.vote.VoteUser;
import jinu.nulld.flow.GameState;
import jinu.nulld.flow.GameStateChangeEvent;
import jinu.nulld.jobs.JobAPI;
import jinu.nulld.jobs.Jobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jinu.nulld.AbilityCommand.*;
import static jinu.nulld.Main.isCitizenAbilityUsed;

public class StateChange implements Listener {
    public static VoteDB voteDB;
    public static VoteData voteData;
    public static JudgeDB judgeDB;
    public static JudgeData judgeData;
    public static AbilityDB abilityDB = new AbilityDB();
    public static List<UUID> votePlayerList = new ArrayList<>();
    public static List<UUID> judgePlayerList = new ArrayList<>();
    public static Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
    public static Team green = board.getTeam("complete");
    public static Team gray = board.getTeam("wait");

    @EventHandler
    public void startVoteEvent(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.SETTING)) {
            voteCount = 0;
            voteResultCount = 0;
            judgeAgree = 0;
            judgeDisagree = 0;
            judgeCount = 0;
            judgeResultCount = 0;
            voteList = new ArrayList<>();
            judgeList = new ArrayList<>();
            if (abilityDB != null) {
                for (AbilityUser user : abilityDB.getUsers()) {
                    abilityDB.deleteUser(user.getUserId());
                }
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (JobAPI.getJob(player) != null && !JobAPI.getJob(player).equals(Jobs.NONE)) {
                    if (JobAPI.getJob(player).equals(Jobs.CITIZEN)) {
                        abilityDB.insertUser(player.getName() + "↔" + ChatColor.stripColor(player.getDisplayName()),
                                AbilityCommand.playerUUID_to_face(player.getUniqueId()),
                                JobAPI.getJob(player).getJobName(),
                                isCitizenAbilityUsed.getOrDefault("mute", false), !player.getGameMode().equals(GameMode.SPECTATOR));
                    } else {
                        abilityDB.insertUser(player.getName() + "↔" + ChatColor.stripColor(player.getDisplayName()),
                                AbilityCommand.playerUUID_to_face(player.getUniqueId()),
                                JobAPI.getJob(player).getJobName(),
                                false, !player.getGameMode().equals(GameMode.SPECTATOR));
                    }
                    Main.LOGGER.info("[Ability]" + player + " added.");
                    AbilityCommand.isAbilityUsed.put(player.getUniqueId(), false);
                }
            }
        }

        if (event.getNewState().equals(GameState.VOTING)) {
            if (green == null) green = board.registerNewTeam("complete");
            green.setDisplayName("투표 완료");
            green.setColor(ChatColor.GREEN);
            if (gray == null) gray = board.registerNewTeam("wait");
            gray.setDisplayName("투표 미완료");
            gray.setColor(ChatColor.GRAY);
            List<VoteUser> users = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (JobAPI.getJob(player) != null && !JobAPI.getJob(player).equals(Jobs.NONE)) {
                    users.add(new VoteUser(0, 0,
                            player.getName() + "↔" + ChatColor.stripColor(player.getDisplayName()),
                            AbilityCommand.playerUUID_to_face(player.getUniqueId()),
                            JobAPI.getJob(player).getJobName(),
                            !player.getGameMode().equals(GameMode.SPECTATOR),
                            true,
                            0));
                    votePlayerList.add(player.getUniqueId());
                    gray.addEntry(player.getName());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 0, false, false, false));
                    Main.LOGGER.info("[Vote]" + player + " added.");
                }
            }
            voteDB = new VoteDB();
            voteData = voteDB.createVote(users);
        }
        if (event.getNewState().equals(GameState.JUDGE)) {
            List<JudgeUser> users = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (JobAPI.getJob(player) != null && !JobAPI.getJob(player).equals(Jobs.NONE)) {
                    users.add(new JudgeUser(0, 0,
                            player.getUniqueId().toString(),
                            !player.getGameMode().equals(GameMode.SPECTATOR),
                            !player.getName().equals(AbilityCommand.resultName)
                    ));
                    judgePlayerList.add(player.getUniqueId());
                    Main.LOGGER.info("[Judge]" + player + " added.");
                }
            }
            judgeDB = new JudgeDB();
            judgeData = judgeDB.createJudge(users);
        }
    }

    public static void teamChange(Player player, Team prev, Team next) {
        if (player != null) {
            if (prev.getEntries().contains(player.getName())) {
                prev.removeEntry(player.getName());
                next.addEntry(player.getName());
            }
        }
    }
}

