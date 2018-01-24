package com.example.john.torresjonathan_ppvi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check to permission to use location services, then run the fragment
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }else{
            getFragmentManager().beginTransaction().replace(R.id.MainActivity_Frame, MainMapFragment.newInstance()).commit();
        }

    }

    //Check if the user has given permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if(grantResults[0] == PermissionChecker.PERMISSION_DENIED){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }else{
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            getFragmentManager().beginTransaction().replace(R.id.MainActivity_Frame, MainMapFragment.newInstance()).commit();
        }
    }



    }


    //Set the OptionsItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MainMapFragment mainFragment = (MainMapFragment)getFragmentManager().findFragmentById
                (R.id.MainActivity_Frame);
        return mainFragment.onOptionsItemSelected(item);
    }

    //Set the OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {



        return true;
    }
}
