package com.example.john.torresjonathan_ppvi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


class RestaurantInfoTask extends AsyncTask<String, Void, String> {

    private final Context mContext;

    private final ImageView mImageView;

    private Bitmap mBitmap;

    private RestaurantInfoObject resInfo;

    RestaurantInfoTask(Context context, ImageView imageView){
        mContext = context;
        mImageView = imageView;
    }

    private ProgressDialog mProgressDialog;

    @Override
    protected String doInBackground(String... params) {

        if(!checkForConnection()){
            return null;
        }else {

            String data = getNetworkData(params[0]);

            try {
                JSONObject outerJson = new JSONObject(data);

                String res_name = outerJson.getString("name");
                String res_cuisines = outerJson.getString("cuisines");
                int res_price = outerJson.getInt("price_range");
                String res_currency = outerJson.getString("currency");
                int res_averageCost = outerJson.getInt("average_cost_for_two");

                JSONObject ratingsJSON = outerJson.getJSONObject("user_rating");
                String res_rating = ratingsJSON.getString("aggregate_rating");

                JSONObject locationJSON = outerJson.getJSONObject("location");
                String res_address = locationJSON.getString("address");
                String res_latitude = locationJSON.getString("latitude");
                String res_longitude = locationJSON.getString("longitude");

                String res_menuListUrl = outerJson.getString("menu_url");


                String res_photo_url = outerJson.getString("featured_image");
                mBitmap = returnImage(res_photo_url);

                Log.d("Logs", res_name + res_address + res_averageCost);

                resInfo = new RestaurantInfoObject(res_name, res_cuisines, res_price,
                        res_currency, res_averageCost, res_rating, res_address,
                        res_latitude, res_longitude, res_menuListUrl);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(checkForConnection()) {
            // Create a Progress Dialog
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Download Data");
            mProgressDialog.setMessage("Loading...");
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

            Intent sendData = new Intent();
            sendData.setAction(InformationFragment.RES_DATA);
            sendData.putExtra("restaurantInfo", resInfo);
            mContext.sendBroadcast(sendData);


            if (mBitmap != null) {
                mImageView.setImageBitmap(mBitmap);
            } else {
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mImageView.setImageResource(R.drawable.no_image);
            }

            mProgressDialog.dismiss();
        }
    }

    private String getNetworkData (String id){

        try{

            URL url = new URL("https://developers.zomato.com/api/v2.1/restaurant?res_id=" + id);
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
            mProgressDialog.dismiss();
        }

        return null;
    }

    private Bitmap returnImage(String url){

        try {
            URL urlConnection = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("user-key","a4c797e59ba9ad8c7fd14c51e35aefdc");
            httpURLConnection.connect();

            InputStream stream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            stream.close();
            return bitmap;
        }catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();

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
