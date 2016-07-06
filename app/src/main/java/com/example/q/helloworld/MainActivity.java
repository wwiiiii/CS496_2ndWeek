package com.example.q.helloworld;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    ContactsFragment mContactsFragment;
    GalleryFragment mGalleryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getApplicationContext(), "PLEASE...", Toast.LENGTH_LONG);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "PLEASE...", Toast.LENGTH_LONG);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "PLEASE...", Toast.LENGTH_LONG);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        getSupportActionBar().setTitle("Project2");
        //checkPermission();
        //try{Thread.sleep(3000);}catch(Exception e){}
        FragmentTabHost tabHost=(FragmentTabHost)findViewById(R.id.tabHost);
        tabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec(ContactsFragment.class.getSimpleName()).setIndicator("CONTACTS"),ContactsFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("gallery").setIndicator("GALLERY"),GalleryFragment.class,null);
        tabHost.addTab(tabHost.newTabSpec("sphere").setIndicator("CHAT"),SphereFragment.class,null);
        tabHost.setCurrentTab(2);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==R.id.action_upload){
            Toast.makeText(this, "Upload", Toast.LENGTH_SHORT).show();
        }

        else if (id==R.id.action_download) {
            Toast.makeText(this, "Download", Toast.LENGTH_LONG).show();
        }

        else if (id==R.id.action_gallery) {
            Toast.makeText(this, "Gallery", Toast.LENGTH_LONG).show();
        }

        else if (id==R.id.action_login) {
            Toast.makeText(this, "Login", Toast.LENGTH_LONG).show();

        }


        return super.onOptionsItemSelected(item);

    }
}
