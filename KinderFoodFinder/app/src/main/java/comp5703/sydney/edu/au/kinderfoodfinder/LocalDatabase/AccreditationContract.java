package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.AppProvider.CONTENT_AUTHORITY;
import static comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.AppProvider.CONTENT_AUTHORITY_URI;

public class AccreditationContract {

    private static final String TAG = "AccreditationContract";
    static final String TABLE_NAME = "Accreditation";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String RATING = "Rating";
        public static final String ACCREDITATION = "Accreditation";
        public static final String BRAND = "Brand";

        private Columns(){
            // private constructors to prevent to instantiation
        }

    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." +TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."+ CONTENT_AUTHORITY +"."+TABLE_NAME;

    static Uri buildAccreditationUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI, taskId); //append a given Id to the end of the path
    }

    static long getAccreditationId(Uri uri){
        return ContentUris.parseId(uri); //converts the last path segments to a long -> take the Id to use for our switch statement in the AppProvider class
    }



}
