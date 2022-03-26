package jinu.nulld.database.vote;

public class VoteUser {
    private final Integer userId;
    private final Integer voteId;
    private final String displayName;
    private final String faceId;
    private final String job;
    private final boolean isValid;
    private final boolean canVote;
    private final Integer voteResult;

    /**
     * VoteUser를 생성합니다. 혼자서 동작하지 않으며, VoteData와 묶어서 전달됩니다.
     * VoteUser를 생성하신 후, Map에 묶어 VoteData에 전달하는 식으로 등록하시면 됩니다.
     * @param userId    이 유저의 ID값 < 직접 생성 시 0으로 설정하세요
     * @param voteId    이 유저가 존재하는 투표의 ID값 < 직접 생성 시 0으로 설정하세요
     * @param displayName   이 유저가 표시되는 이름
     * @param faceId    이 유저의 얼굴 사진 ID
     * @param job   이 유저의 직업
     * @param isValid   이 유저의 UUID
     * @param voteResult    이 유저가 받은 투표 수 < 직접 생성 시 0으로 설정하세요
     */
    public VoteUser(Integer userId, Integer voteId, String displayName, String faceId, String job, boolean isValid, boolean canVote, Integer voteResult) {
        this.userId = userId;
        this.voteId = voteId;
        this.displayName = displayName;
        this.faceId = faceId;
        this.job = job;
        this.isValid = isValid;
        this.canVote = canVote;
        this.voteResult = voteResult;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getVoteId() {
        return voteId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFaceId() {
        return faceId;
    }

    public String getJob() {
        return job;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean canVote() {
        return canVote;
    }

    public Integer getVoteResult() {
        return voteResult;
    }
}
