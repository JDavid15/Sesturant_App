package com.example.john.torresjonathan_ppvi;


import java.io.Serializable;

//Class for the information of all locations
class RestaurantLocationObject implements Serializable {

    final String resName;
    final Double resLat;
    final Double resLng;
    final int resID;
    final int resAvegCost;


    RestaurantLocationObject(String _resName, Double _resLat, Double _resLng, int _resId, int _resAvegCost){
        resName = _resName;
        resLat = _resLat;
        resLng = _resLng;
        resID = _resId;
        resAvegCost = _resAvegCost;
    }

}
