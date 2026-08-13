package lk.foodie.foodiego.models;

public class Food {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private boolean available;
    private String imageUrl;

    public Food() {
    }

    public Food(String id, String name, String description, double price, String category, boolean available, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.imageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
