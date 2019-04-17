package comp5703.sydney.edu.au.kinderfoodfinder.Remote;

import comp5703.sydney.edu.au.kinderfoodfinder.Model.MyDistance;
import comp5703.sydney.edu.au.kinderfoodfinder.Model.MyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {

    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

//    @GET
//    Call<MyDistance> getDistanceMatrix(@Url String url);

}
