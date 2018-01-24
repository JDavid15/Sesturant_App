package com.example.john.torresjonathan_ppvi;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class InformationFragment extends Fragment implements View.OnClickListener {

    private static final String RESID_NUMBER_KEY = "ID_KEY";
    private boolean fav = false;


    public static final String RES_DATA = "com.example.john.torresjonathan_ppvi_UPDATE_DATA";
    private UpdateData resInfoBroadcast;

    private ImageView res_PhotoIV;
    private TextView res_nameTV;
    private TextView res_cuisinesTV;
    private TextView res_priceTV;
    private TextView res_averageCostTV;
    private TextView res_ratingTV;
    private TextView res_address;
    private Button addToFavBtn;

    private String res_lat;
    private String res_lng;
    private String menuUrl;
    private String resName;
    private byte[] resImage;
    private String resCuisine;
    private String resAddress;
    private int res_id;

    public static InformationFragment newInstance(int ID) {

        Bundle args = new Bundle();

        InformationFragment fragment = new InformationFragment();
        args.putInt(RESID_NUMBER_KEY, ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);


        if(!checkForConnection()){
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        //Set all view and their actions
        Button gpsStartBtn = (Button) getActivity().findViewById(R.id.gpsStart_btn);
        gpsStartBtn.setOnClickListener(this);

        Button showMenuBtn = (Button) getActivity().findViewById(R.id.menuWeb_btn);
        showMenuBtn.setOnClickListener(this);

        addToFavBtn = (Button)getActivity().findViewById(R.id.addFav_btn);
        addToFavBtn.setOnClickListener(this);

        res_PhotoIV = (ImageView)getActivity().findViewById(R.id.res_Photo);
        res_nameTV = (TextView)getActivity().findViewById(R.id.res_Name);
        res_cuisinesTV = (TextView)getActivity().findViewById(R.id.res_Cuisines);
        res_priceTV = (TextView)getActivity().findViewById(R.id.res_PriceRange);
        res_averageCostTV = (TextView)getActivity().findViewById(R.id.res_CosForTwo);
        res_ratingTV = (TextView)getActivity().findViewById(R.id.res_Rating);
        res_address = (TextView)getActivity().findViewById(R.id.res_Address);
        int id_key = getArguments().getInt(RESID_NUMBER_KEY);
        res_id = id_key;

        fav = RestaurantStorageUtill.isFavorite(getActivity(), res_id);

        //if the restaurant was marked as favorite. Display it and change the button description
        if(fav){
            addToFavBtn.setText(R.string.removeFav_BtnText);
        }

        //Start the task to download restaurant's data
        RestaurantInfoTask resData = new RestaurantInfoTask(getActivity(), res_PhotoIV);
        resData.execute(String.valueOf(id_key));

    }

    //Broadcast to update the UI with information
    public class UpdateData extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null){
                //noinspection unchecked
                RestaurantInfoObject resObject =
                        (RestaurantInfoObject)intent.getSerializableExtra("restaurantInfo");

                //Load all data to all views

                String currencySymbol = resObject.res_currency;
                int priceNumber = resObject.res_price;

                String resPriceString = "Price Range: ";

                for (int i = 0; i < priceNumber; i++) {
                    resPriceString = resPriceString + currencySymbol;
                }
                res_lat = resObject.resLat;
                res_lng = resObject.resLng;
                res_nameTV.setText(resObject.res_name);
                resName = resObject.res_name;
                res_cuisinesTV.setText(resObject.res_cuisines);
                resCuisine = resObject.res_cuisines;
                res_priceTV.setText(resPriceString);
                res_averageCostTV.setText("Average cost for Two:" + " " + currencySymbol + resObject.res_averageCost);
                res_ratingTV.setText("Rating: " + " " +resObject.res_rating + "/5.0");
                res_address.setText("Address:" + " " + resObject.res_address);
                resAddress = resObject.res_address;
                menuUrl = resObject.resMenuUrl;

                if(res_PhotoIV.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)res_PhotoIV.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    resImage = stream.toByteArray();
                }
            }
        }
    }

    //on Pause, unregister broadcaster

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(resInfoBroadcast);
    }

    //on Resume, Set the broadcaster and register it
    @Override
    public void onResume() {
        super.onResume();

        resInfoBroadcast = new UpdateData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RES_DATA);
        getActivity().registerReceiver(resInfoBroadcast, intentFilter);

    }

    @Override
    public void onClick(View v) {

        //Start the GPS Track
        if(v.getId() == R.id.gpsStart_btn){
            Intent gps = new Intent(Intent.ACTION_VIEW);
            gps.setData(Uri.parse("google.navigation:q=" + res_lat + "," + res_lng));
            startActivity(gps);
            //Start a webpage with the restaurant's menu
        }else if(v.getId() == R.id.menuWeb_btn){
             Uri uri = Uri.parse(menuUrl);
             Intent intent = new Intent(Intent.ACTION_VIEW, uri);
             startActivity(intent);
            //add the restaurant to the favorite list
        }else if (v.getId() == R.id.addFav_btn){

            if(fav){
                RestaurantStorageUtill.deleteRestaurant(getActivity(),
                        new SavedRestaurantsObject(resName, res_id, resImage, resCuisine, resAddress));
                addToFavBtn.setText(R.string.add_to_favorites);
                fav = false;
                Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();

            }else{
                RestaurantStorageUtill.saveRestaurant(getActivity(),
                        new SavedRestaurantsObject(resName, res_id, resImage, resCuisine, resAddress));
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                fav = true;
                addToFavBtn.setText(R.string.removeFav_BtnText);
            }
        }

    }


    private boolean checkForConnection(){
        //If Statements to check if there is connectivity on the phone
        ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
