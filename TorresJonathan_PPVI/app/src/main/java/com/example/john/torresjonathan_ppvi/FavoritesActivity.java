package com.example.john.torresjonathan_ppvi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //Launch Fragment
        getFragmentManager().beginTransaction().replace(R.id.FavoritesActivity_Frame,
                FavoritesFragment.newInstance()).commit();

    }
}
