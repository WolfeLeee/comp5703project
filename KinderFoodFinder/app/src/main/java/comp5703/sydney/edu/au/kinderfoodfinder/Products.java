package comp5703.sydney.edu.au.kinderfoodfinder;

import java.io.Serializable;

public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brand;
    private String rating;

    public Products(String brand, String rating) {
        this.brand = brand;
        this.rating = rating;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Products{" +
                "brand='" + brand + '\n' +
                ", rating='" + rating + '}';
    }
}
