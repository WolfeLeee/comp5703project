package comp5703.sydney.edu.au.kinderfoodfinder.Database;

public class ProductContract {


    private ProductContract(){

    }

    public static class ProductEntry{
        public static final String TABLE_NAME="kkf_table";
        public static final String PRODUCT_ID="product_id";
        public static final String BRAND_NAME="brand_name";
        public static final String ACCREDITATION="accreditation";
        public static final String RATING="rating";
        public static final String AVAILABLE="available";
        public static final String CATEGORY="category";

    }
}
