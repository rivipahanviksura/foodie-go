package lk.foodie.foodiego.models;

public class CartModel {
    private String id;
    String image;
    String name;
    Double price;
    String rating;
    private int quantity;

    public CartModel() {
    }

    public CartModel(String id, String image, String name, Double price, String rating, int quantity) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getQuantity() {
        return quantity; // Getter for quantity
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity; // Setter for quantity
    }
}
