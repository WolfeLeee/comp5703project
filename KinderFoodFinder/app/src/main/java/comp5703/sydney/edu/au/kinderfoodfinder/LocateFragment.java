package comp5703.sydney.edu.au.kinderfoodfinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.Adapter.SearchAdapter;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.Remote.IGoogleAPIService;


public class LocateFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    // defined variables
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private double latitude, longitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker, NearbyMarker;
    private Fragment fragmentreport, fragmentreportaddress;
    private LocateAdapter locateAdapter;

    IGoogleAPIService mService;

    ImageView add_report;
    TextView range;
    SearchAdapter adapter;
    StoreHelper storeHelper;
    ListView listView;
    NearbyAdapter listAdapter;
    int Locate_key, Distance_key;
    String currentBrandInput;
    String Brand;
    MaterialSearchBar materialSearchBar;
    Spinner dropdown;

    List<String> suggestList = new ArrayList<>();

    // Keys for storing activity state.
    public static final int MY_PERMISSION_CODE = 99;

    public LocateFragment()
    {
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_locate, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init Service
        Distance_key = 1000;
        currentBrandInput = "";
        mService = Common.getGoogleAPIService();
        range = mView.findViewById(R.id.range);
        add_report = mView.findViewById(R.id.add_report);
        listView = mView.findViewById(R.id.listview_search);
        fragmentreport = new ReportFragment();
        storeHelper = new StoreHelper(getActivity());
        fragmentreportaddress = new ReportAddressFragment();

        //get the spinner of the distance.
        dropdown = mView.findViewById(R.id.distance);
        final String[] distances = new String[]{"1 km", "5 km", "10 km", "20 km", "50 km"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        //设置distance的dropdown list
        dropdown.setClickable(false);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int index = parent.getSelectedItemPosition();

                if (distances[index].equals("1 km"))
                {
                    if (Distance_key != 1000)
                    {
                        Distance_key = 1000;

                        if(Locate_key == 1)
                        {
                            new startSearch().execute(Brand);

                        }else{

                            new startSearch().execute(currentBrandInput);
                        }
                    }
                }
                else if (distances[index].equals("5 km"))
                {
                    if (Distance_key != 5000)
                    {
                        Distance_key = 5000;

                        if(Locate_key == 1)
                        {
                            new startSearch().execute(Brand);

                        }else{

                            new startSearch().execute(currentBrandInput);
                        }

                    }
                }
                else if (distances[index].equals("10 km"))
                {
                    if (Distance_key != 10000)
                    {
                        Distance_key = 10000;

                        if(Locate_key == 1)
                        {
                            new startSearch().execute(Brand);

                        }else{

                            new startSearch().execute(currentBrandInput);
                        }
                    }
                }
                else if (distances[index].equals("20 km"))
                {
                    if (Distance_key != 20000)
                    {
                        Distance_key = 20000;

                        if(Locate_key == 1)
                        {
                            new startSearch().execute(Brand);

                        }else{

                            new startSearch().execute(currentBrandInput);
                        }
                    }
                }
                else if (distances[index].equals("50 km"))
                {
                    if (Distance_key != 50000)
                    {
                        Distance_key = 50000;

                        if(Locate_key == 1)
                        {
                            new startSearch().execute(Brand);

                        }else{

                            new startSearch().execute(currentBrandInput);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // get the passed key from brand detail page
        if (getArguments() != null)
        {
            Locate_key = getArguments().getInt("LOCATE_KEY");
            Brand = getArguments().getString("Brand");
        }
        else
        {
            Log.d("key", "getArgument is null");
        }

        // Setup material search bar and its custom adapter
        materialSearchBar = mView.findViewById(R.id.search_bar);
        suggestList = storeHelper.getLocateItem();
        materialSearchBar.setLastSuggestions(suggestList);

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener()
        {
            @Override
            public void OnItemClickListener(int position, View v)
            {
                materialSearchBar.setText(materialSearchBar.getLastSuggestions().get(position).toString());
            }

            @Override
            public void OnItemDeleteListener(int position, View v)
            {
                Toast.makeText(getActivity(), "You can't do that!", Toast.LENGTH_SHORT).show();
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                Log.d("searchBar", "Search input is changed to: " + materialSearchBar.getText());

                // send the entered text to our filter and let it manage everything
                if (materialSearchBar.getText().toLowerCase().trim().equals(""))
                {
                    materialSearchBar.setLastSuggestions(suggestList);
                }
                else
                {
                    List<String> suggest = new ArrayList<>();
                    for (String search : suggestList)
                    {
                        if (search.toLowerCase().startsWith(materialSearchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    materialSearchBar.setLastSuggestions(suggest);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener()
        {
            @Override
            public void onSearchStateChanged(boolean enabled)
            {

            }

            @Override
            public void onSearchConfirmed(CharSequence text)
            {
                boolean checkBrand = false;
                for(String brandName : suggestList)
                {
                    if(materialSearchBar.getText().trim().equals(brandName))
                    {
                        checkBrand = true;
                    }
                }
                if(checkBrand)
                {
                    Log.d("searchBar", "Search the selected brand: " + materialSearchBar.getText());
                    currentBrandInput = materialSearchBar.getText();
                    materialSearchBar.disableSearch();
                    new startSearch().execute(text.toString());
                }
                else
                {
                    Toast.makeText(getActivity(), "Please select a brand in suggestions!", Toast.LENGTH_SHORT).show();

                    List<String> resetList = storeHelper.getLocateItem();
                    materialSearchBar.setLastSuggestions(resetList);
                    materialSearchBar.disableSearch();
                    Log.d("searchConfirmed", Integer.toString(materialSearchBar.getLastSuggestions().size()));
                }
            }

            @Override
            public void onButtonClicked(int buttonCode)
            {

            }
        });

        // Jump to Report Page
        add_report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle = new Bundle();
                bundle.putInt("key", 1);
                fragmentreport.setArguments(bundle);
                fragmentreportaddress.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentreport)
                        .addToBackStack(null).commit();
            }
        });

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        return mView;
    }

    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public boolean checkLocationPermission()
    {
        Log.d("checkLocationPermission", "YES!");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
            }
            Log.d("checkLocationPermission", "YES, false");
            return false;
        }
        else
        {
            Log.d("checkLocationPermission", "YES, true");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d("onRequestPermissions", "YES!");
        if(requestCode == MY_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("onRequestPermissions", "YES, check if mGoogleApiClient is null?");
                    if (mGoogleApiClient == null)
                    {
                        Log.d("onRequestPermissions", "YES, null");
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
            }
            else
                Toast.makeText(getActivity(), "Permission denied, go to settings and give app permission for loading map.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Request code is not matched!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    //asyncTask for search features
    private class startSearch extends AsyncTask<String, String, ArrayList<String>>
    {
        Context context;
        Toast loading = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // clear map
            mMap.clear();

            // msg to show user that it starts to search
            loading.setText("Please wait, searching...");
            loading.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings)
        {
            // get the input brand name
            String input = strings[0];
            Log.d("AsyncTaskInput", "Input: " + input);

            // get all the addresses based on the input brand name
            ArrayList<String> locationList = storeHelper.getAddress(input);
            Log.d("AsyncTaskLocList", "List: " + locationList);

            // init some lists
            ArrayList<Nearbydistance> distanceList = new ArrayList<>();
            ArrayList<String> locInfoList = new ArrayList<>();

            // process the locations and show them on map
            for (String location : locationList)
            {
                Geocoder geocoder = new Geocoder(getActivity());
                if(geocoder.isPresent())
                {
                    try
                    {
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        Address address = addressList.get(0);
                        double lat = address.getLatitude();
                        double lng = address.getLongitude();
                        int distance = getDistance(lat, lng);

                        if (distance <= Distance_key)
                        {
                            String str = address.getLatitude() + "," + address.getLongitude() + "," + location;
                            locInfoList.add(str);

                            Nearbydistance near = new Nearbydistance(input, location, distance);
                            distanceList.add(near);
                        }

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            Collections.sort(distanceList);
            listAdapter = new NearbyAdapter(getActivity(), distanceList);
            Log.d("AsyncTaskDibDisList", "List: " + distanceList);
            return locInfoList;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate( values );
        }

        @Override
        protected void onPostExecute(ArrayList<String> locInfoLst)
        {
            Log.d("AsyncTaskPeDisList", "Run: YES!");

            // draw circle based on the current location
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            circleOptions.radius(Distance_key);
            circleOptions.fillColor(0x150099ff);
            circleOptions.strokeWidth(3);
            circleOptions.strokeColor(0x150099ff);
            mMap.addCircle(circleOptions);

            // draw the markers of each store on map
            for (String str : locInfoLst)
            {
                String[] locInfoSplit = str.split(",");
                LatLng latLng = new LatLng(Double.parseDouble(locInfoSplit[0]), Double.parseDouble(locInfoSplit[1]));
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(locInfoSplit[2]);
                NearbyMarker = mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }

            // display the list view for stores
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

            // msg to show user that the result coming out
            loading.setText("Searching completed!");
            loading.show();
        }
    }

    // calculate the distance between two locations
    public int getDistance(double lat, double lng)
    {
        Location current = new Location("");
        current.setLatitude(mLastLocation.getLatitude());  // current latitude
        current.setLongitude(mLastLocation.getLongitude());  // current Longitude

        Location selected = new Location("");
        selected.setLatitude(lat);
        selected.setLongitude(lng);

        //  return approximate distance between current location and selected location;
        return Math.round(current.distanceTo(selected));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnectionSuspended(int i)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    //当user current location变化时，获取新的current location
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

        if(mMarker != null)
            mMarker.remove();

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // get current location
        LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(markerOptions);

        //Move Camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        if(mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


        if(Locate_key == 1)
        {
            new startSearch().execute(Brand);

        }
    }

}

