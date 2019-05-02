package comp5703.sydney.edu.au.kinderfoodfinder.UserInfomation;

import android.content.Context;
import android.content.SharedPreferences;

import comp5703.sydney.edu.au.kinderfoodfinder.R;


public class Helper {

    public static void putSharedPreferencesString(Context context, String key, String value) {
        String last = getSharedPreferencesString(context, key);
        if (last != null && last.equals(value)) {
            return;
        }

        if (key != null && value != null) {
            final SharedPreferences prefs = context
                    .getSharedPreferences(context.getResources().getString( R.string.prefs_users), Context.MODE_PRIVATE);
            prefs.edit()
                    .putString(key, value)
                    .commit();
            //.apply();
        }
    }

    public static String getSharedPreferencesString(Context context, String key) {
        if (key != null) {
            final SharedPreferences prefs = context
                    .getSharedPreferences(context.getResources().getString(R.string.prefs_users), Context.MODE_PRIVATE);
            return prefs.getString(key, "");
        }
        return "";
    }


}
