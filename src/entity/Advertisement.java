package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Advertisement implements Entity<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final long authorId;
    private String category;
    private String title;
    private String description;
    private String price;
    private final LocalDateTime createdAt;
    private AdStatus status;

    public Advertisement(long id, long authorId, String category, String title,
                         String description, String price) {
        this.id = id;
        this.authorId = authorId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.status = AdStatus.ACTIVE;
    }

    @Override
    public Long getId() { return id; }

    public long getAuthorId() { return authorId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AdStatus getStatus() { return status; }

    public void setCategory(String category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setStatus(AdStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Объявление #" + id + "\n" +
                "  Заголовок: " + title + "\n" +
                "  Категория: " + category + "\n" +
                "  Описание: " + description + "\n" +
                "  Цена: " + price + "\n" +
                "  Дата: " + createdAt + "\n" +
                "  Статус: " + status;
    }
}