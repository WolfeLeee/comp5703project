package comp5703.sydney.edu.au.kinderfoodfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.Adapter.SearchAdapter;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.StoreDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.Model.MyPlaces;
import comp5703.sydney.edu.au.kinderfoodfinder.Model.Results;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.Remote.IGoogleAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocateFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;

    private double latitude,longitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker, NearbyMarker;
    private Fragment fragmentreport, fragmentreportaddress;

    IGoogleAPIService mService;

    ImageView add_report;
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


    // Keys for storing activity state.
    private static final int MY_PERMISSION_CODE =1000;

    public LocateFragment(){
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_locate, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init Service
        mService = Common.getGoogleAPIService();
        add_report = mView.findViewById(R.id.add_report);
        searchView = mView.findViewById(R.id.search_bar);
        listView = mView.findViewById(R.id.listview_search);
        searchView.setFocusable(false);

        twenty = mView.findViewById(R.id.twenty_km);
        five = mView.findViewById(R.id.five_km);
        ten = mView.findViewById(R.id.ten_km);
        fifty = mView.findViewById(R.id.ft_km);
        Distance_key = 1000;

        twenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Distance_key = 20000;
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Distance_key = 5000;
            }
        });

        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Distance_key = 10000;
            }
        });

        fifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Distance_key = 50000;
            }
        });

//        recyclerView = mView.findViewById(R.id.recycler_search);
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);

        //Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        if(getArguments()!=null){
            Locate_key = getArguments().getInt( "LOCATE_KEY" );
            Brand = getArguments().getString("Brand");
        }else {
            Log.d("key", "getArgument is null");
        }

        fragmentreport = new ReportFragment();
        storeHelper = new StoreHelper(getActivity());
        fragmentreportaddress = new ReportAddressFragment();

        ArrayList<String> NearbyList = new ArrayList<>();

        // click on available of the detailed activity




        // Jump to Report Page

        add_report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key",1 );
                fragmentreport.setArguments( bundle );
                fragmentreportaddress.setArguments( bundle );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentreport)
                        .addToBackStack( null ).commit();

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                 String brandName = searchView.getQuery().toString();
                 String BrandName = storeHelper.getBrand(brandName);

                ArrayList<String> locationlist = storeHelper.getAddress(brandName);
                List<Address> addressList = null;
                ArrayList<Nearbydistance> DistanceList = new ArrayList<>();
                ArrayList<String> NearbyList = new ArrayList<>();
                mMap.clear();

                for(int i = 1; i< locationlist.size(); i++){

                    String location = locationlist.get(i);
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {

                        for (int j = 0; j < addressList.size(); j++){
                            final Address address = addressList.get(j);
                            double lat = address.getLatitude();
                            double lng = address.getLongitude();
                            final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            int distance = getDistance(lat, lng);


                            if(distance <= Distance_key){

                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                                NearbyMarker =  mMap.addMarker(markerOptions);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
//                                NearbyList.add(location  + " Distance : " + distance + " m");
//                                String Distance = String.valueOf(distance);

                                Nearbydistance near = new Nearbydistance(BrandName, location, distance);
                                DistanceList.add(near);

                                listAdapter = new NearbyAdapter(getContext(), DistanceList);
                                listView.setAdapter(listAdapter);
                                Collections.sort(DistanceList, new Comparator<Nearbydistance>() {
                                    @Override
                                    public int compare(Nearbydistance d1, Nearbydistance d2) {
                                        if(d1.getDistance() < d2.getDistance()){
                                            return 1;
                                        }else {
                                            return 0;
                                        }
//                                        return d1.getDistance().compareTo(d2.getDistance());
                                    }
                                });
//                                Collections.sort(DistanceList, new Sorting());
                                listAdapter.notifyDataSetChanged();

//                                listAdapter.refresh();





//                                listView.setAdapter(listAdapter);

//                                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();


                            }

                        }
                    }


                }


