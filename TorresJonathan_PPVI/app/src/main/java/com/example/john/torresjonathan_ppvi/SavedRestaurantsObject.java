package com.example.john.torresjonathan_ppvi;


import java.io.Serializable;

//Class for the information of the saved restaurant
class SavedRestaurantsObject implements Serializable {

    final String resName;
    final int resId;
    final byte[] resImage;
    final String resCuisine;
    final String resAddress;

    SavedRestaurantsObject(String _resName, int _resId, byte[] _resImage, String _resCuisine,
                           String _resAddress){

        resName = _resName;
        resId = _resId;
        resImage = _resImage;
        resCuisine = _resCuisine;
        resAddress = _resAddress;


    }


}
