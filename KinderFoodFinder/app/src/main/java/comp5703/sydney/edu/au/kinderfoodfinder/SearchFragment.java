package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.ProductDao;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticsDatabase;

/* * * * * * * * * * *
 * May be used later *
 * * * * * * * * * * */
//implements GetProductsData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private static final String TAG = "SearchFragment";
    public static final String PRODUCT_DETAIL = "PRODUCT_DETAIL";
    public static final String IMAGE_DETAIL = "IMAGE_DETAIL";

    private SearchView searchView;
    private ScrollView touchInterceptor;
    private ImageView imageView;
    private ListView egglv,pigslv,chickenlv;
    private ListView product;


    ArrayList<Items> itemsArrayList;
    ArrayList<Items> eggsList=new ArrayList<>( );
    ArrayList<Items> pigList =new ArrayList<>(  );
    ArrayList<Items> chickenList =new ArrayList<>(  );
    ArrayList<Items> bestList =new ArrayList<>(  );
    ArrayList<Items> goodList=new ArrayList<>(  );
    ArrayList<Items> avoidList=new ArrayList<>(  );
    ArrayList<Items> resultList =new ArrayList<>(  );
    public final int VIEW_ITEM_REQUEST_CODE = 647;
    ArrayList<Items> eggsdata=new ArrayList<>(  );
    private ArrayList<Product> result=new ArrayList<>(  );
    ItemsAdapter productAdapter;
    BrandAdapter brandAdapter;

    private ArrayList<Items> accResult;

    private int category,type;

    ArrayList<String> items;
    int temp=0;


//    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


//        BackgroundTask backgroundTask1=new BackgroundTask( getActivity() );
//        backgroundTask1.execute( "read_info");

//        eggsList=searchBackground.get();



        searchView=view.findViewById( R.id.searchProduct );
        product=view.findViewById( R.id.productListView );
        accResult=new ArrayList<>(  );

        items=new ArrayList<>(  );
        String info="All;";
        ArrayList<Product> test=new ArrayList<>(  );

        test=DaoUnit.getInstance().getProduct();

        items.add( "ALL" );
        for (Product products:test){
            String brand=products.getCategory().toUpperCase();
            if(! info.contains( brand)){
                info=info+brand+";";
                items.add( brand );
            }
        }

        RadioGroup catogryRadioes = view.findViewById(R.id.radioCatogory);

        Spinner spinner = view.findViewById(R.id.spinner);
        //create a list of items for the spinner.

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter1);
        spinner.setClickable( false );

        spinner.setOnItemSelectedListener( this );
        result=new ArrayList<>(  );

        catogryRadioes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // clear search view text
                searchView.setQuery( "",false );
                category = checkedId;
                String type=items.get( temp );
                // set list view
                if(category==R.id.radioBrandName){
                    brandAdapter=new BrandAdapter( getActivity(),result );
                    product.setAdapter( brandAdapter );
                    brandAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren( product );
                }else {
                    productAdapter=new ItemsAdapter( getActivity(),accResult );
                    product.setAdapter( productAdapter );
                    productAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                new Searchbrand().doInBackground(   );
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText( getActivity(),"Total Find "+String.valueOf(result.size())+" Records",Toast.LENGTH_LONG ).show();
//                BToast.success(MainActivity.this).text("Total Searching Result"+String.valueOf(ps.size())+" Records").show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(category==R.id.radioBrandName){
//                    result=DaoUnit.getInstance().searchByBrand( category,items.get( temp ),newText );
                    BrandAdapter brandAdapter=new BrandAdapter( getActivity(),result );
                    product.setAdapter( brandAdapter );
                    brandAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren( product );
                }else if(category==R.id.radioAccreditation){
                    new Searchbrand().doInBackground( "acc",newText );
//                    accResult=DaoUnit.getInstance().searchByAcc( category,items.get( temp ),newText );
                    ItemsAdapter productAdapter=new ItemsAdapter( getActivity(),accResult );
                    product.setAdapter( productAdapter );
                    productAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                new Searchbrand().doInBackground( "brand",newText );

                return false;
            }
        });

        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Product p= (Product) productAdapter.getItem( position );
                if(category==R.id.radioBrandName){
                    Product p=result.get( position );
                    List<Accreditation> accreditations=p.getAccreditation();

                    String acc="ACC";
                    String rating= "GOOD";
                    acc=accreditations.get( 0 ).getAccreditation();
                    rating=accreditations.get( 0 ).getRating();
                    ProductDao productDao= MyApplication.getInstance().getDaoSession().getProductDao();
                    Product test=new Product(  );
                    String accId=p.getAccreditation().get( 0 ).getSid();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (intent != null) {
//                        Accreditation accreditation= (Accreditation) p.getAccreditation();
                        intent.putExtra( "sid",p.getSid() );
                        intent.putExtra("brand", p.getBrand_Name());
                        intent.putExtra("type", p.getCategory());
                        intent.putExtra("accreditation", acc);
                        intent.putExtra("rating", rating);
                        intent.putExtra("location", p.getAvailable());
                        intent.putExtra( "stringId",p.getSid() );
                        intent.putExtra( "page","search" );
                        List<Accreditation> accreditation= (List<Accreditation>) p.getAccreditation();
                        intent.putExtra( "accid",accId );
//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivity( intent );

                    }
                }else if(category==R.id.radioAccreditation) {
                    Intent intent = new Intent( getActivity(), Detail2Activity.class );
                    Items acc=accResult.get( position );
                    if (intent != null) {
//                        Accreditation accreditation= (Accreditation) p.getAccreditation();
                        intent.putExtra( "stringId", acc.getSid());
                        intent.putExtra( "page", "search" );
                        intent.putExtra( "accid", acc.getAccID() );
                        startActivity( intent );
                    }
                }
            }
        } );

        return view;
    }

    private void readProduct() {
        ProductDatabase productDatabase = new ProductDatabase( getActivity() );
        SQLiteDatabase database = productDatabase.getReadableDatabase();

        String myname = "eggs";

        String sql = "select * from kkf_table where category='eggs'";
        Cursor cursor = database.rawQuery( sql,new String[] {});

        String info = "";

        while (cursor.moveToNext()) {

            Items items=new Items(  );
            String info1 = "";
            String id = Integer.toString( cursor.getInt( cursor.getColumnIndex( ProductContract.ProductEntry.PRODUCT_ID ) ) );
            String brand = cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.BRAND_NAME ) );
            String acc = cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.ACCREDITATION ) );
            String rating = cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.RATING ) );
            String available = cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.AVAILABLE ) );
            String category = cursor.getString( cursor.getColumnIndex( ProductContract.ProductEntry.CATEGORY ) );
