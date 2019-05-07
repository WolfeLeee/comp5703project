package comp5703.sydney.edu.au.kinderfoodfinder.Database;

public class StoreContract {

    private StoreContract(){

    }

    public static class StoreEntry{
        public static final String TABLE_NAME="kkf_storetable";
        public static final String PRODUCT_ID="product_id";
        public static final String BRAND_NAME="brand_name";
        public static final String STORE_NAME="store_name";
        public static final String STREET="street";
        public static final String POSTCODE="postcode";
        public static final String STATE="state";
        public static final String LAT="latitude";
        public static final String LONG="longitude";


    }
}