//                adapter = new SearchAdapter(getActivity(),NearbyList);
//                recyclerView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return mView;
    }

    public void List(){

    }

    // calculate the distance between two locations
    public int getDistance(double lat,double lng) {

        Location current = new Location("");
        current.setLatitude(mLastLocation.getLatitude());// current latitude
        current.setLongitude(mLastLocation.getLongitude());// current Longitude

        Location selected = new Location("");
        selected.setLatitude(lat);
        selected.setLongitude(lng);

        return Math.round(current.distanceTo(selected));
        //  return approximate distance between current location and selected location;
    }




    private boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity(), new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity(), new String[]{

                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        }
        else
            return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_CODE:
            {
                if(grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(mGoogleApiClient == null){
                            buildGoogleApiClienr();
                            mMap.setMyLocationEnabled(true);}
                    }
                }
                else
                    Toast.makeText(getActivity(),"Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Init Google Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClienr();
                mMap.setMyLocationEnabled(true);
            }
        }
        else{
            buildGoogleApiClienr();
            mMap.setMyLocationEnabled(true);
        }


//        if(Locate_key ==1){
//
//            ArrayList<String> locationlist = storeHelper.getAddress(Brand);
//            List<Address> addressList = null;
//            ArrayList<Nearbydistance> distanceList = new ArrayList<>();
//            ArrayList<String> Nearbylist = new ArrayList<>();

//            for(int i = 1; i< locationlist.size(); i++){
//
//                String location = locationlist.get(i);
//                Geocoder geocoder = new Geocoder(getActivity());
//                try {
//                    addressList = geocoder.getFromLocationName(location, 10);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (addressList != null) {
//
//                    for (int j = 0; j < addressList.size(); j++){
//                        final Address address = addressList.get(j);
//                        double lat = address.getLatitude();
//                        double lng = address.getLongitude();
//                        final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        int distance = getDistance(lat, lng);
//
//
//                        if(distance <= 2000){
//                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
//                        NearbyMarker =  mMap.addMarker(markerOptions);
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
////                                NearbyList.add(location  + " Distance : " + distance + " m");
////                                String Distance = String.valueOf(distance);
//
//                        Nearbydistance near = new Nearbydistance(Brand, location, distance);
//                        distanceList.add(near);
//
//                        listAdapter = new NearbyAdapter(getContext(), distanceList);
//                        listView.setAdapter(listAdapter);
//                        Collections.sort(distanceList, new Comparator<Nearbydistance>() {
//                            @Override
//                            public int compare(Nearbydistance d1, Nearbydistance d2) {
//                                if(d1.getDistance() < d2.getDistance()){
//                                    return 1;
//                                }else {
//                                    return 0;
//                                }
//                            }
//                        });
//
//                        listAdapter.refresh();
//
//
//
//                    }
//
//                }
//                }
//
//
//            }


//        }

    }

    private synchronized void buildGoogleApiClienr() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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
//         mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//         mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        if(mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if(Locate_key ==1){

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


                        if(distance <= 5000){
                            MarkerOptions marker = new MarkerOptions().position(latLn).title(loc);
                            NearbyMarker =  mMap.addMarker(marker);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLn, 14));
//                                NearbyList.add(location  + " Distance : " + distance + " m");
//                                String Distance = String.valueOf(distance);

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
//                                        return d1.getDistance().compareTo(d2.getDistance());
                                }
                            });
//                                Collections.sort(DistanceList, new Sorting());


//                            Nearbydistance near = new Nearbydistance(location, distance);
//                            distancelist.add(near);

//                            Comparator<Nearbydistance> comparator = new Comparator<Nearbydistance>() {
//                                public int compare(Nearbydistance d1, Nearbydistance d2) {
//                                    return d1.getDistance().compareTo(d2.getDistance());
//                                }
//                            };
//                            Collections.sort(distancelist, comparator);

//                        ListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Nearbylist);
//                        listView.setAdapter(ListAdapter);
//                            ((ArrayAdapter) listAdapter).notifyDataSetChanged();


                        }

                    }
                }


            }


        }


    }


}

