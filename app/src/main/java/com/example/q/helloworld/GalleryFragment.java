package com.example.q.helloworld;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Future;

import cz.msebera.android.httpclient.Header;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link GalleryFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link GalleryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class GalleryFragment extends Fragment {


    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    ///////////////////////////////////////////////////////////////////////////////////////

    ActionMenuItemView gallery, upload, download, loginmenu;
    ImageView img;
    String path;
    ArrayList<String> url_list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    /**
     * Permission check.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        img = (ImageView)view.findViewById(R.id.preview);
        gallery = (ActionMenuItemView)getActivity().findViewById(R.id.action_gallery);
        upload =(ActionMenuItemView)getActivity().findViewById(R.id.action_upload);
        download = (ActionMenuItemView)getActivity().findViewById(R.id.action_download);
        loginmenu = (ActionMenuItemView)getActivity().findViewById(R.id.action_login);
        loginmenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("onononon","**********************************************");
            }
        });
        download.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(path);
                Log.v("파일", "http://143.248.48.39:3000/uploads"+"/"+ f.getName());
                AsyncHttpClient client = new AsyncHttpClient();
                final String url = "http://143.248.48.39:3000/uploads"+"/"+ f.getName();
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String URL = new String(responseBody, StandardCharsets.UTF_8);
                        url_list.add(URL);
                        Log.v("URL", Integer.toString(url_list.size()));
                        Intent intent = new Intent(getContext(), grid.class);
                        intent.putStringArrayListExtra("URL_LIST", url_list);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getContext(),"onFail",Toast.LENGTH_LONG);
                    }
                });
            }

        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(path);

                Future uploading = Ion.with(getActivity())
                        .load("http://143.248.48.39:3000/uploads")
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {
                                    JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            }

        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/jpg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK){
                    Log.v("에러", getPathFromURI(data.getData()));
                    path = getPathFromURI(data.getData());
                    img.setImageURI(data.getData());
                    upload.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
