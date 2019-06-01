package comp5703.sydney.edu.au.kinderfoodfinder;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.google.android.gms.maps.model.CameraPosition;
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
import comp5703.sydney.edu.au.kinderfoodfinder.Database.StoreDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.Remote.IGoogleAPIService;


public class LocateFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;

    private double latitude, longitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker, NearbyMarker;
    private Fragment fragmentreport, fragmentreportaddress;
    private LocateAdapter locateAdapter;
    private List<LocateItem> itemList = new ArrayList<>();


    IGoogleAPIService mService;

    ImageView add_report;
    TextView range;
    SearchAdapter adapter;
    private StoreDatabase storedatabase;
    StoreHelper storeHelper;
    SearchView searchView;
    ListView listView;
    NearbyAdapter listAdapter;
    ArrayAdapter ListAdapter;
    int Locate_key, Distance_key;
    String Brand;
    Button twenty, five, ten, fifty;
    LinearLayoutManager layoutManager;
    MaterialSearchBar materialSearchBar;
    Spinner dropdown;

    List<String> suggestList = new ArrayList<>();


    // Keys for storing activity state.
    private static final int MY_PERMISSION_CODE = 1000;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;

    public LocateFragment() {
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        final View mView = inflater.inflate(R.layout.fragment_locate, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // 最初距离设为1000m
        Distance_key = 1000;
        mService = Common.getGoogleAPIService();
        range = mView.findViewById(R.id.range);
        add_report = mView.findViewById(R.id.add_report);
        listView = mView.findViewById(R.id.listview_search);

        //get the spinner of the distance.
        dropdown = mView.findViewById(R.id.distance);
        final String[] distances = new String[]{"1 km", "5 km", "10 km", "20 km", "50 km"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, distances);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        //设置distance的dropdown list
        dropdown.setClickable(false);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int index = parent.getSelectedItemPosition();
                if (distances[index].equals("1 km")) {
                    Distance_key = 1000;
                } else if (distances[index].equals("5 km")) {
                    Distance_key = 5000;
                } else if (distances[index].equals("10 km")) {
                    Distance_key = 10000;
                } else if (distances[index].equals("20 km")) {
                    Distance_key = 20000;
                } else if (distances[index].equals("50 km")) {
                    Distance_key = 50000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //从detail page 传brand name
        if (getArguments() != null) {
            Locate_key = getArguments().getInt("LOCATE_KEY");
            Brand = getArguments().getString("Brand");
        } else {
            Log.d("key", "getArgument is null");
        }

        fragmentreport = new ReportFragment();
        storeHelper = new StoreHelper(getActivity());
        fragmentreportaddress = new ReportAddressFragment();


        // Setup search bar
        materialSearchBar = mView.findViewById(R.id.search_bar);
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        locateAdapter = new LocateAdapter(inflater);

        materialSearchBar.setHint("Search Brand Name...");
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.setMaxSuggestionCount(10);

        //加载search bar的dropdown list里的数据
        loadSuggestList();

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + materialSearchBar.getText());

                /* filter输入框文字，输入框文字会自动从suggestList里filter每个search，
                如果符合就加进suggest列表，然后设置suggest为新的dropdown list */
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().startsWith(materialSearchBar.getText().toLowerCase()));
                    suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                /*当search提交时，会执行startSearch（） function，
                检索数据库，在地图上定位*/
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        // Jump to Report Page
        add_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        return mView;
    }

    //加载dropdown list，从store数据库搜索所有brand
    private void loadSuggestList() {
        suggestList = storeHelper.getLocateItem();
        materialSearchBar.setLastSuggestions(suggestList);
    }

    private void startSearch(String text) {

        //根据输入信息，从数据库搜索这个brand name
        String BrandName = storeHelper.getBrand(text);
        if (BrandName == null) {
            Toast.makeText(getActivity(), "Do not find the store :(", Toast.LENGTH_SHORT).show();
        }

        //根据brand name，从数据库搜索改brand下所有的address信息
        ArrayList<String> locationlist = storeHelper.getAddress(BrandName);
        List<Address> addressList = null;
        ArrayList<Nearbydistance> DistanceList = new ArrayList<>();
        ArrayList<String> NearbyList = new ArrayList<>();
        mMap.clear();

        for (int i = 1; i < locationlist.size(); i++) {

            //从address列表里一个一个确定它的location
            String location = locationlist.get(i);
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //从而获取到latitude、longitude
            if (addressList != null) {
                for (int j = 0; j < addressList.size(); j++) {
                    final Address address = addressList.get(j);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();
                    final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    int distance = getDistance(lat, lng);

                    //计算与current location的距离，如果符合就在map上定位
                    if (distance <= Distance_key) {

                        //画distance circle
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        circleOptions.radius(Distance_key);
                        circleOptions.fillColor(0x150099ff);
                        circleOptions.strokeWidth(3);
                        circleOptions.strokeColor(0x150099ff);
                        mMap.addCircle(circleOptions);

                        //在符合标准的location加marker
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                        NearbyMarker = mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                        //加载store information的list
                        Nearbydistance near = new Nearbydistance(BrandName, location, distance);
                        DistanceList.add(near);

                        listAdapter = new NearbyAdapter(getContext(), DistanceList);
                        listView.setAdapter(listAdapter);

                        //给store list排序（但这个好像并没有效果...）
                        Collections.sort(DistanceList, new Comparator<Nearbydistance>() {
                            @Override
                            public int compare(Nearbydistance d1, Nearbydistance d2) {
                                if (d1.getDistance() < d2.getDistance()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        listAdapter.notifyDataSetChanged();

                    }

                }
            }


        }

    }

    // calculate the distance between two locations
    public int getDistance(double lat, double lng) {

        Location current = new Location("");
        current.setLatitude(mLastLocation.getLatitude());// current latitude
        current.setLongitude(mLastLocation.getLongitude());// current Longitude

        Location selected = new Location("");
        selected.setLatitude(lat);
        selected.setLongitude(lng);

        return Math.round(current.distanceTo(selected));
        //  return approximate distance between current location and selected location;
    }

    //检查location的权限
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity(), new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity(), new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        } else
            return true;

    }

    //申请权限回调的function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClienr();
                            mMap.setMyLocationEnabled(true);
                        }
                    }


                } else
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    //加载Google map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClienr();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClienr();
            mMap.setMyLocationEnabled(true);
        }

    }

    //加载GoogleApi来获取location service
    private synchronized void buildGoogleApiClienr() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    //当GoogleApiClient连上以后
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(mGoogleApiClient.isConnected()){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }

    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //当user current location变化时，获取新的current location
    @Override
    public void onLocationChanged(Location location) {
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


        //从detail page跳转过来后，进行brand的位置定位
        if(Locate_key == 1){

            ArrayList<String> locationlist = storeHelper.getAddress(Brand);
            List<Address> addressList = null;
            ArrayList<Nearbydistance> distanceList = new ArrayList<>();
            ArrayList<String> Nearbylist = new ArrayList<>();

            for(int i = 1; i< locationlist.size(); i++){

                String loc = locationlist.get(i);
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    addressList = geocoder.getFromLocationName(loc, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList != null) {

                    for (int j = 0; j < addressList.size(); j++){
                        final Address address = addressList.get(j);
                        double lat = address.getLatitude();
                        double lng = address.getLongitude();
                        final LatLng latLn = new LatLng(address.getLatitude(), address.getLongitude());
                        int distance = getDistance(lat, lng);


                        if(distance <= Distance_key){

                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            circleOptions.radius(Distance_key);
                            circleOptions.fillColor(0x150099ff);
                            circleOptions.strokeWidth(3);
                            circleOptions.strokeColor(0x150099ff);
                            mMap.addCircle(circleOptions);

                            MarkerOptions marker = new MarkerOptions().position(latLn).title(loc);
                            NearbyMarker =  mMap.addMarker(marker);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLn, 14));

                            Nearbydistance near = new Nearbydistance(Brand, loc, distance);
                            distanceList.add(near);

                            listAdapter = new NearbyAdapter(getContext(), distanceList);
                            listView.setAdapter(listAdapter);
                            Collections.sort(distanceList, new Comparator<Nearbydistance>() {
                                @Override
                                public int compare(Nearbydistance d1, Nearbydistance d2) {
                                    if(d1.getDistance() < d2.getDistance()){
                                        return 1;
                                    }else {
                                        return 0;
                                    }
                                }
                            });


                        }

                    }
                }


            }


        }


    }




}

