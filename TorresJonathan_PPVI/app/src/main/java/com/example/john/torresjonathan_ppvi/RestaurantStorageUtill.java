package com.example.john.torresjonathan_ppvi;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


class RestaurantStorageUtill {

    //File to handle all save,load and delete from the favorites screens

    private static final String FILE_NAME = "res.dat";

    @SuppressWarnings("unchecked")
    public static ArrayList<SavedRestaurantsObject> loadLocations(Context _context) {
        ArrayList<SavedRestaurantsObject> restaurants = null;

        try {
            FileInputStream fis = _context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            restaurants = (ArrayList<SavedRestaurantsObject>)ois.readObject();
            ois.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(restaurants == null) {
            restaurants = new ArrayList<>();
        }

        return restaurants;
    }

    private static void saveLocations(Context _context, ArrayList<SavedRestaurantsObject> _restaurants) {
        try {
            FileOutputStream fos = _context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_restaurants);
            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveRestaurant(Context _context, SavedRestaurantsObject restaurantsObject) {
        ArrayList<SavedRestaurantsObject> locations = loadLocations(_context);
        locations.add(restaurantsObject);
        saveLocations(_context, locations);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public static void deleteRestaurant(Context _context, SavedRestaurantsObject restaurantsObject) {
        ArrayList<SavedRestaurantsObject> locations = loadLocations(_context);
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).resId == restaurantsObject.resId){
                locations.remove(i);
            }
        }
        saveLocations(_context, locations);
    }

    public static boolean isFavorite(Context _context, int Id){

        ArrayList<SavedRestaurantsObject> restaurants = loadLocations(_context);

        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).resId == Id){
                return true;
            }
        }
        return false;
    }

}
