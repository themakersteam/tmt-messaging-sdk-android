package com.tmt.livechat.screens.abstraction.activity;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tmt.livechat.R;
import com.tmt.livechat.screens.abstraction.mvp.MvpActivity;
import com.tmt.livechat.utils.LocationUtils;

/**
 * Created by mohammednabil on 2019-09-18.
 */
public abstract class MapsActivity extends MvpActivity implements OnMapReadyCallback {

    private View mapView;
    protected GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    protected double latitude;
    protected double longitude;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    protected String geocodeLocation;

    private final int PLAY_SERVICES_REQUEST = 1000;
    private final int REQUEST_CHECK_SETTINGS = 2000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        loadMapView();
        buildGoogleApiClient();
    }

    public abstract void onLocationText(String text);

    protected void setup() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        createLocationRequest();
    }

    private void loadMapView() {
        try {
            if (checkPlayServices()) {
                if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();

                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    layoutParams.setMargins(0, getValueInDp(this, 60), getValueInDp(this, 30), 0);

                    if (mGoogleMap != null) {
                        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                            @Override
                            public void onCameraMoveStarted(int i) {

                            }
                        });

                        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                LatLng position = mGoogleMap.getCameraPosition().target;
                                Location location = new Location("");
                                if (position != null) {
                                    location.setLatitude(position.latitude);
                                    location.setLongitude(position.longitude);
                                    showGeoLocation(location);
                                }
                            }
                        });
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        LocationUtils.setCameraToReceivedLocation(mGoogleMap, latitude, longitude);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private int getValueInDp(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    protected synchronized void buildGoogleApiClient() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    getLocation();
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    private void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
            else {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    mCurrentLocation = location;
                                    showGeoLocation(mCurrentLocation);
                                    LocationUtils.setCameraToReceivedLocation(mGoogleMap, latitude, longitude);
                                }
                            }
                        });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void createLocationRequest() {
        try {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showGeoLocation(Location location) {
        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            geocodeLocation = LocationUtils.getAddress(this, latLng);

            if(mCurrentLocation == null) {
                onLocationText(getString(R.string.message_center_send_current_location));
                return;
            }

            if(mCurrentLocation.distanceTo(location) <= 15) {
                onLocationText(getString(R.string.message_center_send_current_location));
            } else {
                onLocationText(getString(R.string.message_center_send_your_location));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getLocation();

                    } else {
                        Toast.makeText(this, "Please grant location permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
