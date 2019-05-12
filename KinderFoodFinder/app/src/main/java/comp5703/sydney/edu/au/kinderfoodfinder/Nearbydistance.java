package comp5703.sydney.edu.au.kinderfoodfinder;

public class Nearbydistance {


    private int distance;
    private String location;
    private String brand;

    public Nearbydistance(String brand, String location, int distance) {
        this.distance = distance;
        this.location = location;
        this.brand = brand;

    }

    public double getDistance() {
        return this.distance;
    }
    public void setDistance(int distance){
        this.distance = distance;
    }

    public String getLocation(){
        return this.location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
