package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import android.os.Environment;

public class Contract {

    final  public static String db_path = Environment.getExternalStorageDirectory().getPath() + "/KFF.db";

    public static final String TABLE_NAME="Accreditation_table";
    public static final String sid="sid";
    public static final String ParentId="parentId";
    public static final String Accreditation="Accreditation";
    public static final String Rating="Rating";
}
