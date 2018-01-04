package com.example.eddie.taquin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by eddie on 27/10/17.
 */

public class TaquinImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Bitmap> chucks;
    private int cuts;
    private int hidden;


    public TaquinImageAdapter(Context c, ArrayList<Bitmap> ch, int cu,int h) {
        mContext = c;
        chucks = ch;
        cuts = cu;
        hidden = h;
    }

    @Override
    public int getCount() {
        return chucks.size();
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
            int s = 768;
            imageView.setLayoutParams(new GridView.LayoutParams(s/cuts, s/cuts));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        
        
//        imageView.setImageBitmap(resizeImg(mThumbIds[position]));
        if(position == hidden){
            imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.trans));
        }
        else{
            imageView.setImageBitmap(chucks.get(position));
        }
        return imageView;
    }


    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }


}
