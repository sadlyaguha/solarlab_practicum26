package entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Advertisement implements Entity<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ACTIVE, INACTIVE, BLOCKED_BY_ADMIN
    }

    private final long id;
    private final long authorId;
    private String category;
    private String title;
    private String description;
    private String price;
    private final LocalDateTime createdAt;
    private Status status;

    public Advertisement(long id, long authorId, String category, String title,
                         String description, String price) {
        this.id = id;
        this.authorId = authorId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    @Override
    public Long getId() { return id; }

    public long getAuthorId() { return authorId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }

    public void setCategory(String category) { this.category = category; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isActive() { return status == Status.ACTIVE; }
    public boolean isBlockedByAdmin() { return status == Status.BLOCKED_BY_ADMIN; }

    public String getFormattedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getPriceDisplay() {
        return (price == null || price.isEmpty()) ? "Договорная" : price + " руб.";
    }

    @Override
    public String toString() {
        return "Объявление #" + id + "\n" +
               "  Заголовок: " + title + "\n" +
               "  Категория: " + category + "\n" +
               "  Описание: " + description + "\n" +
               "  Цена: " + getPriceDisplay() + "\n" +
               "  Дата: " + getFormattedDate() + "\n" +
               "  Статус: " + getStatusText();
    }

    public String getStatusText() {
        switch (status) {
            case ACTIVE: return "Активно";
            case INACTIVE: return "Неактивно";
            case BLOCKED_BY_ADMIN: return "Заблокировано администратором";
            default: return "Неизвестно";
        }
    }
}
