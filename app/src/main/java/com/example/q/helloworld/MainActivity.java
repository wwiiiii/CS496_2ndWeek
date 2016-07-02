package com.example.q.helloworld;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {
    int asdf = 1234;
    public String[] contacts={"Lee Seungwoo",
                                "Jung Taeyoung",
                                "Gimun"};
    ContactsFragment mContactsFragment;
    GalleryFragment mGalleryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTabHost tabHost=(FragmentTabHost)findViewById(R.id.tabHost);
        tabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec(ContactsFragment.class.getSimpleName()).setIndicator("CONTACTS"),ContactsFragment.class,null);
//        mContactsFragment=(ContactsFragment)getSupportFragmentManager().findFragmentByTag(ContactsFragment.class.getSimpleName());
//        Log.d("frag","mContactsFragment" + mContactsFragment.toString());
        tabHost.addTab(tabHost.newTabSpec("gallery").setIndicator("GALLERY"),GalleryFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("sphere").setIndicator("MOTION"),SphereFragment.class,null);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height=120;
    }
    @Override
    public void onAttachFragment(Fragment frag) {
//        Log.d("frag","frag attached");
        super.onAttachFragment(frag);
        if(frag.getClass()==ContactsFragment.class) {
//            Log.d("frag","mContactsFragment registered");
            mContactsFragment=(ContactsFragment)frag;
        }
        else if(frag.getClass()==GalleryFragment.class) {
            mGalleryFragment=(GalleryFragment)frag;
        }
    }

    public void callButtonPushed(View view) {
        mContactsFragment.buttonPushed(view);
    }
    public void galleryBackToGrid(View view) {
        mGalleryFragment.BackToGrid(view);
    }
}
