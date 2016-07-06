package com.example.q.helloworld;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link GalleryFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link GalleryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class GalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public Integer[] mThumbIds = {

    };
    private ViewPager pager;
    private NewAdapter adapter;
    private ViewSwitcher viewSwitcher;
    private boolean isZoom=false;
    private float sizeX;
    private float sizeY;
    protected class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        ViewPager mPager;
        public ScaleListener(ViewPager pager) {
            mPager=pager;
        }
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
//            Log.d("pager",""+scaleFactor);
//            Log.d("pager",""+mPager.getCurrentItem());
//            FrameLayout currentBox=(FrameLayout)mPager.findViewWithTag("pager"+mPager.getCurrentItem());
            View currentView=mPager.findViewWithTag("pager"+mPager.getCurrentItem());
//            Log.d("pager","box : "+currentBox.getClass().toString());
//            View currentView = ((LinearLayout)currentBox.getChildAt(0)).getChildAt(0);
//            Log.d("pager","view : "+currentView.getClass().toString());
//            mPager.getAdapter().getItem;
//            View currentView=mPager.getFocusedChild();
//            Log.d("pager","current scaleX : "+currentView.getScaleX());
            sizeX=currentView.getScaleX();
//            Log.d("pager","sizeX : "+currentView.getScaleX());
            Log.d("pager","scaleFactor : "+scaleFactor);
            sizeY=currentView.getScaleY();
            currentView.setScaleX(sizeX*scaleFactor);
//            Log.d("pager","sizeX : "+currentView.getScaleX());
            currentView.setScaleY(sizeY*scaleFactor);
//            float sizeX=mPager.getScaleX();
//            float sizeY=mPager.getScaleY();
//            mPager.setScaleX(sizeX*scaleFactor);
//            mPager.setScaleY(sizeY*scaleFactor);
//            mPager.getChildAt(mPager.getCurrentItem()).setScaleY(scaleFactor);
            return true;
        }
    }
    private ScaleGestureDetector scaleGestureDetector;

    public void BackToGrid(View v){
        viewSwitcher.showNext();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        viewSwitcher = (ViewSwitcher)view.findViewById(R.id.viewSwitcher);
        Animation inAnimation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_in_left); inAnimation.setDuration(500);
        Animation outAnimation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_out_right); outAnimation.setDuration(500);
        viewSwitcher.setOutAnimation(outAnimation);
        viewSwitcher.setInAnimation(inAnimation);
        {
            GridView gridView = (GridView) view.findViewById(R.id.gridView);
            gridView.setAdapter(new ImageAdapter(this));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(view.getContext(), "" + position, Toast.LENGTH_SHORT).show();
                    pager.setCurrentItem(position, true);
                    viewSwitcher.showNext();
                }
            });
//            System.out.println("============================");
//            viewSwitcher.addView(gridView);
        }
        {
            pager = (ViewPager) view.findViewById(R.id.main_viewPager);
            adapter = new NewAdapter(this.getActivity().getLayoutInflater(), this);
            pager.setAdapter(adapter);
            pager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                    float normalizedposition = Math.abs(1 - Math.abs(position));

                    page.setAlpha(normalizedposition);  //View의 투명도 조절
                    page.setScaleX(normalizedposition / 2 + 0.5f); //View의 x축 크기조절
                    page.setScaleY(normalizedposition / 2 + 0.5f); //View의 y축 크기조절
                    page.setRotationY(position * 80);   //View의 Y축(세로축) 회전 각도
                }
            });

            scaleGestureDetector=new ScaleGestureDetector(view.getContext(), new ScaleListener(pager));
            pager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int act=event.getAction();
                    switch(act&MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            if(!isZoom)
                                isZoom=true;
                            break;
                        case MotionEvent.ACTION_DOWN :
                            break;
                        case MotionEvent.ACTION_UP:
                            isZoom=false;
                            break;
                        case MotionEvent.ACTION_MOVE:
//                            if(!isZoom) return false;
                    }
                    scaleGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }

        return view;
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
}
