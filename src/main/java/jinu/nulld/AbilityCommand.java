package jinu.nulld;

import jinu.nulld.database.ability.AbilityUser;
import jinu.nulld.database.judge.JudgeUser;
import jinu.nulld.database.vote.VoteUser;
import jinu.nulld.flow.GameState;
import jinu.nulld.flow.JudgeEndEvent;
import jinu.nulld.flow.VoteEndEvent;
import jinu.nulld.innerEvents.StateChange;
import jinu.nulld.jobs.IsThief;
import jinu.nulld.jobs.JobAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import jinu.nulld.jobs.Jobs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import su.plo.voice.PlasmoVoice;

import java.util.*;
import java.util.List;

import static jinu.nulld.Main.*;
import static jinu.nulld.innerEvents.StateChange.*;
import static jinu.nulld.innerEvents.WorldGuardRegion.*;

public class AbilityCommand implements TabExecutor {
    public static Map<UUID, Jobs> jobsMap = Jobs.jobMap;

    public static boolean isNumeric(String string) {
        if (string == null) return false;
        try {
            double d = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static Map<String, UUID> face_to_playerUUID = new HashMap<>();
    public static String playerUUID_to_face(UUID uuid) {
        String toReturn = null;
        for (String string : face_to_playerUUID.keySet()) {
            if (face_to_playerUUID.get(string).equals(uuid)) {
                toReturn = string;
                break;
            }
        }
        return toReturn;
    }
    public static String playerUUID_to_face(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return playerUUID_to_face(uuid);
    }

    public static int voteCount = 0;
    public static  int voteResultCount = 0;
    private static Map<String, Integer> voteResultMap = new HashMap<>();
    public static  int judgeAgree = 0;
    public static  int judgeDisagree = 0;
    public static  int judgeCount = 0;
    public static  int judgeResultCount = 0;
    public static String resultName = "null";
    public static List<UUID> voteList = new ArrayList<>();
    public static List<UUID> judgeList = new ArrayList<>();
    private VoteEndEvent endEvent;
    private List<String> voteResultList = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("??????") && player.isOp()) {
            StringBuilder string = new StringBuilder();
            for (String s : args) string.append(" ").append(s);

            for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage("??7[??6???????????7]??d" + string);
        }

        // thab command
        if (label.equalsIgnoreCase("thab")) {
            if (player.isOp()) {
                if (args[0].equalsIgnoreCase("debugvote")) {
                    GameState.setGameState(GameState.VOTING);
                    Main.LOGGER.info("GameState changed to VOTING.");
                }
                if (args[0].equalsIgnoreCase("debugjudge")) {
                    GameState.setGameState(GameState.JUDGE);
                    Main.LOGGER.info("GameState changed to JUDGE.");
                }
                if (args[0].equalsIgnoreCase("debugnight")) {
                    GameState.setGameState(GameState.NIGHT);
                    Main.LOGGER.info("GameState changed to NIGHT.");
                }
                if (args[0].equalsIgnoreCase("debugdiscuss")) {
                    GameState.setGameState(GameState.DISCUSS);
                    Main.LOGGER.info("GameState changed to DISCUSS.");
                }
                if (args[0].equalsIgnoreCase("debugsetting")) {
                    GameState.setGameState(GameState.SETTING);
                    Main.LOGGER.info("GameState changed to SETTING.");
                }
            }
            if (args[0].equalsIgnoreCase("vote")) {
                if (!GameState.getNowState().equals(GameState.VOTING)) {
                    player.sendMessage("??c??????????????? ????????????.");
                    return false;
                }
                int plus = 1;
                if (JobAPI.getJob(player).equals(Jobs.CLERK)) plus = 2;
                List<VoteUser> voteUsers = new ArrayList<>(voteDB.getVoteByID(voteData.getVoteId()).getVoteUsers().values());
                VoteUser playerUser = null;
                for (VoteUser user : voteUsers) {
                    if (user.getDisplayName().split("???")[0].equals(ChatColor.stripColor(player.getName()))) playerUser = user;
                }

                int mapResult = voteResultMap.getOrDefault(args[1], 0);
                if (args[1].equalsIgnoreCase("skip") && !voteList.contains(player.getUniqueId())) {
                    voteDB.updateVote(voteData.getVoteId(), voteData.isResult(), mapResult + plus);
                }
                if (args[1].contains("face")) {
                    VoteUser objectUser = null;
                    for (VoteUser user : voteUsers) {
                        if (user.getFaceId().equals(args[1])) {
                            objectUser = user;
                            break;
                        }
                    }

                    if (objectUser == null) {
                        Main.LOGGER.info("[Vote] Invalid vote object");
                        return false;
                    }
                    if (playerUser == null) {
                        Main.LOGGER.info("[Vote] Invalid vote player");
                        return false;
                    }

                    voteDB.updateUser(objectUser.getUserId(), new VoteUser(
                            objectUser.getUserId(),
                            objectUser.getVoteId(),
                            objectUser.getDisplayName(),
                            objectUser.getFaceId(),
                            objectUser.getJob(),
                            objectUser.isValid(),
                            !voteList.contains(face_to_playerUUID.get(args[1])),
                            mapResult + plus
                    ));
                }
                voteResultMap.put(args[1], mapResult + plus);

                Main.LOGGER.info(player.getName() + " voted \"" + args[1] + "\"");

                voteCount++;

                voteList.add(player.getUniqueId());
                voteDB.updateUser(playerUser.getUserId(), new VoteUser(
                        playerUser.getUserId(),
                        playerUser.getVoteId(),
                        playerUser.getDisplayName(),
                        playerUser.getFaceId(),
                        playerUser.getJob(),
                        playerUser.isValid(),
                        false,
                        voteResultMap.getOrDefault(playerUUID_to_face(player.getUniqueId().toString()), 0)
                ));
                teamChange(player, gray, green);

                if (voteCount == StateChange.votePlayerList.size()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("??a?????? ??????", "??f?????? ????????? ??????????????????...", 15, 30, 15);
                    }
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendTitle("??a?????? ??????", "??f????????? ???????????????.", 5, 30, 5);
                                teamChange(p, green, gray);
                            }
                            voteDB.updateVote(voteData.getVoteId(), true, voteResultMap.getOrDefault("skip", 0));
                        }
                    }.runTaskLater(Main.getPlugin(Main.class), 60);
                }
            }
            if (args[0].equalsIgnoreCase("resultvote")) {
                if (!GameState.getNowState().equals(GameState.VOTING)) {
                    player.sendMessage("??c??????????????? ????????????.");
                    return false;
                }
                voteResultCount++;
                Main.LOGGER.info(player.getName() + " checked \"voteResult\"");
                teamChange(player, gray, green);

                if (voteResultCount == StateChange.votePlayerList.size()) {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            voteResultList = new ArrayList<>();
                            List<String> _result_vote = new ArrayList<>(voteResultMap.keySet());
                            _result_vote.sort((r1, r2) -> voteResultMap.get(r2).compareTo(voteResultMap.get(r1)));

                            int _tmp_vote = voteResultMap.get(_result_vote.get(0));
                            voteResultList.add(_result_vote.get(0));

                            for (int i = 1; i < _result_vote.size(); i++) {
                                if (voteResultMap.get(_result_vote.get(i)) > _tmp_vote) {
                                    voteResultList = new ArrayList<>();
                                    voteResultList.add(_result_vote.get(i));
                                    _tmp_vote = voteResultMap.get(_result_vote.get(i));
                                } else if (voteResultMap.get(_result_vote.get(i)) == _tmp_vote) {
                                    voteResultList.add(_result_vote.get(i));
                                }
                            }

                            if (voteResultList.contains("skip")) voteResultList = Collections.singletonList("skip");
                            else {
                                List<String> result = new ArrayList<>();
                                for (String faces : voteResultList) {
                                    result.add(face_to_playerUUID.get(faces).toString());
                                }

                                voteResultList = result;
                            }

                            endEvent = new VoteEndEvent();
                            endEvent.setResult(voteResultList);
                            boolean _t = false;
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (JobAPI.getJob(p).equals(Jobs.CITIZEN)) {
                                    _t = true;
                                    break;
                                }
                            }
                            if (voteResultList.size() == 1 && !voteResultList.contains("skip")) {
                                resultName = Bukkit.getPlayer(UUID.fromString(voteResultList.get(0))).getName();
                                if (_t) {
                                    if (JobAPI.getJob(UUID.fromString(voteResultList.get(0))).equals(Jobs.CITIZEN) && !isCitizenAbilityUsed.get("avoid")) {
                                        TextComponent yesorno = new TextComponent("??f???????????? ???????????????. ?????? ????????? ?????????????????????????   ");
                                        TextComponent blank = new TextComponent("   ");
                                        TextComponent yes = new TextComponent("??a[???]");
                                        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thab avoidVote " + resultName + " agree"));
                                        yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??a???")));
                                        TextComponent no = new TextComponent("??c[?????????]");
                                        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thab avoidVote " + resultName + " disagree"));
                                        no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??c?????????")));
                                        yesorno.addExtra(yes);
                                        yesorno.addExtra(blank);
                                        yesorno.addExtra(no);
                                        Bukkit.getPlayer(UUID.fromString(voteResultList.get(0))).spigot().sendMessage(yesorno);
                                    } else {
                                        if (isCitizenAbilityUsed.get("avoid") && JobAPI.getJob(UUID.fromString(voteResultList.get(0))).equals(Jobs.CITIZEN)) {
                                            Bukkit.getPlayer(UUID.fromString(voteResultList.get(0))).sendMessage("??c???????????? ????????????, ???????????? ?????? ???????????? ????????? ??? ????????????.");
                                        }
                                        player.performCommand("thab avoidVote " + player.getName() + " disagree");
                                    }
                                } else {
                                    player.performCommand("thab avoidVote " + player.getName() + " disagree");
                                }
                            }

                            voteDB.deleteVote(voteData.getVoteId());
                            voteResultMap = new HashMap<>();

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                teamChange(p, green, gray);
                            }
                        }
                    }.runTaskLater(Main.getPlugin(Main.class), 60);
                }
            }
            if (args[0].equalsIgnoreCase("avoidVote")) {
                String resultName = args[1];
                String _go = args[2];

                if (resultName.equalsIgnoreCase(player.getName())) {
                    if (_go.equalsIgnoreCase("agree")) {
                        endEvent.setResult(Collections.singletonList("skip"));
                        Bukkit.broadcastMessage("??e?????? ????????a??? ????????? ???????????? ????????? ??????????????????.");
                        isCitizenAbilityUsed.put("avoid", true);
                    } else if (_go.equalsIgnoreCase("disagree")) {
                        endEvent.setResult(voteResultList);
                    }
                    Bukkit.getPluginManager().callEvent(endEvent);
                    Main.LOGGER.info(voteResultList.toString());
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("judge")) {
                if (!GameState.getNowState().equals(GameState.JUDGE)) {
                    player.sendMessage("??c??????????????? ????????????.");
                    return false;
                }
                if (judgeList.contains(player.getUniqueId())) {
                    player.sendTitle("", "??c?????? ?????? ??????????????????.", 15, 30, 15);
                    return false;
                }

                List<JudgeUser> judgeUsers = new ArrayList<>(judgeDB.getJudgeByID(judgeData.getJudgeId()).getJudgeUsers().values());
                JudgeUser playerUser = null;
                for (JudgeUser user : judgeUsers) {
                    if (user.getPlayerUUID().equals(player.getUniqueId().toString())) playerUser = user;
                }
                int plus = 1;
                if (JobAPI.getJob(player).equals(Jobs.CLERK)) plus = 2;
                if (args[1].equalsIgnoreCase("agree")) {
                    judgeAgree += plus;
                }
                if (args[1].equalsIgnoreCase("disagree")) {
                    judgeDisagree += plus;
                }

                judgeDB.updateJudge(judgeData.getJudgeId(), judgeAgree, judgeDisagree, false);

                judgeCount++;
                judgeDB.updateUser(playerUser.getUserId(), new JudgeUser(
                        playerUser.getUserId(),
                        playerUser.getJudgeId(),
                        playerUser.getPlayerUUID(),
                        playerUser.isValid(),
                        false
                ));
                teamChange(player, gray, green);

                if (judgeCount == (judgePlayerList.size()-1)) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("??a?????? ??????", "??f?????? ????????? ??????????????????...", 15, 30, 15);
                    }
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.sendTitle("??a?????? ??????", "??f????????? ???????????????.", 5, 30, 5);
                                teamChange(p, green, gray);
                            }
                            judgeDB.updateJudge(judgeData.getJudgeId(), judgeAgree, judgeDisagree, true);
                        }
                    }.runTaskLater(Main.getPlugin(Main.class), 60);
                }
            }
            if (args[0].equalsIgnoreCase("resultjudge")) {
                if (!GameState.getNowState().equals(GameState.JUDGE)) {
                    player.sendMessage("??c??????????????? ????????????.");
                    return false;
                }
                judgeResultCount++;
                Main.LOGGER.info(player.getName() + " checked \"judgeResult\"");
                teamChange(player, gray, green);

                if (judgeResultCount == StateChange.judgePlayerList.size()) {
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            if (judgeAgree > judgeDisagree) Bukkit.getPluginManager().callEvent(new JudgeEndEvent(resultName));
                            else Bukkit.getPluginManager().callEvent(new JudgeEndEvent(null));
                            Main.LOGGER.info(resultName);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                teamChange(p, green, gray);
                                p.removePotionEffect(PotionEffectType.GLOWING);
                            }

                            judgeDB.deleteJudge(judgeData.getJudgeId());
                        }
                    }.runTaskLater(Main.getPlugin(Main.class), 60);
                }
            }
            if (args[0].equalsIgnoreCase("setface") && args.length == 3) {
                String playerName = args[1];
                String num = args[2];
                if (!isNumeric(num)) return false;

                Player toSet = Bukkit.getPlayer(playerName);
                if (toSet == null) return false;

                int number = Integer.parseInt(num);
                if (number < 1 || number > 8) return false;
                String face = "face"+num;

                face_to_playerUUID.put(face, toSet.getUniqueId());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOp()) p.sendMessage("Set player "+toSet.getName()+" to "+face);
                }
                roomPeople.put(face, 0);
            } else if (args[0].equalsIgnoreCase("use") && args.length >= 2) {
                if (jobsMap.get(player.getUniqueId()) != null) {
//                    if (jobsMap.get(player.getUniqueId()).equals(Jobs.MECHANIC)) {
//                        if (StateChange.lastNight == 0) {
//                            player.sendMessage("??c??? ????????? ???????????? ??????????????? ????????? ????????? ??? ????????????.");
//                            return false;
//                        } else {
//                            UUID uuid = face_to_playerUUID.get(args[1]);
//                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tn " + player.getName() + " " + Bukkit.getPlayer(uuid).getName());
//                        }
//                    }
                    AbilityUser playerUser = null;
                    for (AbilityUser user : abilityDB.getUsers()) {
                        if (user.getDisplayName().split("???")[0].equals(ChatColor.stripColor(player.getName()))) playerUser = user;
                    }
                    if (playerUser != null && playerUser.abilityUse()) {
                        player.sendMessage("??c?????? ?????????????????? ?????? ????????? ??????????????????.");
                        return false;
                    }
                    if (jobsMap.get(player.getUniqueId()).equals(Jobs.ACCOUNTING)) {
                        UUID uuid = face_to_playerUUID.get(args[1]);
                        double pp = Math.random() * 100;

                        if (pp < 50) {
                            player.sendTitle(" ", "??f?????? ????????? ??a????????f????????????.", 5, 30, 5);
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    if (IsThief.booleanThief(uuid)) {
                                        player.sendTitle("??e" + Bukkit.getPlayer(uuid).getDisplayName() + "??a??????", "??f????????? ??a????????????.", 20, 40, 20);
                                    } else {
                                        player.sendTitle("??e" + Bukkit.getPlayer(uuid).getDisplayName() + "??a??????", "??f????????? ??c????????????.", 20, 40, 20);
                                    }
                                }
                            }.runTaskLater(ThiefGame.getPlugin(ThiefGame.class), 40);
                        } else {
                            player.sendTitle(" ", "??f?????? ????????? ??c????????f????????????.", 5, 30, 5);
                            return false;
                        }
                    }
                    if (jobsMap.get(player.getUniqueId()).equals(Jobs.SECURITY)) {
                        UUID uuid = face_to_playerUUID.get(args[1]);
                        List<Jobs> _list_ = new ArrayList<>(Arrays.asList(Jobs.values()));
                        String _real = jobsMap.get(uuid).getJobName();
                        _list_.remove(jobsMap.get(uuid));
                        String _fake = _list_.get((int) (Math.random() * 7)).getJobName();

                        player.sendMessage("??e" + Bukkit.getPlayer(uuid).getDisplayName() + "??a?????? ?????????\n" + "??b" + _real + " ??f?????? ??b" + _fake + " ??f?????????.");
                    }
                    if (jobsMap.get(player.getUniqueId()).equals(Jobs.CITIZEN)) {
                        if (!isCitizenAbilityUsed.getOrDefault("mute", false)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vmute " + Bukkit.getPlayer(face_to_playerUUID.get(args[1])).getName());
                            player.sendMessage("??7[??6???????????7]??f ??e" + Bukkit.getPlayer(face_to_playerUUID.get(args[1])).getDisplayName() + "??a?????? ?????????????????????.");
                            player.sendMessage("??7[??6???????????7]??f ??c????????? ???????????? ???????????? ??? ????????????.");
                            isCitizenAbilityUsed.put("mute", true);
                            Bukkit.getPlayer(face_to_playerUUID.get(args[1])).sendTitle("", "??b?????? ????????c??? ???????????? ?????? ????????? ????????? ????????? ???????????????.", 5, 30, 5);
                        }
                    }
                    if (jobsMap.get(player.getUniqueId()).equals(Jobs.ARCHITECT)) {
                        if (GameState.getNowState().equals(GameState.SETTING)) player.performCommand("ft setbank " + args[1]);
                        else player.sendTitle("", "??c?????? ??????????????? ????????????.", 5, 30, 5);
                    }
                    isAbilityUsed.put(player.getUniqueId(), true);
                    abilityDB.updateUser(playerUser.getUserId(), new AbilityUser(
                            playerUser.getUserId(),
                            playerUser.getDisplayName(),
                            playerUser.getFaceId(),
                            playerUser.getJob(),
                            true,
                            playerUser.isValid()
                    ));
                }
            } else if (args[0].equalsIgnoreCase("check")) {
                if (jobsMap.get(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "??7[??6???????????7]??f &b&l?????? : &e&l"+jobsMap.get(player.getUniqueId()).getJobName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "??7[??6???????????7]??f &b&l?????? : &e&l"+jobsMap.get(player.getUniqueId()).getJobDescription()));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                help(sender);
//            } else if (args[0].equalsIgnoreCase("ask")) {
//                if (!GameState.getNowState().equals(GameState.NIGHT)) {
//                    player.sendMessage("??c?????? ????????????.");
//                    return false;
//                }
//                Player object = Bukkit.getPlayer(face_to_playerUUID.get(args[1])); // ??????????????????
//
//                object.sendMessage("??7[??6???????????7]??f ??e" + player.getDisplayName() + "??f???????????? ?????? ????????? ??????????????????.");
//                if (roomPeople.get(args[1]) <= 2) {
//                    TextComponent yesorno = new TextComponent("??f?????????????????????????   ");
//                    TextComponent blank = new TextComponent("   ");
//                    TextComponent yes = new TextComponent("??a[???]");
//                    yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thab accept " + playerUUID_to_face(player.getUniqueId())));
//                    yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??a??????")));
//                    TextComponent no = new TextComponent("??c[x]");
//                    no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thab deny " + playerUUID_to_face(player.getUniqueId())));
//                    no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("??c??????")));
//                    yesorno.addExtra(yes);
//                    yesorno.addExtra(blank);
//                    yesorno.addExtra(no);
//                    object.spigot().sendMessage(yesorno);
//                }
//            } else if (args[0].equalsIgnoreCase("accept")) {
//                if (!GameState.getNowState().equals(GameState.NIGHT)) {
//                    player.sendMessage("??c?????? ????????????.");
//                    return false;
//                }
//                Player object = Bukkit.getPlayer(face_to_playerUUID.get(args[1])); // ??????????????????
//                if (roomPeople.get(args[1]) > 1) {
//                    player.sendMessage("??7[??6???????????7]??f ??c?????? ?????? ??? ?????? ????????? ??? ????????????.");
//                    return false;
//                }
//
//                player.sendMessage("??7[??6???????????7]??f ??e" + object.getDisplayName() + "??f?????? ?????? ????????? ??a????????f????????????.");
//                object.sendMessage("??7[??6???????????7]??f ??e" + player.getDisplayName() + "??f?????? ?????? ????????? ??a????????f????????????.");
//
//                object.teleport(player.getLocation());
//            } else if (args[0].equalsIgnoreCase("deny")) {
//                if (!GameState.getNowState().equals(GameState.NIGHT)) {
//                    player.sendMessage("??c?????? ????????????.");
//                    return false;
//                }
//                Player object = Bukkit.getPlayer(face_to_playerUUID.get(args[1])); // ??????????????????
//                if (roomPeople.get(args[1]) > 1) {
//                    return false;
//                }
//
//                player.sendMessage("??7[??6???????????7]??f ??e" + object.getDisplayName() + "??f?????? ?????? ????????? ??c????????f????????????.");
//                object.sendMessage("??7[??6???????????7]??f ??e" + player.getDisplayName() + "??f?????? ?????? ????????? ??c????????f????????????.");
//            }
            }
        }

        return false;
    }
    private void help(CommandSender sender) {
        sender.sendMessage("??6---------- ??7[ ??6????????? ??7] ??6----------");
        sender.sendMessage("??f - ?????? : ??e" + JobAPI.getJob((Player) sender).getJobName());
        sender.sendMessage("??f - ?????? : ??b" + JobAPI.getJob((Player) sender).getJobDescription());
    }
    public static Map<UUID, Boolean> isAbilityUsed = new HashMap<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("thab")) {
            if (args.length == 1) return StringUtil.copyPartialMatches(args[0], Arrays.asList("setface", "use", "check", "vote", "resultvote", "judge", "resultjudge", "help", "ask"), new ArrayList<>());
            if (args.length == 2 && args[1].equalsIgnoreCase("setface")) {
                List<String> pList = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) pList.add(player.getName());
                return StringUtil.copyPartialMatches(args[1], pList, new ArrayList<>());
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("setface")) return StringUtil.copyPartialMatches(args[2], Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"), new ArrayList<>());
        } else return new ArrayList<>();
        return null;
    }
}