//
            items.setType( category );
            items.setBrand( brand );
            items.setAccreditation( acc );
            items.setRating( rating );
            items.setAvailable( available );
            eggsdata.add( items );

        }
        
    }

    // test the function
    public void readEggData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.eggs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));
                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Eggs" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );
                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }

                    eggsList.add( items );

                    Log.d("lengthllllll :",items.getType()+"$$$"+ items.getBrand());

                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readChickenData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.chickens);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));

                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Chickens" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );
                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }
                    chickenList.add( items );
                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readporkData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.pigs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));
                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Pork" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );

                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }

                    pigList.add( items );
                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("spinner",String.valueOf( position ));
        Log.d("spinner",items.get( position ));
        searchView.setQuery( "",false );
        temp=position;
        if(category==R.id.radioBrandName){
//            result=DaoUnit.getInstance().getAllinsearchBrand(items.get( temp ) );

            brandAdapter=new BrandAdapter( getActivity(),result );
            brandAdapter.notifyDataSetChanged();
            product.setAdapter( brandAdapter );
            Utility.setListViewHeightBasedOnChildren( product );

        }else if(category==R.id.radioAccreditation) {

//            accResult=DaoUnit.getInstance().getAllinsearchAcc( items.get( temp ) );
            productAdapter=new ItemsAdapter( getActivity(),accResult );
            product.setAdapter( productAdapter );
            productAdapter.notifyDataSetChanged();
            Utility.setListViewHeightBasedOnChildren(product);
        }

        new Searchbrand().doInBackground( );

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //asyncTask for search features
    private class Searchbrand extends AsyncTask<String, String, String> {
        Context context;
        @Override
        protected String doInBackground(String... strings) {
            //select search type brand name or accreditation
            //select product type eggs, chicken or pork
            if(strings.length==2){
                if(strings[0].equalsIgnoreCase( "brand" )){
                    result=DaoUnit.getInstance().searchByBrand( category,items.get( temp ),strings[1] );
                }else if(strings[0].equalsIgnoreCase( "acc" )){
                    accResult=DaoUnit.getInstance().searchByAcc( category,items.get( temp ),strings[1] );
                }
            }else {
                if(category==R.id.radioBrandName){
                    //get the result based on query condition
                    result=DaoUnit.getInstance().getAllinsearchBrand(items.get( temp ) );
                    //set listview adapter
                    brandAdapter=new BrandAdapter( getActivity(),result );
                    brandAdapter.notifyDataSetChanged();
                }else {
                    //get the result based on query condition
                    accResult=DaoUnit.getInstance().getAllinsearchAcc( items.get( temp ) );
                    //set listview adapter
                    productAdapter=new ItemsAdapter( getActivity(),accResult );
                    productAdapter.notifyDataSetChanged();
                }
            }

            return "One Row Insert";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate( values );
        }
        @Override
        protected void onPostExecute(String s) {
        }

    }


}
