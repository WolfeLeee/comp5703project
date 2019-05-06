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
    MaterialSearchBar materialSearchBar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchAdapter adapter;
    private StoreDatabase storedatabase;
    SearchView searchView;
    ListView listView;
    ListAdapter listAdapter;


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

//        recyclerView = mView.findViewById(R.id.recycler_search);
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);

        //Request Runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        fragmentreport = new ReportFragment();
        storedatabase = new StoreDatabase(getActivity());
        fragmentreportaddress = new ReportAddressFragment();

        ArrayList<String> NearbyList = new ArrayList<>();


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
                        .replace(R.id.fragment_container, fragmentreport).commit();

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String brandName = searchView.getQuery().toString();
                ArrayList<String> locationlist = storedatabase.getAddress(brandName);
                List<Address> addressList = null;
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
                            float distance = getDistance(lat, lng);

                            if(distance <= 2000){

                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                                NearbyMarker =  mMap.addMarker(markerOptions);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                NearbyList.add(location);

                                listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, NearbyList);
                                listView.setAdapter(listAdapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


                                    }
                                });

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



    // calculate the distance between two locations
    public float getDistance(double lat,double lng) {

        Location current = new Location("");
        current.setLatitude(mLastLocation.getLatitude());// current latitude
        current.setLongitude(mLastLocation.getLongitude());// current Longitude

        Location selected = new Location("");
        selected.setLatitude(lat);
        selected.setLongitude(lng);

        return current.distanceTo(selected);
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
                        if(mGoogleApiClient == null)
                            buildGoogleApiClienr();
                        mMap.setMyLocationEnabled(true);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);

        if(mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);


    }
}
