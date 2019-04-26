package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.ProductsRecyclerViewAdapter;

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

        readEggData();
        readChickenData();
        readporkData();

        Log.d("ddddddddd",String.valueOf( eggsList.size() ));
        Log.d("ddddddddd",String.valueOf( chickenList.size() ));
        Log.d("ddddddddd",String.valueOf( pigList.size() ));


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
        final ItemsAdapter eggAdapter=new ItemsAdapter( getActivity(),eggsList );
        final ItemsAdapter chickenAdapter=new ItemsAdapter( getActivity(),chickenList );
        final ItemsAdapter pigAdapter=new ItemsAdapter( getActivity(),pigList );

//        chickenlv=view.findViewById( R.id.chickenlv );
        imageView=view.findViewById( R.id.image_search );

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
                imageView.setImageResource( R.drawable.farm3 );
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
                imageView.setImageResource( R.drawable.farm2 );
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
                imageView.setImageResource( R.drawable.farm1 );
            }
        });




//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                eggAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });

//         search view listener for searching result
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // search by the input from user
                if(categoryID == 1) // egg
                {
                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
//                    eggAdapter.getFilter().filter(query);
//                    egglv.setAdapter( eggAdapter );
                    eggAdapter.getFilter().filter(query);
                    product.setAdapter( eggAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);

                    return false;
                }
                else if(categoryID == 2) // chicken
                {

                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();

                    product.setAdapter( chickenAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                else if(categoryID == 3) // pigs
                {
                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();

                    product.setAdapter( pigAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                else // nothing selected
                {
                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();


                }
//                Log.d(TAG, "onQueryTextSubmit: "+query);
//                GetProductsData getProductsData = new GetProductsData(SearchFragment.this,SearchFragment.this.getContext());
//                getProductsData.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {




                if(categoryID == 1) // egg
                {
                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
//                    eggAdapter.getFilter().filter(query);
//                    egglv.setAdapter( eggAdapter );
                    eggAdapter.getFilter().filter(newText);
                    product.setAdapter( eggAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);

                    return false;
                }
                else if(categoryID == 2) // chicken
                {

                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();

                    chickenAdapter.getFilter().filter(newText);

                    product.setAdapter( chickenAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                else if(categoryID == 3) // pigs
                {
                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();

                    pigAdapter.getFilter().filter(newText);

                    product.setAdapter( pigAdapter );
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                else // nothing selected
                {
                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
                    adapter.getFilter().filter(newText);

                    product.setAdapter( adapter );
                    Utility.setListViewHeightBasedOnChildren(product);
                }
                return false;
            }
        });


//        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if(categoryID == 1) // egg
//                {
//                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
//                }
//                else if(categoryID == 2) // chicken
//                {
//                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();
//                }
//                else if(categoryID == 3) // pigs
//                {
//                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();
//                }
//                else // nothing selected
//                {
//                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        } );

        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(categoryID == 1) // egg
                {
                    Toast.makeText(getActivity(), "Searching egg product...", Toast.LENGTH_SHORT).show();
                    Items c= (Items) eggAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (intent != null) {

                        intent.putExtra("brand", c.getBrand());
                        intent.putExtra("type", c.getType());
                        intent.putExtra("accreditation", c.getAccreditation());
                        intent.putExtra("rating", c.getRating());
                        intent.putExtra("location", c.getAvailable());

//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivity( intent );
                        getActivity().finish();

                    }
                }
                else if(categoryID == 2) // chicken
                {

                    Items c= (Items) chickenAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (intent != null) {

                        intent.putExtra("brand", c.getBrand());
                        intent.putExtra("type", c.getType());
                        intent.putExtra("accreditation", c.getAccreditation());
                        intent.putExtra("rating", c.getRating());
                        intent.putExtra("location", c.getAvailable());

//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivity( intent );
                        getActivity().finish();

                    }
                    Toast.makeText(getActivity(), "Searching chicken product...", Toast.LENGTH_SHORT).show();
                }
                else if(categoryID == 3) // pigs
                {
                    Items c= (Items) pigAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (intent != null) {

                        intent.putExtra("brand", c.getBrand());
                        intent.putExtra("type", c.getType());
                        intent.putExtra("accreditation", c.getAccreditation());
                        intent.putExtra("rating", c.getRating());
                        intent.putExtra("location", c.getAvailable());

//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivity( intent );
                        getActivity().finish();

                    }
                    Toast.makeText(getActivity(), "Searching pig product...", Toast.LENGTH_SHORT).show();
                }
                else // nothing selected
                {
                    Toast.makeText(getActivity(), "Please select the category before searching!", Toast.LENGTH_SHORT).show();
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
