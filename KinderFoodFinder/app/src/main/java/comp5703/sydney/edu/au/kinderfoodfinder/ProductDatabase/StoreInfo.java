package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


public class StoreInfo {
    private String storeName;
    private String StreetAddress;
    private String State;
    private String Postcode;
    private String Lat;
    private String Long;
    private String Brandid;
    private String Brandname;

    public StoreInfo( String storeName, String StreetAddress, String State, String Postcode, String Lat, String Long, String Brandid, String Brandname) {

        this.storeName = storeName;
        this.StreetAddress = StreetAddress;
        this.State = State;
        this.Postcode = Postcode;
        this.Lat = Lat;
        this.Long = Long;
        this.Brandid = Brandid;
        this.Brandname = Brandname;
    }

    public StoreInfo() {
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStreetAddress() {
        return this.StreetAddress;
    }

    public void setStreetAddress(String StreetAddress) {
        this.StreetAddress = StreetAddress;
    }

    public String getState() {
        return this.State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getPostcode() {
        return this.Postcode;
    }

    public void setPostcode(String Postcode) {
        this.Postcode = Postcode;
    }

    public String getLat() {
        return this.Lat;
    }

    public void setLat(String Lat) {
        this.Lat = Lat;
    }

    public String getLong() {
        return this.Long;
    }

    public void setLong(String Long) {
        this.Long = Long;
    }

    public String getBrandid() {
        return this.Brandid;
    }

    public void setBrandid(String Brandid) {
        this.Brandid = Brandid;
    }

    public String getBrandname() {
        return this.Brandname;
    }

    public void setBrandname(String Brandname) {
        this.Brandname = Brandname;
    }

    @Override
    public String toString() {
        return "StoreInfo{" +
                ", storeName='" + storeName + '\'' +
                ", StreetAddress='" + StreetAddress + '\'' +
                ", State='" + State + '\'' +
                ", Postcode='" + Postcode + '\'' +
                ", Lat='" + Lat + '\'' +
                ", Long='" + Long + '\'' +
                ", Brandid='" + Brandid + '\'' +
                ", Brandname='" + Brandname + '\'' +
                '}';
    }
}
