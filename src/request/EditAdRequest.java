package request;

public class EditAdRequest {
    private final long adId;
    private final long userId;
    private final String category;
    private final String title;
    private final String description;
    private final String price;

    public EditAdRequest(long adId, long userId, String category, String title, String description, String price) {
        this.adId = adId;
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public long getAdId() { return adId; }
    public long getUserId() { return userId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
}
