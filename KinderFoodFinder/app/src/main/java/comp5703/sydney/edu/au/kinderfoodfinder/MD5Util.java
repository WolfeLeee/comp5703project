package comp5703.sydney.edu.au.kinderfoodfinder;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**

 */
public class MD5Util {
    private static final String TAG = "MD5Util";

    /***
     *
     */
    public static String string2MD5(String inStr) {
        Log.e(TAG, "string2MD5: -------------------------");
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     *
     */
    public static String convertMD5(String inStr) {
        Log.e(TAG, "convertMD5: -------------------------");
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }



    public String encrypt(String str) {
        // String s = new String(str);

        // MD5
        String s1 = string2MD5(str);
        //
        String s2 = new String(s1);

        return convertMD5(s2);

    }


}
