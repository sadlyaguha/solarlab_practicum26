package request;

public class CreateAdRequest {
    private final long authorId;
    private final String category;
    private final String title;
    private final String description;
    private final String price;

    public CreateAdRequest(long authorId, String category, String title, String description, String price) {
        this.authorId = authorId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public long getAuthorId() { return authorId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
}
