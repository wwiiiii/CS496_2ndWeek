package com.example.q.helloworld;

/**
 * Created by q on 2016-07-07.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import java.util.ArrayList;


public class grid extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = getIntent();
        ArrayList<String> url_list = intent.getStringArrayListExtra("URL_LIST");
        Log.v("URL", Integer.toString(url_list.size()));
        GridView gridview = (GridView) findViewById(R.id.Gallery);
        gridview.setAdapter(new ImageAdapter(this, url_list));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
