package com.example.q.helloworld;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.renderscript.Matrix3f;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link SphereFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link SphereFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class SphereFragment extends Fragment implements SensorEventListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public SphereFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment SphereFragment.
//     */
    // TODO: Rename and change types and number of parameters
    public static SphereFragment newInstance(String param1, String param2) {
        SphereFragment fragment = new SphereFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sphere, container, false);

        {
            mSensorManager=(SensorManager) this.getContext().getSystemService(this.getActivity().SENSOR_SERVICE);
            mSensor=null;
            mSensorAvailable=false; //Init value. I will be turned on when we find a accelerometer
            if(mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!=null) {
                mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                mSensorAvailable=true;
            }
            if(!mSensorAvailable) {
            }
            mAccelSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        {
            mWebView = (WebView) view.findViewById(R.id.webView);
            mWebView.setWebViewClient(new WebViewClient());
            WebSettings setting = mWebView.getSettings();
            setting.setJavaScriptEnabled(true);
            setting.setBuiltInZoomControls(false);
            setting.setLoadWithOverviewMode(true);
            setting.setUseWideViewPort(true);
            setting.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
            mWebView.loadUrl("http://comic.naver.com/webtoon/detail.nhn?titleId=678499&no=4&weekday=fri");
        }
        {
            Button button=(Button)view.findViewById(R.id.slideButton);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            onStartSlide();
                            break;
                        case MotionEvent.ACTION_UP:
                            onStopSlide();
                            break;
                    }
                    return true;
                }
            });
        }
        {
            SeekBar seekBar=(SeekBar)view.findViewById(R.id.speedBar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    onScrollSpeed(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            Switch s=(Switch)view.findViewById(R.id.switchScrollWay);
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    way=isChecked;
                }
            });
        }

        return view;
    }

    public void onScrollSpeed(int value) {
        speed=30.0f+value;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
    //Supports for motion sensor
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private float speed=100.0f;
    private boolean way=false;

    private Sensor mAccelSensor;
    private Sensor mMagnetSensor;
    float[] mGravity;
    float[] mMagnetic;

    private boolean mSensorAvailable;

    private boolean isSliding;
    private float[] oldR;

    public final void onSensorChanged(SensorEvent event) {
        float y;

        float I[] = new float[9];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mMagnetic = event.values;

        float[] R=new float[9];
        boolean success;
        if(mGravity==null || mMagnetic==null)
            return;
        if(!isSliding) {
            oldR = new float[9];
            success = SensorManager.getRotationMatrix(oldR, I, mGravity, mMagnetic);
        }
        else {
            if(SensorManager.getRotationMatrix(R, I, mGravity, mMagnetic)) {
                float orientation[] = new float[3];
                SensorManager.getAngleChange(orientation, R, oldR);
                y = orientation[1];
//                Log.d("motion","x : " + orientation[0] + "\ty : "+orientation[1] + "\tz : "+orientation[2]);

                float offset = (y * y);
                if (y < 0.0f)
                    offset = -offset;
                if(way)
                    offset=-offset;
                offset*=speed;
                Log.d("gimun", "offset : " + offset);
                if (Math.abs(offset) >= 3.0f)
                    mWebView.setScrollY(mWebView.getScrollY() + (int)offset);
            }
        }
    }
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Must be implemented even if it is empty
    }
    public void onResume() {
        super.onResume();
        if(mSensorAvailable)
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
        if(mAccelSensor!=null)
            mSensorManager.registerListener(this,mAccelSensor,SensorManager.SENSOR_DELAY_GAME);
        if(mMagnetSensor!=null)
            mSensorManager.registerListener(this,mMagnetSensor,SensorManager.SENSOR_DELAY_GAME);
    }
    public  void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    public void onStartSlide() {
        if(isSliding)
            return;
        isSliding=true;
        mWebView.setEnabled(false);
    }
    public void onStopSlide() {
        isSliding=false;
        mWebView.setEnabled(true);
    }
}
