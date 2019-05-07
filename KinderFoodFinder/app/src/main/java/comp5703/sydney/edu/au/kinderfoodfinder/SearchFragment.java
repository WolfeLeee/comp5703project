package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.ProductDao;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

/* * * * * * * * * * *
 * May be used later *
 * * * * * * * * * * */
//implements GetProductsData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener

public class SearchFragment extends Fragment
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
    ProductAdpater productAdapter;


    private int category,type;


    private LinearLayout categoryEgg, categoryChicken, categoryPig;
    private int categoryID;

//    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

//        readEggData();
        readChickenData();
        readporkData();

        readProduct();

        Log.d("ddddddddd",String.valueOf( eggsList.size() ));
        Log.d("ddddddddd",String.valueOf( chickenList.size() ));
        Log.d("ddddddddd",String.valueOf( pigList.size() ));

//        BackgroundTask backgroundTask1=new BackgroundTask( getActivity() );
//        backgroundTask1.execute( "read_info");

//        eggsList=searchBackground.get();




        // set up
        touchInterceptor = (ScrollView) getActivity().findViewById(R.id.touchInterceptor);
        searchView = (SearchView) view.findViewById(R.id.searchProduct);
        searchView.setFocusable(false);  // prevent from opening keyboard first

        categoryEgg = (LinearLayout) view.findViewById(R.id.categoryEgg);
        categoryChicken = (LinearLayout) view.findViewById(R.id.categoryChicken);
        categoryPig = (LinearLayout) view.findViewById(R.id.categoryPig);
        categoryID = 0; // 0 = no select, 1 = egg selected, 2 = chicken selected, 3 = pig selected

        product=view.findViewById( R.id.productListView );


        final ItemsAdapter adapter=new ItemsAdapter( getActivity(),itemsArrayList );
//        final ItemsAdapter eggAdapter=new ItemsAdapter( getActivity(),eggsList );
        final ItemsAdapter chickenAdapter=new ItemsAdapter( getActivity(),chickenList );
        final ItemsAdapter pigAdapter=new ItemsAdapter( getActivity(),pigList );
        final ItemsAdapter eggDataAp=new ItemsAdapter( getActivity(),eggsdata );

//        product.setAdapter( adapter );
//        Utility.setListViewHeightBasedOnChildren(product);

//        egglv=view.findViewById( R.id.egglv );
//        pigslv=view.findViewById( R.id.piglv );
//        chickenlv=view.findViewById( R.id.chickenlv );

        /* * * * * * *
         * Listeners *
         * * * * * * */
        // remove focus on search view if touch other area
        touchInterceptor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.clearFocus();
            }
        });

        // categories click listener
        categoryEgg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                categoryID = 1;
                categoryEgg.setBackgroundColor(getResources().getColor(R.color.darkOrange));
                categoryChicken.setBackgroundColor(getResources().getColor(R.color.white));
                categoryPig.setBackgroundColor(getResources().getColor(R.color.white));
                product.setAdapter( null );
            }
        });
        categoryChicken.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                categoryID = 2;
                categoryEgg.setBackgroundColor(getResources().getColor(R.color.white));
                categoryChicken.setBackgroundColor(getResources().getColor(R.color.darkOrange));
                categoryPig.setBackgroundColor(getResources().getColor(R.color.white));
                product.setAdapter( null );

            }
        });
        categoryPig.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                categoryID = 3;
                categoryEgg.setBackgroundColor(getResources().getColor(R.color.white));
                categoryChicken.setBackgroundColor(getResources().getColor(R.color.white));
                categoryPig.setBackgroundColor(getResources().getColor(R.color.darkOrange));
                product.setAdapter( null );

            }
        });




// initial function
//         search view listener for searching result
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
//        {
//            @Override
//            public boolean onQueryTextSubmit(String query)
//            {
//                // search by the input from user
//                if(categoryID == 1) // egg
//                {
//                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
////                    eggAdapter.getFilter().filter(query);
////                    egglv.setAdapter( eggAdapter );
//                    eggDataAp.getFilter().filter(query);
//                    product.setAdapter( eggDataAp );
//                    Utility.setListViewHeightBasedOnChildren(product);
//
//                    return false;
//                }
//                else if(categoryID == 2) // chicken
//                {
//
//                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();
//
//                    product.setAdapter( chickenAdapter );
//                    Utility.setListViewHeightBasedOnChildren(product);
//                }
//                else if(categoryID == 3) // pigs
//                {
//                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();
//
//                    product.setAdapter( pigAdapter );
//                    Utility.setListViewHeightBasedOnChildren(product);
//                }
//                else // nothing selected
//                {
//                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
//
//
//                }
////                Log.d(TAG, "onQueryTextSubmit: "+query);
////                GetProductsData getProductsData = new GetProductsData(SearchFragment.this,SearchFragment.this.getContext());
////                getProductsData.execute(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText)
//            {
//
//                if(categoryID == 1) // egg
//                {
//                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
////                    eggAdapter.getFilter().filter(query);
////                    egglv.setAdapter( eggAdapter );
//                    eggDataAp.getFilter().filter(newText);
//                    product.setAdapter( eggDataAp );
//                    Utility.setListViewHeightBasedOnChildren(product);
//
//                    return false;
//                }
//                else if(categoryID == 2) // chicken
//                {
//
//                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();
//
//                    chickenAdapter.getFilter().filter(newText);
//
//                    product.setAdapter( chickenAdapter );
//                    Utility.setListViewHeightBasedOnChildren(product);
//                }
//                else if(categoryID == 3) // pigs
//                {
//                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();
//
//                    pigAdapter.getFilter().filter(newText);
//
//                    product.setAdapter( pigAdapter );
//                    Utility.setListViewHeightBasedOnChildren(product);
//                }
//                else // nothing selected
//                {
//                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
//
//                }
//                return false;
//            }
//        });


