package com.example.john.torresjonathan_ppvi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class InformationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //Send the restaurant's Id to the fragment
        Intent intent = getIntent();
        int id = intent.getIntExtra("ID", 0);

        if(!checkForConnection()){
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            finish();
        }else{
            getFragmentManager().beginTransaction().replace(R.id.InformationActivity_Frame,
                    InformationFragment.newInstance(id)).commit();
        }

    }

    //Set the OptionsItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        InformationFragment infoFragment = (InformationFragment)getFragmentManager().findFragmentById
                (R.id.MainActivity_Frame);
        return infoFragment.onOptionsItemSelected(item);
    }

    //Set the OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private boolean checkForConnection(){
        //If Statements to check if there is connectivity on the phone
        ConnectivityManager mgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mgr != null) {

            NetworkInfo info = mgr.getActiveNetworkInfo();

            if(info != null){

                boolean isConnected = info.isConnected();
                if (isConnected){
                    return true;
                }
            }
        }
        return false;
    }

}


