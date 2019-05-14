package comp5703.sydney.edu.au.kinderfoodfinder;

import java.util.Comparator;

public class Sorting implements Comparator<Nearbydistance> {

    public int compare(Nearbydistance d1, Nearbydistance d2) {
        if(d1.getDistance()<d2.getDistance()){
            return 1;
        }else {
            return 0;
        }
    }
}

