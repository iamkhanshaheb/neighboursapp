package com.neighbours.neighbours.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.ImageRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.neighbours.neighbours.R;
import com.neighbours.neighbours.Util.PermissionUtils;
import com.neighbours.neighbours.Util.WriteToFileUtil;
import com.neighbours.neighbours.models.SuccessResponse;
import com.neighbours.neighbours.network.RestAdapterProvider;
import com.vansuita.pickimage.IPickResult;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddPostActivity extends AppCompatActivity implements IPickResult.IPickResultBitmap, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    TextView locationName;
    TextView locationPicker;
    TextView imgPicker;
    ImageView imgPreview;
    EditText status;
    File photo;
    File userPhoto;
    Button btnSend;
    ProfileTracker profileTracker;

    String text;
    double lat;
    double lng;
    String userName;
    String userId;
    String userImageUrl;

    GoogleApiClient mGoogleApiClient;
    boolean googleApiClientConnected;
    boolean resolvingError;
    LocationRequest mLocationRequest;

    private static final String TAG = AddPostActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        locationName = (TextView) findViewById(R.id.tvAddLocationText);
        locationPicker = (TextView) findViewById(R.id.tvAddLocation);
        imgPicker = (TextView) findViewById(R.id.tvTakePhoto);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnSend = (Button) findViewById(R.id.btnPost);
        status = (EditText) findViewById(R.id.etStatus);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!resolvingError) {
            mGoogleApiClient.connect();
        }

        prepareCurrentLocationRequest();


        imgPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.on(AddPostActivity.this, new PickSetup());
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            // If there is an access token then Login Button was used
            // Check if the profile has already been fetched
            Profile currentProfile = Profile.getCurrentProfile();
            if (currentProfile != null) {
                userId = currentProfile.getId();
                userName = currentProfile.getName();
                Uri profilePictureUri = ImageRequest.getProfilePictureUri(Profile.getCurrentProfile().getId(), 200 , 200 );
                userImageUrl = profilePictureUri.toString();
            } else {
                // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
                Profile.fetchProfileForCurrentAccessToken();
            }
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                text = status.getText().toString();

                Map<String, String> params = new HashMap<>();
                params.put("text", text);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));
                params.put("user_id", userId.replace("", ""));
                params.put("user_name", userName.replace("", ""));
                params.put("user_photo_url", userImageUrl.replace("",""));

                MultipartBody.Part imagePart = null;
                if (photo != null) {
                    imagePart = MultipartBody.Part.createFormData("photo", photo.getName(), RequestBody.create(MediaType.parse("image/*"), photo));
                }

                RestAdapterProvider.getProvider().getRestApiForRetrofit().addPost(params, imagePart).enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {

                        progressDialog.hide();
                        startActivity(new Intent(AddPostActivity.this, FeedActivity.class));
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void prepareCurrentLocationRequest() {
        Log.d(TAG,"Current location picker called");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
    }


    @Override
    public void onPickImageResult(Bitmap bitmap) {
        imgPreview.setImageBitmap(bitmap);
        try {
            imgPreview.setVisibility(View.VISIBLE);;
            photo = WriteToFileUtil.writeToFile(this, bitmap, "journal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClientConnected = true;
        requestCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClientConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        googleApiClientConnected = false;

        if (!resolvingError) {
            if (result.hasResolution()) {
                try {
                    resolvingError = true;
                    result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                // Show dialog using GoogleApiAvailability.getErrorDialog()
                Log.d(TAG, "Auto location detection failed");
                resolvingError = true;
            }
        }
    }

    private void requestCurrentLocation() {
        if (PermissionUtils.hasPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            initRequest();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        AddPostActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ignored) {
                            }
                            break;
                    }
                }
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(AddPostActivity.this,
                        "Auto location detection won't work without this permission",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                ActivityCompat.requestPermissions(AddPostActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initRequest() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location loc) {
            String place = "(" + loc.getLatitude() + ", " + loc.getLongitude() + ")";

            lat = loc.getLatitude();
            lng = loc.getLongitude();
            locationName.setText(place);
        }
    };
}
