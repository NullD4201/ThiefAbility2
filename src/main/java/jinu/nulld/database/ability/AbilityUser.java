package jinu.nulld.database.ability;

public class AbilityUser {
    private final Integer userId;
    private final String displayName;
    private final String faceId;
    private final String job;
    private final boolean abilityUse;
    private final boolean isValid;

    /**
     * VoteUser를 생성합니다. 혼자서 동작하지 않으며, VoteData와 묶어서 전달됩니다.
     * VoteUser를 생성하신 후, Map에 묶어 VoteData에 전달하는 식으로 등록하시면 됩니다.
     * @param userId    이 유저의 ID값 < 직접 생성 시 0으로 설정하세요
     * @param displayName   이 유저가 표시되는 이름
     * @param faceId    이 유저의 얼굴 사진 ID
     * @param job   이 유저의 직업
     * @param abilityUse   이 유저의 UUID
     * @param isValid 이 유저의 가능여부
     */
    public AbilityUser(Integer userId, String displayName, String faceId, String job, boolean abilityUse, boolean isValid) {
        this.userId = userId;
        this.displayName = displayName;
        this.faceId = faceId;
        this.job = job;
        this.abilityUse = abilityUse;
        this.isValid = isValid;
    }

    public Integer getUserId() {
        return userId;
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

    public boolean abilityUse() {
        return abilityUse;
    }

    public boolean isValid() {
        return isValid;
    }
}
