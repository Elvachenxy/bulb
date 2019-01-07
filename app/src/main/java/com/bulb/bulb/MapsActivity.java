package com.bulb.bulb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bulb.bulb.Models.LocationSearchResultModel;
import com.bulb.bulb.Models.PlaceDetailSearchModel;
import com.bulb.bulb.Models.ReviewsRecyclerAdapter;
import com.bulb.bulb.Models.Therapist;
import com.bulb.bulb.Models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap mMap;
    SupportMapFragment mapFrag;
    FusedLocationProviderClient mFusedLocationClient;

    Button searchButton;
    EditText searchET;
    Location currLocation;
    protected GeoDataClient mGeoDataClient;
    GoogleApiClient mGoogleApiClient;
    PlaceDetectionClient mPlaceDetectionClient;
    LocationSearchResultModel myLocationSearchResultModel;
    View infoWindowView;

    TextView infoTitleTV;
    TextView infoSubtitleTV;
    TextView infoMentionTV;
    TextView infoDetailTV;
    ConstraintLayout fragmentContainer;

    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    Marker prevMarker;
    User user;
    RequestQueue queue;
    String placeSearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    String detailSearchUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
    String api_key = "AIzaSyDn103BugowwXYYxkwq5x4BhYDlA2-DCyM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        infoWindowView = findViewById(R.id.info_view);
        infoWindowView.setAlpha(0f);
        user = (User) getIntent().getSerializableExtra("user");
        fragmentContainer = findViewById(R.id.container);
        fragmentContainer.setVisibility(View.GONE);
        Log.d("BULB", "USER:" + user);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_map);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.action_forum) {
                            Intent i = new Intent(MapsActivity.this, MainActivity.class);
                            i.putExtra("user", user);
                            startActivity(i);
                            bottomNavigationView.setSelectedItemId(R.id.action_map);
                        }
                        return true;
                    }
                });

        queue = Volley.newRequestQueue(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // TODO: Start using the Places API.

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();

        searchET = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
                queryLocation(searchET.getText());
            }
        });

        infoTitleTV = findViewById(R.id.info_window_title);
        infoSubtitleTV = findViewById(R.id.info_window_subtitle);
        infoDetailTV = findViewById(R.id.info_window_details);
        infoMentionTV = findViewById(R.id.info_window_mentions);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            currLocation = location;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location
                    .getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }



    String getQueryLocationUrl(CharSequence sequence) {
        String queryUrl = new StringBuilder()
                .append(placeSearchUrl)
                .append("location=")
                .append(currLocation.getLatitude())
                .append(",")
                .append(currLocation.getLongitude())
                .append("&radius=1500&type=doctor&keyword=")
                .append(sequence)
                .append("&key=")
                .append(api_key)
                .toString();

        return queryUrl;
    }

    String getPlaceDetailUrl(String placeId) {
        String queryUrl = new StringBuilder()
                .append(detailSearchUrl)
                .append("placeid=")
                .append(placeId)
                .append("&fields=formatted_address,formatted_phone_number,website,name")
                .append("&key=")
                .append(api_key)
                .toString();
        return queryUrl;
    }

    void queryLocation(CharSequence sequence) {
        String requestUrl = getQueryLocationUrl(sequence);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LocationSearchResultModel locationSearchResultModel =
                                new Gson().fromJson(response, LocationSearchResultModel.class);
                        if (locationSearchResultModel.getResults().size() == 0) {
                            displayErrorMessage("Network Error");
                        } else {
                            myLocationSearchResultModel = locationSearchResultModel;
                            displayMarkers(myLocationSearchResultModel);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Network Error", Toast.LENGTH_LONG)
                     .show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    void queryPlaceDetail(String placeId, final Marker marker) {
        String requestUrl = getPlaceDetailUrl(placeId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PlaceDetailSearchModel placeDetailSearchModel =
                                new Gson().fromJson(response, PlaceDetailSearchModel.class);
                        if (placeDetailSearchModel.getResults() == null) {
                            displayErrorMessage("Network Error");
                        } else {
                            String website = "";
                            if (placeDetailSearchModel.getResults().getWebsite() == null && placeDetailSearchModel.getResults().getUrl() != null) {
                                website += "Website: " + placeDetailSearchModel.getResults().getUrl();
                            } else if (placeDetailSearchModel.getResults().getWebsite() != null) {
                                website += "Website: " + placeDetailSearchModel.getResults().getWebsite();
                            }

                            String details = "Phone: "
                                    + placeDetailSearchModel.getResults().getPhone()
                                    + "\nLocation: "
                                    + placeDetailSearchModel.getResults().getAddress()
                                    + "\n"
                                    + website;
                            infoSubtitleTV.setText(details);
                            infoTitleTV.setText(placeDetailSearchModel.getResults().getName());

                            String path = "therapists/" + placeDetailSearchModel.getResults().getName().replaceAll(" ", "_").replaceAll("[.]","").toLowerCase();
                            StorageReference therapistReference = storage.getReference(path);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            therapistReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Therapist therapist = SerializationUtils.deserialize(bytes);
                                    Log.d("Bulb", "Retrieved therapist");
                                    if (therapist.reviews.size() > 0) {
                                        infoMentionTV.setText("mentioned by " + therapist.reviews.size() + " users");
                                    } else {
                                        infoMentionTV.setText(R.string.no_mentions);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    infoMentionTV.setText(R.string.no_mentions);
                                }
                            });

                            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                            animation.setDuration(300);
                            animation.setFillAfter(true);
                            infoWindowView.setAlpha(1f);
                            infoWindowView.startAnimation(animation);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Network Error", Toast.LENGTH_LONG)
                     .show();
            }
        });

        queue.add(stringRequest);
    }

    void displayErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void displayMarkers(LocationSearchResultModel locationSearchResultModel) {
        LatLng location = new LatLng(0, 0);
        Double lat;
        Double lng;
        LocationSearchResultModel.ResultModel result;
        mMap.clear();
        for (int i = 0; i < locationSearchResultModel.getResults().size(); i++) {
            result = locationSearchResultModel.getResults().get(i);
            LocationSearchResultModel.ResultModel.GeometryModel.LocationModel loc
                    = result.getGeometry().getLocation();
            lat = Double.valueOf(loc.getLatitude());
            lng = Double.valueOf(loc.getLongitude());
            location = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(result.getName())
                    .snippet(result.getPlaceId())
            );
        }

        LocationSearchResultModel.ResultModel.GeometryModel.LocationModel closest = locationSearchResultModel.getResults().get(0).getGeometry().getLocation();
        Double closestLat = Double.valueOf(closest.getLatitude());
        Double closestLng = Double.valueOf(closest.getLongitude());
        LatLng closestLatLng = new LatLng(closestLat, closestLng);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(closestLatLng)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public String getInsurances(ArrayList<String> insurances) {
        String insurancesString = "";
        for(String insurance : insurances) {
            if(!insurance.equals("") && !insurance.equals(" ")) {
                insurancesString = insurancesString + insurance.replaceAll(" ", "") + ", ";
            }
        }
        return insurancesString.length() > 3 ? insurancesString.substring(0, insurancesString.length()-2) : "None listed so far.";
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String placeId = marker.getSnippet();
        queryPlaceDetail(placeId, marker);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
        if (prevMarker != null) {
            //Set prevMarker back to default color
            prevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        //leave Marker default color if re-click current Marker
        if (!marker.equals(prevMarker)) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            prevMarker = marker;
        }
        prevMarker = marker;
        return true;
    }

    public void onClickViewDetails(View view) {
        FragmentManager manager = getSupportFragmentManager();
        fragmentContainer.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchET.setVisibility(View.GONE);
        final MapTherapistDetailActivity detailFragment = new MapTherapistDetailActivity();
        manager.beginTransaction().replace(R.id.container, detailFragment, detailFragment.getTag()).commit();
        getSupportFragmentManager().executePendingTransactions();
        detailFragment.nameTV.setText(infoTitleTV.getText());
        detailFragment.detailTV.setText(infoSubtitleTV.getText());
        detailFragment.exitButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentContainer.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                searchET.setVisibility(View.VISIBLE);
            }
        });
        String path = "therapists/" + infoTitleTV.getText().toString().replaceAll(" ", "_").replaceAll("[.]","").toLowerCase();
        StorageReference therapistReference = storage.getReference(path);

        final long ONE_MEGABYTE = 1024 * 1024;
        therapistReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Therapist therapist = SerializationUtils.deserialize(bytes);
                Log.d("Bulb", "Retrieved therapist");
                if (therapist.reviews.size() > 0) {
                    detailFragment.insuranceTV.setText((therapist.insurances != null && !therapist.insurances.isEmpty() &&!(therapist.insurances.toString().equals("[]")) ? getInsurances(therapist.insurances) : "None listed."));
                    getReviews(therapist, detailFragment);
                } else {
                    detailFragment.insuranceTV.setText("None listed.");
                    detailFragment.reviewsHeaderTV.setText("No Reviews Yet!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Bulb", "Failed to load therapist.");
                detailFragment.insuranceTV.setText("None listed.");
                detailFragment.reviewsHeaderTV.setText("No Reviews Yet!");
            }
        });
    }


    void getReviews(Therapist therapist, MapTherapistDetailActivity detailFragment) {
        detailFragment.reviewAdapter = new ReviewsRecyclerAdapter(this, therapist);
        detailFragment.recyclerView.setAdapter(detailFragment.reviewAdapter);

    }
}
