package request;

public class BlockUserRequest {
    private final String loginToBlock;
    private final long adminId;

    public BlockUserRequest(String loginToBlock, long adminId) {
        this.loginToBlock = loginToBlock;
        this.adminId = adminId;
    }

    public String getLoginToBlock() { return loginToBlock; }
    public long getAdminId() { return adminId; }
}
