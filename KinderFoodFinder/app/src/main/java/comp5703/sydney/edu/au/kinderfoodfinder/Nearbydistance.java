package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.annotation.NonNull;

public class Nearbydistance implements Comparable
{
    private int distance;
    private String location;
    private String brand;

    public Nearbydistance(String brand, String location, int distance)
    {
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

    @Override
    public int compareTo(@NonNull Object o)
    {
        int compareDistance = (int) ((Nearbydistance) o).getDistance();
        return this.distance - compareDistance;
    }
}
