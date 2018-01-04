package com.example.eddie.taquin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

/**
 * Created by eddie on 27/10/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(256, 256));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(resizeImg(mThumbIds[position]));
        return imageView;
    }

    private Bitmap resizeImg(Integer mThumbId) {
        Bitmap img = BitmapFactory.decodeResource(mContext.getResources(), mThumbId);
        Bitmap resized = Bitmap.createScaledBitmap(img, 128, 128, false);
        return resized;
    }


    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.champs, R.drawable.champs2,
            R.drawable.lac, R.drawable.montagne,
            R.drawable.plage, R.drawable.ville
    };

    public int getDrawableId(int i){
        return mThumbIds[i];
    }
}
