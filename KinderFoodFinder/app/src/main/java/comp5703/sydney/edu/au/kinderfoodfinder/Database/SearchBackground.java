package comp5703.sydney.edu.au.kinderfoodfinder.Database;




import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.Items;
import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.ProductsContract;

public class SearchBackground extends AsyncTask<String,Void,ArrayList<Items>> {
    Context context;

    private ArrayList<Items> eggList;
    public SearchBackground(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Items> doInBackground(String... params) {

        String method = params[0];


        ProductDatabase productDatabase = new ProductDatabase( context );

        SQLiteDatabase writableDatabase = productDatabase.getWritableDatabase();
        SQLiteDatabase readDatabase = productDatabase.getReadableDatabase();
        ContentResolver contentResolver = context.getContentResolver();



        if (method.equals( "eggs" )) {
            eggList=new ArrayList<Items>(  );

            String[] projiectons={ProductContract.ProductEntry.PRODUCT_ID,ProductContract.ProductEntry.BRAND_NAME,ProductContract.ProductEntry.ACCREDITATION,ProductContract.ProductEntry.RATING,ProductContract.ProductEntry.AVAILABLE,ProductContract.ProductEntry.CATEGORY};

            String selection = ProductsContract.Columns.BRAND_NAME+" LIKE '%"+params[0]+"%' " ;
            Cursor cursor=readDatabase.query( ProductContract.ProductEntry.TABLE_NAME ,
                    projiectons,selection,null,null,null,null );



//            Cursor cursor = contentResolver.query(ProductsContract.CONTENT_URI, projection, selection, null, ProductsContract.Columns.BRAND_SORTORDER);

            if (cursor != null) {

                while (cursor.moveToNext()){
                    String info1 ="";
                    String id =Integer.toString(cursor.getInt( cursor.getColumnIndex( ProductContract.ProductEntry.PRODUCT_ID ) ));
                    String brand=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.BRAND_NAME ) );
                    String acc=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.ACCREDITATION ) );
                    String rating=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.RATING ) );
                    String available=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.AVAILABLE ) );
                    String category=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.CATEGORY ) );
                    Items items=new Items(  );
                    items.setBrand( brand );
                    items.setAccreditation( acc );
                    items.setRating( rating );
                    items.setAvailable( available );
                    items.setType( category );

//
                    Log.d( "Database","add item to eggslist" );

                }

            }

            return eggList;
        } else if (method.equals( "check_database" )) {
//
//            Cursor cursor = readDatabase.query( ProductContract.ProductEntry.TABLE_NAME, null, null, null, null, null, null );
//            if (cursor.moveToFirst()) {
//                Log.d( "Database ", " already has information" );
//            } else {
//                Log.d( "Database ", " Please insert data" );
//
//            }


//        } else if (method.equals( "read_info" )) {
////            String brand =params[1];
////            String acc=params[2];
////            String rating=params[3];
////            String available =params[4];
////            String category=params[5];
////            productDatabase.addProduct( brand,acc,rating,available,category,database );
////            productDatabase.close();
////            Cursor cursor=productDatabase.readContats( readDatabase );
////            String info ="";
////
////            while (cursor.moveToNext()){
////                String info1 ="";
////                String id =Integer.toString(cursor.getInt( cursor.getColumnIndex( ProductContract.ProductEntry.PRODUCT_ID ) ));
////                String brand=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.BRAND_NAME ) );
////                String acc=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.ACCREDITATION ) );
////                String rating=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.RATING ) );
////                String available=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.AVAILABLE ) );
////                String category=cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.CATEGORY ) );
//////                info="\n\n"+"ID :"+id+"\nBrand :"+brand+"\nAcc :"+acc+"\nRating :"+rating+"\nAvailbale :"+available+"\nCategory :"+category;
////                info1="ID :"+id+";; Brand :"+brand+";; Acc :"+acc+";; Rating :"+rating+";; Availbale :"+available+";; Category :"+category;
////
////                Log.d( "Database",info1 );
//
//            }


//            Log.d( "Database",info );
            return eggList;
        }else if(method.equalsIgnoreCase( "eggs" )){



        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate( values );
    }

    @Override
    protected void onPostExecute(ArrayList<Items> result) {

//        Toast.makeText( context,result,Toast.LENGTH_LONG ).show();
    }


}
//
