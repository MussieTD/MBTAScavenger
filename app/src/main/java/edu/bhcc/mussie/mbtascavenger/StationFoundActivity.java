package edu.bhcc.mussie.mbtascavenger;

import android.*;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;

/**
 * Created by Mussie on 12/2/2017.
 */

public class StationFoundActivity extends AppCompatActivity {

    File mPhotoFile;
    Station closeStation;
    ImageView stationPhotoIv;
    Button registerStationBtn;
    public static String myCurrentLocation;
    private GoogleApiClient mClient;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
           android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private double LARGEST_DISTANCE = 5;
    private boolean prefersKM = true;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_found_activity);
        Log.i("onCreate", "yep");
        final TextView closestStationTv = (TextView) findViewById(R.id.closest_station_textView);
        stationPhotoIv = (ImageView) findViewById(R.id.station_photo_imageview);
        registerStationBtn = (Button) findViewById(R.id.register_btn);

        mClient = new
                GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            findStation();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();


        closestStationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    closestStationTv.setText("The Closest Station is: " + closeStation.getName());
                }
            }
        });


        if (myCurrentLocation != null)
         closeStation = StationsLab.get(getApplicationContext()).getClosestStation(myCurrentLocation);
        else
            closeStation = StationsLab.get(getApplicationContext()).getDummy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            closestStationTv.setText("closest Station is: " + closeStation.getName());
        }
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                6);

        mPhotoFile = (File) StationsLab.get(getApplicationContext()).getPhotoFile(closeStation);
        updatePhotoView();
        PackageManager packageManager = getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mPhotoFile != null && captureImage.resolveActivity(packageManager) != null);
        //  mPhotoButton.setEnabled(canTakePhoto);
        System.out.println("mpf: " + mPhotoFile + " cpI: " + captureImage.resolveActivity(packageManager) + " CTP: " + canTakePhoto);
        if (canTakePhoto) {

            System.out.println("Cannot take a photo mpf: " + mPhotoFile + " cpI: " + captureImage.resolveActivity(packageManager));
            Uri uri = Uri.fromFile(mPhotoFile);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); // ignores the FileUriExposedException
            StrictMode.setVmPolicy(builder.build());
           //    File file = new File(mPhotoFile);
           //   Uri photoUri = FileProvider.getUriForFile(this    ,getPackageName() + ".provider", mPhotoFile);
            captureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //     System.out.println("uri: " + uri + " Mpf: " + mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        stationPhotoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, 0);
            }
        });


        registerStationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StationsLab.get(getApplicationContext())
                        .isCloseEnough(StationsLab.get(getApplicationContext()).getClosestStationDistance(myCurrentLocation)
                                ,LARGEST_DISTANCE))
                {

                    closeStation.setVisited(true);
                    StationsLab.get(getApplicationContext()).setStationVisited(closeStation);

                    AlertDialog.Builder successDialog = new AlertDialog.Builder(StationFoundActivity.this);
                    successDialog.setTitle("Success");
                    successDialog.setMessage(" You have successfully visited " + closeStation.getName() + " Station.");
                    successDialog.show();
                }
                else
                {
                    AlertDialog.Builder tooFarDialog = new AlertDialog.Builder(StationFoundActivity.this);
                    tooFarDialog.setTitle("Problem");
                    tooFarDialog.setMessage("Hey, you seem to be " + StationsLab.get(getBaseContext()).getDistance(myCurrentLocation,prefersKM)
                            + (prefersKM ? " Km." : " Miles."));
                    tooFarDialog.show();
                    Toast.makeText(getApplicationContext(),"Station too far, please get closer",Toast.LENGTH_LONG).show();

                }


            }
        });



    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int  requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findStation();
                }
            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void findStation() {

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location locat = LocationServices.FusedLocationApi.getLastLocation(mClient);
            myCurrentLocation = locat.getLongitude() + "," +locat.getLatitude();

            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    Log.i("GPS Working", "Got a fix: " + location.getLongitude());
                    Station x = StationsLab.get(getApplication()).getClosestStation(location.getLongitude() + "," + location.getLatitude());
                Log.i("Tracking",x.getName());
                myCurrentLocation = (location.getLongitude() + "," + location.getLatitude());
                    Log.i("Tracking",x.getName() + "myCurLoca: " + myCurrentLocation);
                }

            });
        }
        else {

            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }

    }

    private boolean hasLocationPermission() {
        int result = ContextCompat
                .checkSelfPermission(this,
                        LOCATION_PERMISSIONS[0]);
        return result ==
                PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onStart() {
        super.onStart();
         mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("onActivityresult","at StationFoundAct, resultCode: " + resultCode + " requestCode: " + requestCode);

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0)
        {
            Log.i("picture ","requestCode: " + requestCode);
            updatePhotoView();

        }

    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
             stationPhotoIv.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), this);
            stationPhotoIv.setImageBitmap(bitmap);
        }
    }

}
