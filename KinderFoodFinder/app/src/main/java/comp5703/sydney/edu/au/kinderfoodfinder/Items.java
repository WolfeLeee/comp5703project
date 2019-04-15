package comp5703.sydney.edu.au.kinderfoodfinder;

import java.io.Serializable;

public class Items implements Serializable {


    private String type,accreditation,brand,rating,available;
    private int img;

//    public Items(String type, String accreditation, String brand, String rating, String available, int img) {
//
//        this.type = type;
//        this.accreditation = accreditation;
//        this.brand = brand;
//        this.rating = rating;
//        this.available = available;
//
//        this.img = img;
//    }

    public Items (String type,String accreditation, String brand, String rating, String available){

        this.accreditation = accreditation;
        this.brand = brand;
        this.rating = rating;
        this.available = available;
    }
    public Items(){

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(String accreditation) {
        this.accreditation = accreditation;
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

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
