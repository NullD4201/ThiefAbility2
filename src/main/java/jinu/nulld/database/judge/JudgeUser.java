package jinu.nulld.database.judge;

public class JudgeUser {
    private final int userId;
    private final int judgeId;
    private final String playerUUID;
    private final boolean isValid;
    private final boolean canJudge;

    public JudgeUser(int userId, int judgeId, String playerUUID, boolean isValid, boolean canJudge) {
        this.userId = userId;
        this.judgeId = judgeId;
        this.playerUUID = playerUUID;
        this.isValid = isValid;
        this.canJudge = canJudge;
    }

    public int getUserId() {
        return userId;
    }

    public int getJudgeId() {
        return judgeId;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean canJudge() {
        return canJudge;
    }
}
