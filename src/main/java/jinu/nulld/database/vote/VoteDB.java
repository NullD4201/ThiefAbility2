package jinu.nulld.database.vote;

import jinu.nulld.MySQL;
import static jinu.nulld.Main.LOGGER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteDB {

    private final MySQL db;

    public VoteDB() {
        this.db = new MySQL();
    }

    /**
     * VoteData 객체를 구해옵니다.
     * @param voteid    투표의 ID를 받습니다.
     * @return  VoteData 객체를 반환합니다.
     */
    public VoteData getVoteByID(Integer voteid) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteSet where voteid=?");

            stmt.setInt(1, voteid);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer voteId = rs.getInt(1);
                boolean isResult = rs.getBoolean(2);
                Integer skipVotes = rs.getInt(3);
                PreparedStatement stmt2 = conn.prepareStatement("select * from VoteUsers where voteid=?");
                stmt2.setInt(1, voteid);
                ResultSet rs2 = stmt2.executeQuery();
                Map<Integer, VoteUser> users = new HashMap<>();
                while (rs2.next()) {
                    users.put(
                            rs2.getInt(1),
                            new VoteUser(
                                    rs2.getInt(1),
                                    rs2.getInt(2),
                                    rs2.getString(3),
                                    rs2.getString(4),
                                    rs2.getString(5),
                                    rs2.getBoolean(6),
                                    rs2.getBoolean(7),
                                    rs2.getInt(8)
                            )
                    );
                }
                rs2.close();
                stmt2.close();
                rs.close();
                stmt.close();
                this.db.disconnect();
                return new VoteData(voteId, skipVotes, isResult, users);
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
//    public VoteData getVoteByID(Integer voteid) {
//        try {
//            if (!this.db.isConnected()) this.db.connect();
//            Connection conn = this.db.getConnection();
//            PreparedStatement stmt = conn.prepareStatement("select * from VoteSet where voteid=?");
//
//            stmt.setInt(1, voteid);
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                Integer voteId = rs.getInt(1);
//                boolean isResult = rs.getBoolean(2);
//                Integer skipVotes = rs.getInt(3);
//                PreparedStatement stmt2 = conn.prepareStatement("select * from VoteUsers where voteid=?");
//                stmt2.setInt(1, voteid);
//                ResultSet rs2 = stmt2.executeQuery();
//                Map<Integer, VoteUser> users = new HashMap<>();
//                while (rs2.next()) {
//                    users.put(
//                            rs2.getInt(1),
//                            new VoteUser(
//                                    rs2.getInt(1),
//                                    rs2.getInt(2),
//                                    rs2.getString(3),
//                                    rs2.getString(4),
//                                    rs2.getString(5),
//                                    rs2.getBoolean(6),
//                                    rs2.getBoolean(7),
//                                    rs2.getInt(8)
//                            )
//                    );
//                }
//                rs2.close();
//                stmt2.close();
//                rs.close();
//                stmt.close();
//                this.db.disconnect();
//                return new VoteData(voteId, skipVotes, isResult, users);
//            } else {
//                LOGGER.warn("[Vote] Vote is not found");
//                rs.close();
//                stmt.close();
//                this.db.disconnect();
//                return null;
//            }
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * VoteUser 객체를 구해옵니다.
     * @param userID    User의 ID를 받습니다.
     * @return  VoteUser 객체를 반환합니다.
     */
    public VoteUser getUserByID(Integer userID) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteUsers where userid=?");

            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                VoteUser user = new VoteUser(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getBoolean(6),
                        rs.getBoolean(7),
                        rs.getInt(8)
                );
                rs.close();
                stmt.close();
                this.db.disconnect();
                return user;
            } else {
                LOGGER.warning("[Vote] User is not found");
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
    public VoteData getFirstVote() {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteSet order by voteid desc limit 1");
            ResultSet rs = stmt.executeQuery();
//            LOGGER.info(rs);
//            LOGGER.info("FV : "+rs.next());
            if (rs.next()) {
                int voteId = rs.getInt(1);
                boolean isResult = rs.getBoolean(2);
                int skipVotes = rs.getInt(3);
                rs.close();
                stmt.close();
                LOGGER.info("voteID: "+voteId+", isResult: "+isResult+", skipVotes: "+skipVotes);
                PreparedStatement stmt2 = conn.prepareStatement("select * from VoteUsers where voteid=? order by faceid asc");
                stmt2.setInt(1, voteId);
                ResultSet rs2 = stmt2.executeQuery();
                Map<Integer, VoteUser> users = new HashMap<>();
                int i = 1;
                while (rs2.next()) {
                    users.put(
                            i++,
                            new VoteUser(
                                    rs2.getInt(1),
                                    rs2.getInt(2),
                                    rs2.getString(3),
                                    rs2.getString(4),
                                    rs2.getString(5),
                                    rs2.getBoolean(6),
                                    rs2.getBoolean(7),
                                    rs2.getInt(8)
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
                return new VoteData(voteId, skipVotes, isResult, users);
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


    /**
     * 투표를 생성합니다.
     * @param userList 등록할 유저의 리스트
     * @return 생성된 VoteData 객체
     */
    public VoteData createVote(List<VoteUser> userList) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into VoteSet(result, skipvotes) values(0, 0)");
            stmt.executeUpdate();
            stmt.close();
            stmt = conn.prepareStatement("select * from VoteSet where result=0 order by voteid desc limit 1");
            ResultSet rs = stmt.executeQuery();
            int voteId = 0;
            Map<Integer, VoteUser> userMap = new HashMap<>();
            if (rs.next()) {
                voteId = rs.getInt(1);
                for (VoteUser user : userList) {
                    PreparedStatement stmt2 = conn.prepareStatement("insert into VoteUsers(voteid, displayName, faceid, job, isValid, canVote, voteResult) values(?, ?, ?, ?, ?, ?, ?)");
                    stmt2.setInt(1, voteId);
                    stmt2.setString(2, user.getDisplayName());
                    stmt2.setString(3, user.getFaceId());
                    stmt2.setString(4, user.getJob());
                    stmt2.setBoolean(5, user.isValid());
                    stmt2.setBoolean(6, user.canVote());
                    stmt2.setInt(7, 0);
                    stmt2.executeUpdate();
                    stmt2.close();
                    userMap.put(user.getUserId(), user);
                }
            } else {
                rs.close();
                stmt.close();
                LOGGER.warning("[Vote] Vote is not found");
                return null;
            }
            rs.close();
            stmt.close();
            // "INSERT INTO Reservation(ID, Name, ReserveDate, RoomNum) VALUES(5, '이순신', '2016-02-16', 1108);"

            this.db.disconnect();
            return new VoteData(voteId, 0, false, userMap);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 투표의 정보를 갱신합니다. 유저의 갱신은 불가능합니다.
     * @param voteid    갱신할 투표의 ID
     * @param result    투표의 결과 여부
     * @param skipVotes 스킵에 투표한 수
     */
    public void updateVote(Integer voteid, boolean result, Integer skipVotes) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update VoteSet set result=?, skipvotes=? where voteid=?");
            stmt.setBoolean(1, result);
            stmt.setInt(2, skipVotes);
            stmt.setInt(3, voteid);
            stmt.executeUpdate();
            stmt.close();
            this.db.disconnect();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 유저를 갱신합니다. 투표의 갱신은 불가능합니다.
     * @param userid    갱신할 유저의 ID
     * @param user  갱신할 유저 객체
     */
    public void updateUser(Integer userid, VoteUser user) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update VoteUsers set displayName=?, faceid=?, job=?, isValid=?, canVote=?, voteResult=? where userid=?");

            stmt.setString(1, user.getDisplayName());
            stmt.setString(2, user.getFaceId());
            stmt.setString(3, user.getJob());
            stmt.setBoolean(4, user.isValid());
            stmt.setBoolean(5, user.canVote());
            stmt.setInt(6, user.getVoteResult());
            stmt.setInt(7, userid);

            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 투표를 삭제합니다. 투표에 등록된 유저도 같이 삭제합니다.
     * @param voteid 삭제할 투표의 ID
     */
    public void deleteVote(Integer voteid) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteUsers where voteid=?");
            stmt.setInt(1, voteid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PreparedStatement stmt2 = conn.prepareStatement("delete from VoteUsers where userid=?");
                stmt2.setInt(1, rs.getInt(1));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs.close();
            stmt.close();
            stmt = conn.prepareStatement("delete from VoteSet where voteid=?");
            stmt.setInt(1, voteid);
            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Integer는 최상위 표수
    public Map<List<VoteUser>, Integer> getVoteMaxResultByVoteId(Integer voteid) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from VoteUsers where voteid=? order by voteResult desc");
            stmt.setInt(1, voteid);

            List<VoteUser> users = new ArrayList<>();
            Map<List<VoteUser>, Integer> userMap = new HashMap<>(); //

            ResultSet rs = stmt.executeQuery();
            int maxVote = 0;
            while (rs.next()) {
                int thisVote = rs.getInt(7);
                if (thisVote > maxVote) {
                    maxVote = thisVote;
                }

                if (thisVote == maxVote) {
                    users.add(new VoteUser(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getBoolean(6),
                            rs.getBoolean(7),
                            rs.getInt(8)
                    ));
                }
            }

            rs.close();
            stmt.close();
            this.db.disconnect();
            userMap.put(users, maxVote);
            return userMap;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
