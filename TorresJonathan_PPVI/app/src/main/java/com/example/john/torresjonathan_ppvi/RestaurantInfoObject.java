package com.example.john.torresjonathan_ppvi;

import java.io.Serializable;

//Class for the information of the restaurant
class RestaurantInfoObject implements Serializable {

    final String res_name;
    final String res_cuisines;
    final int res_price;
    final String res_currency;
    final int res_averageCost;
    final String res_rating;
    final String res_address;
    final String resLat;
    final String resLng;
    final String resMenuUrl;

    RestaurantInfoObject(String _res_names, String _res_cuisines, int _res_price,
                             String _res_currency, int _res_averageCost ,
                         String _res_rating, String _res_address, String _resLat, String _resLng, String _resMenuUrl){
        res_name = _res_names;
        res_cuisines = _res_cuisines;
        res_price = _res_price;
        res_currency = _res_currency;
        res_averageCost = _res_averageCost;
        res_rating = _res_rating;
        res_address = _res_address;
        resLat = _resLat;
        resLng = _resLng;
        resMenuUrl = _resMenuUrl;

    }

}

