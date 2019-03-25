package comp5703.sydney.edu.au.kinderfoodfinder;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brand;
//    private String rating;
    private String category;
    private Bitmap image;

    public Products(String category, String brand, Bitmap image) {
        this.brand = brand;
//        this.rating = rating;
        this.category = category;
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

//    public String getRating() {
//        return rating;
//    }
//
//    public void setRating(String rating) {
//        this.rating = rating;
//    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
