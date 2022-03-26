package jinu.nulld.database.vote;

import java.util.Map;

public class VoteData {
    private final Integer voteId;
    private final Integer skipVotes;
    private final boolean isResult;
    private final Map<Integer, VoteUser> voteUsers;

    /**
     * VoteData를 생성합니다.
     * 직접 생성 시, VoteUser가 들어가는 Map을 만드신 후, 등록해서 생성하시면 됩니다.
     * @param voteId    이 투표의 ID < 직접 생성 시 0으로 설정해주세요
     * @param skipVotes 스킵에 투표한 수 < 직접 생성 시 0으로 설정해주세요
     * @param isResult  이 투표가 결과가 나왔는가 < 직접 생성 시 false로 설정해주세요
     * @param voteUsers 투표에 올라온 후보 맵 < VoteUser 맵을 생성 후, 등록해주세요.
     */
    public VoteData(Integer voteId, Integer skipVotes, boolean isResult, Map<Integer, VoteUser> voteUsers) {
        this.voteId = voteId;
        this.skipVotes = skipVotes;
        this.isResult = isResult;
        this.voteUsers = voteUsers;
    }

    public int getVoteId() {
        return voteId;
    }

    public int getSkipVotes() {
        return skipVotes;
    }

    public boolean isResult() { return isResult; }

    public Map<Integer, VoteUser> getVoteUsers() {
        return voteUsers;
    }
}
