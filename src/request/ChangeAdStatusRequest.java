package request;

public class ChangeAdStatusRequest {
    private final long adId;
    private final long userId;

    public ChangeAdStatusRequest(long adId, long userId) {
        this.adId = adId;
        this.userId = userId;
    }

    public long getAdId() { return adId; }
    public long getUserId() { return userId; }
}
