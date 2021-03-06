package jinu.nulld.database.judge;

import jinu.nulld.MySQL;
import static jinu.nulld.Main.LOGGER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JudgeDB {
    private final MySQL db;

    public JudgeDB() {
        this.db = new MySQL();
    }

    public JudgeData getJudgeByID(int judgeId) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from JudgeSet where judgeId=?");

            stmt.setInt(1, judgeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int judgeID = rs.getInt(1);
                int agree = rs.getInt(2);
                int disagree = rs.getInt(3);
                boolean isResult = rs.getBoolean(4);
                PreparedStatement stmt2 = conn.prepareStatement("select * from JudgeUsers where judgeId=?");
                stmt2.setInt(1, judgeId);
                ResultSet rs2 = stmt2.executeQuery();
                Map<Integer, JudgeUser> users = new HashMap<>();
                while (rs2.next()) {
                    users.put(
                            rs2.getInt(1),
                            new JudgeUser(
                                    rs2.getInt(1),
                                    rs2.getInt(2),
                                    rs2.getString(3),
                                    rs2.getBoolean(4),
                                    rs2.getBoolean(5)
                            )
                    );
                }

                rs.close();
                stmt.close();
                this.db.disconnect();
                return new JudgeData(judgeID, agree, disagree, isResult, users);
            } else {
                LOGGER.warning("[Judge] Judge is not found");
                rs.close();
                stmt.close();
                this.db.disconnect();
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JudgeData getFirstJudge() {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from JudgeSet order by judgeId desc limit 1");
            ResultSet rs = stmt.executeQuery();
//            LOGGER.info(rs);
//            LOGGER.info("FV : "+rs.next());
            if (rs.next()) {
                int judgeID = rs.getInt(1);
                int agree = rs.getInt(2);
                int disagree = rs.getInt(3);
                boolean isResult = rs.getBoolean(4);
                rs.close();
                stmt.close();
                PreparedStatement stmt2 = conn.prepareStatement("select * from JudgeUsers where judgeId=?");
                stmt2.setInt(1, judgeID);
                ResultSet rs2 = stmt2.executeQuery();
                Map<Integer, JudgeUser> users = new HashMap<>();
                int i = 1;
                while (rs2.next()) {
                    users.put(
                            i++,
                            new JudgeUser(
                                    rs2.getInt(1),
                                    rs2.getInt(2),
                                    rs2.getString(3),
                                    rs2.getBoolean(4),
                                    rs2.getBoolean(5)
                            )
                    );
//                    i++;
                    LOGGER.info("Current User Id : "+rs2.getInt(1));
                }
                rs2.close();
                stmt2.close();
                LOGGER.info("Statement Finish");
                this.db.disconnect();
                LOGGER.info("Connection closed");
                return new JudgeData(judgeID, agree, disagree, isResult, users);
            } else {
                LOGGER.warning("[Vote] Vote is not found");
                rs.close();
                stmt.close();
                this.db.disconnect();
                return null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JudgeData createJudge(List<JudgeUser> userList) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into JudgeSet(agree, disagree, isResult) values(0, 0, 0)");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("select * from JudgeSet where isResult=0 order by judgeId desc limit 1");
            ResultSet rs = stmt.executeQuery();
            int judgeId = 0;
            Map<Integer, JudgeUser> userMap = new HashMap<>();
            if (rs.next()) {
                judgeId = rs.getInt(1);
                for (JudgeUser user : userList) {
                    PreparedStatement stmt2 = conn.prepareStatement("insert into JudgeUsers(judgeId, playerUUID, isValid, canJudge) values(?, ?, ?, ?)");
                    stmt2.setInt(1, judgeId);
                    stmt2.setString(2, user.getPlayerUUID());
                    stmt2.setBoolean(3, user.isValid());
                    stmt2.setBoolean(4, user.canJudge());
                    stmt2.executeUpdate();
                    stmt2.close();
                    userMap.put(user.getUserId(), user);
                }
            } else {
                rs.close();
                stmt.close();
                LOGGER.warning("[Judge] Judge is not found");
                return null;
            }
            rs.close();
            stmt.close();
            // "INSERT INTO Reservation(ID, Name, ReserveDate, RoomNum) VALUES(5, '?????????', '2016-02-16', 1108);"

            this.db.disconnect();
            return new JudgeData(judgeId, 0, 0, false, userMap);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateJudge(int judgeId, int agree, int disagree,  boolean result) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update JudgeSet set agree=?, disagree=?, isResult=? where judgeId=?");
            stmt.setInt(1, agree);
            stmt.setInt(2, disagree);
            stmt.setBoolean(3, result);
            stmt.setInt(4, judgeId);
            stmt.executeUpdate();
            stmt.close();
            this.db.disconnect();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(int userId, JudgeUser user) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update JudgeUsers set judgeId=?, playerUUID=?, isValid=?, canJudge=? where userId=?");

            stmt.setInt(1, user.getJudgeId());
            stmt.setString(2, user.getPlayerUUID());
            stmt.setBoolean(3, user.isValid());
            stmt.setBoolean(4, user.canJudge());
            stmt.setInt(5, userId);

            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteJudge(int judgeId) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from JudgeUsers where judgeId=?");
            stmt.setInt(1, judgeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PreparedStatement stmt2 = conn.prepareStatement("delete from JudgeUsers where userId=?");
                stmt2.setInt(1, rs.getInt(1));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs.close();
            stmt.close();
            stmt = conn.prepareStatement("delete from JudgeSet where judgeId=?");
            stmt.setInt(1, judgeId);
            stmt.executeUpdate();

            // ?????? ????????? ????????????, ?????????????????? ?????? ???????????? ???????????????
            // insert, update, delete ????????? executeUpdate???,
            // select ????????? executeQuery??? ???????????????~
            // executeUpdate??? ????????? ?????? ????????? ?????????, executeQuery??? ????????? ????????? ResultSet??? ???????????????

            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
