package com.example.q.helloworld;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link SphereFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link SphereFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class SphereFragment extends Fragment {

    String[] globalres;
    Handler handler;
    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":12345";
    String nowChatLog;
    String myid;
    public SphereFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SphereFragment newInstance() {
        SphereFragment fragment = new SphereFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public void debug(String s){Log.d("mydebug",s);}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalres = null;
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                debug("msg called!");
                display(null);
            }
        };


        mSocket = null;
        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
        } catch (Exception e) {}
        mSocket.connect();
        debug(mSocket.toString());
        mSocket.off("init"); mSocket.off("otherChat");
        mSocket.on("init", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                debug("init called");
                JSONArray initarr = new JSONArray(); initarr = (JSONArray) args[0];
                globalres = new String[initarr.length()];try{
                    for(int i=0;i<initarr.length();i++) globalres[i] = initarr.get(i).toString();}catch(Exception e){}
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        });
        mSocket.on("otherChat",  new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                debug("other chat called");
                globalres = new String[1];
                globalres[0] = args[0].toString();
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        });
        myid = ((int)(Math.random()*500000) + 1)+"";
        nowChatLog = "";


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sphere, container, false);
        ImageButton but = (ImageButton) view.findViewById(R.id.chatButton);
        but.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                sendChat();
            }
        });
        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
        } catch (Exception e) {}
        mSocket.connect();
        debug(mSocket.toString());
        mSocket.off("init"); mSocket.off("otherChat");
        mSocket.on("init", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                debug("init called");
                JSONArray initarr = new JSONArray(); initarr = (JSONArray) args[0];
                globalres = new String[initarr.length()];try{
                for(int i=0;i<initarr.length();i++) globalres[i] = initarr.get(i).toString();}catch(Exception e){}
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        });
        mSocket.on("otherChat",  new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                debug("other chat called");
                globalres = new String[1];
                globalres[0] = args[0].toString();
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        });
        myid = ((int)(Math.random()*500000) + 1)+"";
        nowChatLog = "";
        mSocket.emit("init", myid);
        return view;
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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    public void sendChat()
    {
        debug("send my chat");
        String content = ((TextView)getView().findViewById(R.id.chatContent)).getText().toString();
        ((TextView)getView().findViewById(R.id.chatContent)).setText("");
        JSONObject temp = new JSONObject();
        try{temp.put("content", content);} catch(Exception e){}
        mSocket.emit("myChat", temp);
        String[] t = new String[1]; t[0] = "Me: "+content;
        display(t);
    }

    public void display(String[] strs)
    {
        if(strs != null) {
            for (String i : strs) nowChatLog += i + '\n';
            ((TextView) getView().findViewById(R.id.chatShow)).setText(nowChatLog);
        } else if(globalres!=null){
            for (String i : globalres) nowChatLog += i + '\n';
            ((TextView) getView().findViewById(R.id.chatShow)).setText(nowChatLog);
        }
    }
}
