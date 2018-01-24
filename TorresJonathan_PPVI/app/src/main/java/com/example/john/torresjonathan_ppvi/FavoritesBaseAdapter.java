package com.example.john.torresjonathan_ppvi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



class FavoritesBaseAdapter extends BaseAdapter {

    // BASE ID
    private static final int BASE_ID = 0x0A0B0CD;

    // Reference to our owning screen (context)
    private final Context mContext;

    // Reference to our collection
    private final ArrayList<SavedRestaurantsObject> mCollection;

    // ViewHolder inner class
    class ViewHolder{
        final TextView resName;
        final TextView resCuisine;
        final TextView resAddress;
        final ImageView resImage;

        ViewHolder(View v){

            resName = (TextView) v.findViewById(R.id.resName_ListViewText);
            resCuisine = (TextView) v.findViewById(R.id.resCuisine_ListViewText);
            resAddress = (TextView) v.findViewById(R.id. resAddress_ListViewText);
            resImage = (ImageView) v.findViewById(R.id.resPic_ImageView);

        }
    }

    //Inner Class
    FavoritesBaseAdapter(Context _context, ArrayList<SavedRestaurantsObject> _collection) {
        mContext = _context;
        mCollection = _collection;
    }

    @Override
    public int getCount() {
        if(mCollection!=null) {
            return mCollection.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if( mCollection!=null &&
                position < mCollection.size() &&
                position > -1) {
            return mCollection.get(position);
        }
        return null;    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        //recycle all view
        if(convertView == null) {

            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.listviewitem_layout,parent,false);


            //Create view holder and use set tag
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }


        SavedRestaurantsObject savedRestaurantsObject = (SavedRestaurantsObject) getItem(position);


        //set information to the views
        holder.resName.setText(savedRestaurantsObject.resName);

        holder.resCuisine.setText(savedRestaurantsObject.resCuisine);

        holder.resAddress.setText(savedRestaurantsObject.resAddress);

        byte[] imageData = savedRestaurantsObject.resImage;

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        if(bitmap != null){
            holder.resImage.setImageBitmap(bitmap);
        }else{
            holder.resImage.setImageDrawable(mContext.getResources().getDrawable
                    (R.drawable.no_image, null));
        }

        return convertView;
    }
}