//        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if(categoryID == 1) // egg
//                {
//                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
//                    Items c= (Items) eggDataAp.getItem(position);
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    if (intent != null) {
//
//                        intent.putExtra("brand", c.getBrand());
//                        intent.putExtra("type", c.getType());
//                        intent.putExtra("accreditation", c.getAccreditation());
//                        intent.putExtra("rating", c.getRating());
//                        intent.putExtra("location", c.getAvailable());
//
////                    intent.putExtra("img", String.valueOf(c.getImg()));
//                        startActivity( intent );
//
//
//                    }
//                }
//                else if(categoryID == 2) // chicken
//                {
//
//                    Items c= (Items) chickenAdapter.getItem(position);
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    if (intent != null) {
//
//                        intent.putExtra("brand", c.getBrand());
//                        intent.putExtra("type", c.getType());
//                        intent.putExtra("accreditation", c.getAccreditation());
//                        intent.putExtra("rating", c.getRating());
//                        intent.putExtra("location", c.getAvailable());
//
////                    intent.putExtra("img", String.valueOf(c.getImg()));
//                        startActivity( intent );
//
//
//                    }
//                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();
//                }
//                else if(categoryID == 3) // pigs
//                {
//                    Items c= (Items) pigAdapter.getItem(position);
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    if (intent != null) {
//
//                        intent.putExtra("brand", c.getBrand());
//                        intent.putExtra("type", c.getType());
//                        intent.putExtra("accreditation", c.getAccreditation());
//                        intent.putExtra("rating", c.getRating());
//                        intent.putExtra("location", c.getAvailable());
//
////                    intent.putExtra("img", String.valueOf(c.getImg()));
//                        startActivity( intent );
//
//                    }
//                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();
//                }
//                else // nothing selected
//                {
//                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        } );


///***********************************************************



        Intent intent =getActivity().getIntent();
        final String userID=intent.getStringExtra( "userID" );
        final String gender=intent.getStringExtra( "gender" );
        final String birthday=intent.getStringExtra( "birthday" );

        RadioGroup catogryRadioes = view.findViewById(R.id.radioCatogory);
        RadioGroup typeRadioes = view.findViewById(R.id.radioType);
        //大类选择
        catogryRadioes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                category = checkedId;
            }
        });
        //鸡、鸡蛋、猪选择
        typeRadioes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                type = checkedId;
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
                result=DaoUnit.getInstance().searchByOption(category,type,query);

                ProductAdpater productAdapter=new ProductAdpater( getActivity(),result );

                product.setAdapter( productAdapter );
                productAdapter.notifyDataSetChanged();
                Utility.setListViewHeightBasedOnChildren(product);

                Log.d("search result",String.valueOf( result.size() ));


                Toast.makeText( getActivity(),"Total Find "+String.valueOf(result.size())+" Records",Toast.LENGTH_LONG ).show();
//                BToast.success(MainActivity.this).text("Total Searching Result"+String.valueOf(ps.size())+" Records").show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Product p= (Product) productAdapter.getItem( position );
                Product p=result.get( position );


                ProductDao productDao= MyApplication.getInstance().getDaoSession().getProductDao();
                Product test=new Product(  );

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (intent != null) {
//                        Accreditation accreditation= (Accreditation) p.getAccreditation();
                        String acc="ACC";
                        String rating= "GOOD";
                        intent.putExtra("brand", p.getBrand_Name());
                        intent.putExtra("type", p.getCategory());
                        intent.putExtra("accreditation", acc);
                        intent.putExtra("rating", rating);
                        intent.putExtra("location", p.getAvailable());
                        intent.putExtra( "stringId",p.getSid() );
                        intent.putExtra( "gender",gender );
                        intent.putExtra( "birthday",birthday );
                        intent.putExtra( "userID",userID );
                        List<Accreditation> accreditation= (List<Accreditation>) p.getAccreditation();
                        String a=accreditation.get( 0 ).getSid();
                        String b=accreditation.get( 0 ).getAccreditation();
                        String c=accreditation.get( 0 ).getRating();


                        String info=p.getSid()+"; "+p.getBrand_Name()+"; "+p.getCategory()+"; "+p.getImage()+"; "
                                +a+"; "+b+"; "+c;
                        Log.d("statistics put record",info);
//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivity( intent );
                    }
            }
        } );




        return view;
    }

    /* * * * * *
     * Methods *
     * * * * * */
//    @Override
//    public void onDataAvailable(List<Products> data)
//    {
//        Log.d(TAG, "onDataAvailable: starts");
//        mProductsRecyclerViewAdapter.loadNewData(data);
//        Log.d(TAG, "onDataAvailable: ends");
//    }
//
//    @Override
//    public void onItemClick(View view, int position)
//    {
//        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
//        Products pseudoproduct = mProductsRecyclerViewAdapter.getProducts(position);
//        Products passingproducts = new Products(pseudoproduct.getCategory(),pseudoproduct.getBrand());
//        intent.putExtra(PRODUCT_DETAIL,passingproducts);
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        pseudoproduct.getImage().compress(Bitmap.CompressFormat.PNG,0,bStream);
//        byte[] byteArray = bStream.toByteArray();
//        intent.putExtra(IMAGE_DETAIL,byteArray);
//        startActivity(intent);
//    }


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

//
//


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


}
