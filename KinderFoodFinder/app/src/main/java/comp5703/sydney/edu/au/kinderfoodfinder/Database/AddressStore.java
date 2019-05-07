package comp5703.sydney.edu.au.kinderfoodfinder.Database;

public class AddressStore {
    public String brand_name, store_name, street, postcode, state, latitude, longitude;
    public Integer product_id;

    public AddressStore (Integer product_id, String brand_name, String store_name, String street,
                   String postcode, String state, String latitude, String longitude){

        this.product_id = product_id;
        this.brand_name = brand_name;
        this.store_name = store_name;
        this.street = street;
        this.postcode = postcode;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AddressStore(){

    }

    public Integer getProduct_id(){
        return product_id;
    }

    public void setProduct_id(Integer product_id){
        this.product_id = product_id;
    }

    public String getStore_name(){
        return store_name;
    }

    public void setStore_name(String store_name){
        this.store_name = store_name;
    }


    public String getPostcode(){
        return postcode;
    }

    public void setPostcode(String postcode){
        this.postcode = postcode;
    }

    public String getBrand_name(){
        return brand_name;
    }

    public void setBrand_name(String brand_name){
        this.brand_name = brand_name;
    }

    public String getStreet(){
        return street;
    }

    public void setStreet(String street){
        this.street = street;
    }

    public String getState(){
        return state;
    }

    public void setState(String state){
        this.state = state;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

}
