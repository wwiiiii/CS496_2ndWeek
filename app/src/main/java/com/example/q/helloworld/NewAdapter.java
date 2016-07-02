package com.example.q.helloworld;

import android.app.ActionBar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by q on 2016-06-29.
 */
public class NewAdapter extends PagerAdapter {

    LayoutInflater inflater;
    private GalleryFragment mContext;

    @Override
    public int getCount(){
        return 22;
    }
    public NewAdapter(LayoutInflater inflater, GalleryFragment c){
        super();
        this.inflater=inflater;
        mContext=c;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view=inflater.inflate(R.layout.viewpager_img, null);
        ImageView img=(ImageView) view.findViewById(R.id.imageView2);
        img.setImageResource(mContext.mThumbIds[position]);
        view.setTag("pager"+position);

//        FrameLayout box=new FrameLayout(view.getContext());
//        box.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        box.addView(view);
//        box.setTag("pager"+position);

//        ViewGroup.LayoutParams layPra = new ViewGroup.LayoutParams(container.getLayoutParams().width,container.getLayoutParams().height);
////        layPra .gravity = Gravity.CENTER | Gravity.BOTTOM;
//        box.setLayoutParams(layPra );

//        box.getLayoutParams().height= ViewPager.LayoutParams.MATCH_PARENT;
//        box.getLayoutParams().width=ViewPager.LayoutParams.MATCH_PARENT;
        container.addView(view);

        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View)object);
    }
    @Override
    public boolean isViewFromObject(View v, Object obj){
        return v==obj;
    }
}