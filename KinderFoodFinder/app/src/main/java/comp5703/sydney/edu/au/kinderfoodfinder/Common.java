package comp5703.sydney.edu.au.kinderfoodfinder;

import comp5703.sydney.edu.au.kinderfoodfinder.Remote.IGoogleAPIService;
import comp5703.sydney.edu.au.kinderfoodfinder.Remote.RetrofitClient;

public class Common {

    private static final String GOOGLE_API_URL = "http://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
