package edu.mdc.entec.north.arttracker.view.map;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import edu.mdc.entec.north.arttracker.utils.Utils;
import edu.mdc.entec.north.arttracker.contract.MapContract;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.presenter.MapPresenter;
import edu.mdc.entec.north.arttracker.view.MainActivity;
import edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment;

import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.DIRECTORY;
import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.EXTENSION;
import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.SHOWING_ART_PIECE;


public class MapFragment extends Fragment
        implements OnMapReadyCallback
                    , MapContract.View
                    , GoogleMap.OnMarkerClickListener
                    , GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapFragment";
    private static final LatLng mdcLocation = new LatLng(25.8774460, -80.2461949);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private List<ArtPieceWithArtist> artPieces;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private View view;

    private MapContract.Presenter mapPresenter;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//called when orientation changes but not when another tab is pressed
        Log.d(TAG, "-------------------------In the onCreate() method");
        mapPresenter = new MapPresenter(getContext(), this);
        super.onCreate(savedInstanceState);//other nested frags are attached and created
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        return view;
    }

    public void setPresenter(MapContract.Presenter mapPresenter) {
        this.mapPresenter = mapPresenter;
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) view.findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                ImageView imageView = (ImageView) infoWindow.findViewById(R.id.imageView4);
                imageView.setImageResource( ((ArtPieceWithArtist) marker.getTag()).getPictureID(getContext()));
                imageView.setImageBitmap(Utils.loadBitmapFromAssets(getContext(), DIRECTORY + "/" + ((ArtPieceWithArtist) marker.getTag()).getPictureID() + EXTENSION));



                return infoWindow;
            }
        });

        mMap.setOnInfoWindowClickListener(this);


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


        // Add a marker at MDC North and move the camera

        mMap.addMarker(new MarkerOptions().position(mdcLocation).title("Miami Dade College North Campus"));

        mMap.animateCamera(CameraUpdateFactory.newLatLng(mdcLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mdcLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));


        mapPresenter.start();

    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mdcLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    @Override
    public void showArtPiecesOnMap(List<ArtPieceWithArtist> artPieces) {

        this.artPieces = artPieces;

        for(ArtPieceWithArtist artPieceWithArtist: artPieces) {
            LatLng artPiece = new LatLng(artPieceWithArtist.getLatitude(), artPieceWithArtist.getLongitude());
            MarkerOptions mo = new MarkerOptions()
                    .position(artPiece)
                    .title(artPieceWithArtist.getName() + " (" + artPieceWithArtist.getYear() + ")")
                    .snippet(artPieceWithArtist.getFirstName() + " " + artPieceWithArtist.getLastName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            Marker marker = mMap.addMarker(mo);
            marker.setTag(artPieceWithArtist);
            marker.showInfoWindow();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        GalleryFragment galleryFragment = (GalleryFragment) ((MainActivity) getActivity()).getAdapter().getItem(0);
        galleryFragment.setShowingList(false);
        galleryFragment.setShowing(SHOWING_ART_PIECE);
        galleryFragment.setArtPiece((ArtPieceWithArtist) marker.getTag());

        TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabhost.getTabAt(0).select();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.isInfoWindowShown())
            marker.hideInfoWindow();
        else
            marker.showInfoWindow();
        return true;
    }
}
