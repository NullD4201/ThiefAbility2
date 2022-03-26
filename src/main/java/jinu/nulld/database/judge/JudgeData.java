package jinu.nulld.database.judge;

import java.util.Map;

public class JudgeData {
    private final int judgeId;
    private final int agree;
    private final int disagree;
    private final boolean isResult;
    private final Map<Integer, JudgeUser> judgeUsers;

    public JudgeData(int judgeId, int agree, int disagree, boolean isResult, Map<Integer, JudgeUser> judgeUsers) {
        this.judgeId = judgeId;
        this.agree = agree;
        this.disagree = disagree;
        this.isResult = isResult;
        this.judgeUsers = judgeUsers;
    }

    public int getJudgeId() {
        return judgeId;
    }

    public int getAgree() {
        return agree;
    }

    public int getDisagree() {
        return disagree;
    }

    public boolean isResult() {
        return isResult;
    }

    public Map<Integer, JudgeUser> getJudgeUsers() {
        return judgeUsers;
    }
}
