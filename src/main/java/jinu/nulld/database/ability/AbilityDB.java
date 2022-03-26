package jinu.nulld.database.ability;

import jinu.nulld.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static jinu.nulld.Main.LOGGER;

public class AbilityDB {

    private final MySQL db;

    public AbilityDB() {
        this.db = new MySQL();
    }

    public void insertUser(String displayName, String faceId, String job, boolean abilityUse, boolean isValid) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into AbilityUsers(displayName, faceId, job, abilityUse, isValid) values (?, ?, ?, ?, ?)");

            stmt.setString(1, displayName);
            stmt.setString(2, faceId);
            stmt.setString(3, job);
            stmt.setBoolean(4, abilityUse);
            stmt.setBoolean(5, isValid);

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * AbilityUser 객체를 구해옵니다.
     * @param userID    User의 ID를 받습니다.
     * @return  AbilityUser 객체를 반환합니다.
     */
    public AbilityUser getUserByID(Integer userID) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from AbilityUsers where userId=?");

            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                AbilityUser user = new AbilityUser(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        rs.getBoolean(6)
                );
                rs.close();
                stmt.close();
                this.db.disconnect();
                return user;
            } else {
                LOGGER.warning("[Ability] User is not found");
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
     * 유저를 갱신합니다. 투표의 갱신은 불가능합니다.
     * @param userid    갱신할 유저의 ID
     * @param user  갱신할 유저 객체
     */
    public void updateUser(Integer userid, AbilityUser user) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update AbilityUsers set displayName=?, faceId=?, job=?, abilityUse=?, isValid=? where userId=?");

            stmt.setString(1, user.getDisplayName());
            stmt.setString(2, user.getFaceId());
            stmt.setString(3, user.getJob());
            stmt.setBoolean(4, user.abilityUse());
            stmt.setBoolean(5, user.isValid());
            stmt.setInt(6, userid);

            stmt.executeUpdate();
            stmt.close();

            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<AbilityUser> getUsers() {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from AbilityUsers");

            ResultSet rs = stmt.executeQuery();
            List<AbilityUser> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new AbilityUser(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        rs.getBoolean(6)
                ));
            }
            rs.close();
            stmt.close();

            this.db.disconnect();

            return users;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(int userId) {
        try {
            this.db.connect();
            Connection conn = this.db.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from AbilityUsers where userId=?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PreparedStatement stmt2 = conn.prepareStatement("delete from AbilityUsers where userId=?");
                stmt2.setInt(1, rs.getInt(1));
                stmt2.executeUpdate();
                stmt2.close();
            }
            rs.close();
            stmt.close();
            this.db.disconnect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
