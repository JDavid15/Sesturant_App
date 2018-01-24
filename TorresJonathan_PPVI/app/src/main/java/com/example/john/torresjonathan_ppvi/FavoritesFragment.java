package com.example.john.torresjonathan_ppvi;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class FavoritesFragment extends ListFragment {

    //Hold all Restaurant Data
    private ArrayList<SavedRestaurantsObject> savedRestaurants = new ArrayList<>();

    public static FavoritesFragment newInstance() {

        Bundle args = new Bundle();
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Inflate a new View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container,false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get and set the ListView with an Custom adapter for the views
        savedRestaurants = RestaurantStorageUtill.loadLocations(getActivity());

        ListView listView = getListView();
        FavoritesBaseAdapter adapter = new FavoritesBaseAdapter(getActivity(), savedRestaurants);
        listView.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Go open an information activity with the desired restaurant
        Intent toFav = new Intent(getActivity(), InformationActivity.class);
        toFav.putExtra("ID", savedRestaurants.get(position).resId);
        toFav.putExtra("fav", true);
        startActivityForResult(toFav, 0);

    }

    //Refresh the list view
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Get and set the ListView with an Custom adapter for the views
        savedRestaurants = RestaurantStorageUtill.loadLocations(getActivity());

        ListView listView = getListView();
        FavoritesBaseAdapter adapter = new FavoritesBaseAdapter(getActivity(), savedRestaurants);
        listView.setAdapter(adapter);

    }
}
