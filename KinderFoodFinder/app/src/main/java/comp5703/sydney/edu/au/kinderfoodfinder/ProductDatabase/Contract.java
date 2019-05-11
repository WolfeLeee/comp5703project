package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import android.os.Environment;

public class Contract {

    final  public static String db_path = Environment.getExternalStorageDirectory().getPath() + "/KFF.db";

    public static final String TABLE_NAME="Accreditation_table";
    public static final String sid="sid";
    public static final String ParentId="parentId";
    public static final String Accreditation="Accreditation";
    public static final String Rating="Rating";

    public static class StoreContract{
        public static final String TABLE_NAME="store_table";
        public static final String storeName="storeName";
        public static final String StreetAddress="StreetAddress";
        public static final String State="State";
        public static final String Postcode="Postcode";
        public static final String Lat="Lat";
        public static final String Long="Long";
        public static final String Brandid="Brandid";
        public static final String Brandname= "Brandname";
    }
}
