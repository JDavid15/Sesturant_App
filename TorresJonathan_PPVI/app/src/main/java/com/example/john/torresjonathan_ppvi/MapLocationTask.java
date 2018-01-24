package com.example.john.torresjonathan_ppvi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


 class MapLocationTask extends AsyncTask<String,Void,String> {


     private final Context mContext;

     private ProgressDialog mProgressDialog;

     private boolean restaurantsFound = true;

     MapLocationTask(Context context){
        mContext = context;}

    private final ArrayList<RestaurantLocationObject> JSONLocationsFound = new ArrayList<>();

    @Override
    protected String doInBackground(String... params) {

        if(!checkForConnection()){
            return null;
        }else{

            String data = getNetworkData(params[0]);

            try{
                //receive the data from the Json file
                JSONObject outerJson = new JSONObject(data);
                Log.d("restaurants", outerJson.toString());

                JSONArray restaurants = outerJson.getJSONArray("restaurants");

                if(restaurants.length() == 0){
                    restaurantsFound = false;
                }else{

                for (int i = 0; i < restaurants.length(); i++) {

                    JSONObject innerJsonObject = restaurants.getJSONObject(i);

                    JSONObject restaurantJSON = innerJsonObject.getJSONObject("restaurant");

                    String resName = restaurantJSON.getString("name");

                    JSONObject idJSON = restaurantJSON.getJSONObject("R");

                    int resId = idJSON.getInt("res_id");

                    Log.d("resName", resName);

                    JSONObject locationJSON = restaurantJSON.getJSONObject("location");

                    String resLat = locationJSON.getString("latitude");
                    String resLng = locationJSON.getString("longitude");

                    int resAvergCost = restaurantJSON.getInt("average_cost_for_two");

                    JSONLocationsFound.add(new RestaurantLocationObject(resName,
                            Double.parseDouble(resLat), Double.parseDouble(resLng), resId, resAvergCost));
                    }
                }

            }catch (JSONException e){
                e.printStackTrace();

            }

        }
        return null;
    }
     @Override
     protected void onPreExecute() {
         super.onPreExecute();

         if(checkForConnection()){
             // Create a Progress Dialog
             mProgressDialog = new ProgressDialog(mContext);
             mProgressDialog.setTitle("Looking for nearby Restaurants");
             mProgressDialog.setMessage("Please Wait...");
             mProgressDialog.setIndeterminate(false);
             mProgressDialog.show();
         }else{
             Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_LONG).show();

         }


     }


     @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

         if(checkForConnection()) {
             Intent update = new Intent();
             update.setAction(MainMapFragment.UPDATE_DATA);
             update.putExtra("allLocationsData", JSONLocationsFound);
             mContext.sendBroadcast(update);
             mProgressDialog.dismiss();

             if(!restaurantsFound){
                 Toast.makeText(mContext, R.string.noRes_Found, Toast.LENGTH_SHORT).show();
             }

         }

     }

    private String getNetworkData (String latlon){

        try{

            URL url = new URL("https://developers.zomato.com/api/v2.1/search?" + latlon + "&radius=1000");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-key","a4c797e59ba9ad8c7fd14c51e35aefdc");
            connection.setDoOutput(true);

            connection.connect();

            InputStream is = connection.getInputStream();

            String data = IOUtils.toString(is);

            is.close();

            connection.disconnect();

            return data;

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

     private boolean checkForConnection(){
         //If Statements to check if there is connectivity on the phone
         ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
