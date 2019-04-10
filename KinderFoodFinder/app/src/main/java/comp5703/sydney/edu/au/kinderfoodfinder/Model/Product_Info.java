package comp5703.sydney.edu.au.kinderfoodfinder.Model;

public class Product_Info {
    public String id, category, accreditation, brand_name, rating, location;

    public Product_Info(String id, String category, String accreditation, String brand_name, String rating, String location){
        this.id = id;
        this.category = category;
        this.accreditation = accreditation;
        this.brand_name = brand_name;
        this.rating = rating;
        this.location = location;
    }

    public Product_Info(){

    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }


    public String getAccreditation(){
        return accreditation;
    }

    public void setAccreditation(String accreditation){
        this.accreditation = accreditation;
    }

    public String getBrand_name(){
        return brand_name;
    }

    public void setBrand_name(String brand_name){
        this.brand_name = brand_name;
    }

    public String getRating(){
        return rating;
    }

    public void setRating(String rating){
        this.rating = rating;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

}
