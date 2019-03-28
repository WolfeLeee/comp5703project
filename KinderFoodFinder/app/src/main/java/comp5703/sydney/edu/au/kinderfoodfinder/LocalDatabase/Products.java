package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Products implements Serializable {

    static final long serialVersionUID = 20190326;

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

    public Products (String category, String brand){
        this.brand = brand;
        this.category = category;
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
