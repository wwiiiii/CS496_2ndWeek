package com.example.q.helloworld;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by q on 2016-07-07.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<String> mThumbIds =new ArrayList<>();

    public ImageAdapter(Context c, ArrayList<String> l){
        mContext = c;
        mThumbIds = l;
    }

    public int getCount(){
        return mThumbIds.size();
    }

    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(600, 600));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView) convertView;
        }

        Ion.with(imageView)
                .placeholder(R.drawable.ic_clear_black_24dp)
                .error(R.drawable.ic_clear_black_24dp)
                .load(mThumbIds.get(position));
        return imageView;
    }
}
