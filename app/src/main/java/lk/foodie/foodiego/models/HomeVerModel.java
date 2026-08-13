package lk.foodie.foodiego.models;

public class HomeVerModel {

    String imageUrl;
    String foodName;
    String category;
    String description;
    Boolean available;
    Double price;

    public HomeVerModel() {
    }

    public HomeVerModel(String imageUrl, String foodName, String category, String description, Boolean available, Double price) {
        this.imageUrl = imageUrl;
        this.foodName = foodName;
        this.category = category;
        this.description = description;
        this.available = available;
        this.price = price;

    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}