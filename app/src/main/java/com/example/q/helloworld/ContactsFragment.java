package com.example.q.helloworld;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import javax.net.ssl.HttpsURLConnection;


public class ContactsFragment extends Fragment {

    private static int ADD_LIST_CONTACT = 424;
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";
    CallbackManager callbackManager;
    final String SERVER_IP = "52.78.66.95";
    JSONObject myInfo;//{myId : facebook id}
    private Socket mSocket;

    // contacts JSONArray
    JSONArray contacts = null;
    String jsonPath;

    ArrayList<Person> contactList;
    ArrayAdapter Adapter;
    Vibrator vibrator;
    static final int BUFFER_SIZE = 8192;


    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        try{
            mSocket = IO.socket("http://"+SERVER_IP+":8124");
        } catch (Exception e) {debug(e.toString());}
        mSocket.connect();
        mSocket.off("initres"); mSocket.off("uploadPhoneContactres"); mSocket.off();
        mSocket.on("initres", handleNewChat);
        mSocket.on("uploadPhoneContactres",  new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                String res = "";
                for(int i=0;i<args.length;i++) res += args[i].toString();
                debug(res);
                mSocket.emit("updateContact");
            }
        });
        mSocket.on("updateContactres",  new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                String res = "";
                for(int i=0;i<args.length;i++) res += args[i].toString();
                debug(res);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        //contactList = jsonStringToList(loadJsonData());

        //facebook login button settings
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        List<String> perm = new ArrayList<String>();
        perm.add("user_friends"); perm.add("public_profile"); perm.add("email");
        loginButton.setReadPermissions(perm);
        loginButton.setFragment(this);


        //ListView settings
        Adapter = new PersonAdapter(
                getContext(), R.layout.my_item_view, contactList
        );
        ListView list = (ListView)view.findViewById(R.id.listView);
//        list.setAdapter(Adapter);

        //ButtonListener settings
        ImageButton butimg = (ImageButton)view.findViewById(R.id.button);
        butimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListContact();
            }
        });
        list.setOnItemLongClickListener(new ListViewItemLongClickListener());

        Button but2 = (Button)view.findViewById(R.id.button_recon);
        but2.setOnClickListener(null);
        but2.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        debug("reconnected");
                        try{
                            mSocket = IO.socket("http://"+SERVER_IP+":8124");
                            mSocket.connect();
                        } catch (Exception e) {debug(e.toString());}
                    }
                }
        );
        Button loginbut = (Button)view.findViewById(R.id.mylogin);
        loginbut.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        callLogin();
                    }
                }
        );

        Button sendbut = (Button)view.findViewById(R.id.send_contact);
        sendbut.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        sendLocalContent();
                    }
                }
        );

        if(AccessToken.getCurrentAccessToken() != null)   debug("Token is " + AccessToken.getCurrentAccessToken().getToken().toString());
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getContext(), "succeed!", Toast.LENGTH_SHORT).show();
                debug("success in fb login");
                debug("Token is " + loginResult.getAccessToken().getToken().toString());
                debug("current Token is " + AccessToken.getCurrentAccessToken().getToken().toString());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "cancel!", Toast.LENGTH_SHORT).show();
                debug("cancel in fb login");
                debug("current Token is " + AccessToken.getCurrentAccessToken().toString());
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(), "error\n" + error.toString(), Toast.LENGTH_SHORT).show();
                debug("error in fb login " + error.toString());
            }
        });
        try{
            mSocket = IO.socket("http://"+SERVER_IP+":8124");
            mSocket.connect();
        } catch (Exception e) {debug(e.toString());}
        updateLoginStatus(view);

        return view;
    }
    public void debug(String str)
    {
        Log.v("mydebug",str);
    }


    protected ArrayList<Person> jsonStringToList(String jsonStr)
    {
        ArrayList<Person> res = new ArrayList<Person>();
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                contacts = jsonObj.getJSONArray(TAG_CONTACTS);

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    //String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String email = c.getString(TAG_EMAIL);
                    //String address = c.getString(TAG_ADDRESS);
                    //String gender = c.getString(TAG_GENDER);

                    // Phone node is JSON ObjectString mobile
                    String mobile = "";
                    if(c.has("phone") && !c.isNull("phone")) {
                        JSONObject phone = c.getJSONObject(TAG_PHONE);
                        mobile = phone.getString(TAG_PHONE_MOBILE);
                        //String home = phone.getString(TAG_PHONE_HOME);
                        //String office = phone.getString(TAG_PHONE_OFFICE);
                    }
                    else mobile = c.getString("mobile");

                    Person contact = new Person(name,email,mobile);

                    // adding contact to contact list
                    res.add(contact);
                }
            } catch (Exception e) {
                debug("jsonStr to List error : " + e.toString());
            }
        }
        res.add(new Person("","",""));
        return res;
    }
    public void buttonPushed(View view)
    {
        vibrator.vibrate(1000);
        LinearLayout parent = (LinearLayout)view.getParent();
        parent = (LinearLayout) parent.getChildAt(0);
        parent = (LinearLayout) parent.getChildAt(2);
        TextView mobile = (TextView)parent.getChildAt(1);
        String num = mobile.getText().toString();
        Toast.makeText(getContext(), num, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+num));
        startActivity(intent);
    }

    protected void addListContact()
    {
        Intent intent = new Intent(getContext(), addActivity.class);
        startActivityForResult(intent, ADD_LIST_CONTACT);
        return;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        debug("onActivity called");
        if(requestCode == ADD_LIST_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {

                    String name = data.getStringExtra("data_name");
                    String email = data.getStringExtra("data_email");
                    String mobile = data.getStringExtra("data_mobile");
                    Person newb = new Person(name, email, mobile);
                    contactList.add(contactList.size() - 1, newb);
                    //savePersonToInternal(newb);
                    //sortContact();
                    Adapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Input Canceled!", Toast.LENGTH_SHORT);
            }
        } else if (requestCode == 12345){
            updateLoginStatus(null);
        } else{
            callbackManager.onActivityResult(requestCode,   resultCode, data);
        }
    }
    class Person{
        public String name;
        public String email;
        public String mobile;

        public Person(String _name, String _email, String _mobile)
        {
            this.name = _name;
            this.email = _email;
            this.mobile = _mobile;
        }

    }
    private class PersonAdapter extends ArrayAdapter<Person>{

        private ArrayList<Person> items;

        public PersonAdapter(Context context, int textViewResourceId, ArrayList<Person> items){
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.my_item_view, null);
            }
            Person p = items.get(position);
            if (p != null) {
                TextView nameV = (TextView) v.findViewById(R.id.name);
                TextView emailV = (TextView) v.findViewById(R.id.email);
                TextView mobileV = (TextView) v.findViewById(R.id.mobile);
                if (nameV != null) {
                    nameV.setText(p.name);
                }
                if (emailV != null) {
                    emailV.setText(p.email);
                }
                if (mobileV != null) {
                    mobileV.setText(p.mobile);
                }
            }
            ImageButton button = (ImageButton) v.findViewById(R.id.call);
            debug("button : " + button.toString());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonPushed(view);
                }
            });
            return v;
        }
    }



    class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> av, View view, int pos, long id){
            debug("LONG CLICK with "+pos+" "+id);
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());
            alertDlg.setTitle("삭제");
            final int position = pos;
            // '예' 버튼이 클릭되면
            alertDlg.setNegativeButton( "예", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                    debug("longClick : Yes");
                    //removeContact(position);
                    Adapter.notifyDataSetChanged();
                    dialog.dismiss();  // AlertDialog를 닫는다.
                }
            });

            // '아니오' 버튼이 클릭되면
            alertDlg.setPositiveButton( "아니오", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    debug("longClick : No");
                    dialog.dismiss();  // AlertDialog를 닫는다.
                }
            });

            alertDlg.setMessage( String.format( "\"%s\"를 삭제하시겠습니까?",
                    contactList.get(pos).name) );
            alertDlg.show();
            return true;
        }
    };


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


    public void sendToken(){
        debug("sendToken");
        if(AccessToken.getCurrentAccessToken() == null){
            debug("no token available");
            return;
        }
        JSONObject qry = new JSONObject();
        String token = AccessToken.getCurrentAccessToken().getToken().toString();
        try{
            qry.put("token",token);
            qry.put("ReqType","sendToken");
            qry.put("ResType","null");
        } catch(Exception e){
            debug("sendToken json err " + e.toString());
        }
        renewMyInfo();
        mSocket.emit("init",myInfo.toString());
    }
    //http://socket.io/blog/native-socket-io-and-android/
    private Emitter.Listener handleNewChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            debug("args " + args.length);
            for(int i=0;i<args.length;i++) debug(i + args[i].toString());
        }
    };

    public void updateLoginStatus(View view)
    {
        if (view == null) view = getView();
        String path = getContext().getFilesDir().getAbsolutePath() + "/mylogin";
        File acc = new File(path + "/account");
        if(!acc.exists())//no dir
        {
            debug("no acc");
            Button loginbut = (Button)view.findViewById(R.id.mylogin);
            loginbut.setText("LOGIN");
        }
        else
        {
            debug("yes acc");
            Button loginbut = (Button)view.findViewById(R.id.mylogin);
            loginbut.setText("LOGOUT");
        }
    }

    public JSONObject getLoginStatus()
    {
        String path = getContext().getFilesDir().getAbsolutePath() + "/mylogin";
        File acc = new File(path + "/account");
        if(!acc.exists()) return null;
        else {
            String res = "";
            byte[] b = new byte[1024];
            try {
                FileInputStream input = new FileInputStream(path + "/account");
                input.read(b);
                res = new String(b).trim();
                input.close();
            }catch(Exception e){
                Log.d("mydebug", "getLoginStatus error + " + e.toString());
            }
            try{
                JSONObject result = new JSONObject(res);
                return result;}
            catch(Exception e){
                Log.d("mydebug", "getLoginStatus error + " + e.toString());
            }
        }
        return null;
    }

    public void callLogin()
    {
        JSONObject acc = getLoginStatus();

        //Login
        if(acc == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, 12345);
        }

        //Logout
        else{
            String path = getContext().getFilesDir().getAbsolutePath() + "/mylogin";
            File f = new File(path + "/account");
            Log.d("mydebug", f.delete()+"");
            updateLoginStatus(null);
        }
        return;
    }


    public ArrayList<JSONObject> getContactList()
    {
        HashMap<String , JSONObject> temp = new HashMap<String,JSONObject>();
        ArrayList<JSONObject> res = new ArrayList<JSONObject>();
        ContentResolver cr = getContext().getContentResolver();
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

        String[] projection = new String[] { ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.PHOTO_ID,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA2, // type
                ContactsContract.Data.DATA1  // phone.number, organization.company
        };

        Cursor mCursor = getContext().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                ContactsContract.Data.MIMETYPE +"='"+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE +"' or " +
                        ContactsContract.Data.MIMETYPE +"='"+ ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE +"'",
                null,
                ContactsContract.Data.DISPLAY_NAME+","+ContactsContract.Data._ID+" COLLATE LOCALIZED ASC");


        int idIdx = mCursor.getColumnIndex( ContactsContract.Data.CONTACT_ID);
        int nameIdx = mCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
        int numIdx = mCursor.getColumnIndex(ContactsContract.Data.DATA1);
        int emailIdx = mCursor.getColumnIndex(ContactsContract.Data.DATA2);
        int photoIdx = mCursor.getColumnIndex(ContactsContract.Data.PHOTO_ID);
        String id = " ", name = " ", num = " ", email=" ", photo=" ";
        if(mCursor.moveToFirst()) {
            do {
                id = " "; name = " "; num = " "; email=" "; photo=" ";
                if (idIdx != -1) id = mCursor.getString(idIdx);
                if (nameIdx != -1) name = mCursor.getString(nameIdx);
                if (numIdx != -1) num = mCursor.getString(numIdx);
                if (emailIdx != -1) email = mCursor.getString(emailIdx);
                if (photoIdx != -1) photo = mCursor.getString(photoIdx);
                id = id.trim(); name=name.trim(); num=num.trim(); email=email.trim();if(photo!=null) photo=photo.trim();
                if(temp.containsKey(id)){
                    JSONObject now = temp.get(id);try{
                        if (id!=null &&!id.equals("") && !now.has("id")) now.put("id", id);
                        if (name!=null && !name.equals("")&& !now.has("name")) now.put("name", name);
                        if (num!=null && !num.equals("")) {
                            if(isEmailAddress(num)){now.getJSONArray("email").put(num);}
                            else if(isPhoneNumber(num)){now.getJSONArray("phone").put(num);}
                            else now.getJSONArray("other").put(num);
                        }
                        if (photo!=null && !photo.equals(" ") && !now.has("photo")) now.put("photo", photo);}catch(Exception e){
                        debug("err1"+e.toString());
                    }
                } else {//make new json object
                    JSONObject now = new JSONObject();
                    try {
                        JSONArray phoneArr = new JSONArray(); JSONArray emailArr = new JSONArray(); JSONArray other = new JSONArray();
                        now.put("phone", phoneArr); now.put("email", emailArr); now.put("other",other);
                        if (!id.equals("")) now.put("id", id);
                        if (!name.equals("")) now.put("name", name);
                        if (!num.equals("")) {
                            if(isEmailAddress(num)){now.getJSONArray("email").put(num);}
                            else if(isPhoneNumber(num)){now.getJSONArray("phone").put(num);}
                            else now.getJSONArray("other").put(num);
                        }
                        if (photo!=null && !photo.equals(" ")) now.put("photo", photo);
                    } catch(Exception e){debug(e.toString());}
                    temp.put(id,now);
                }
                debug(id+name+num+email+photo);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        for(Map.Entry<String, JSONObject> i : temp.entrySet())
        {
            debug(i.getKey().toString() + i.getValue().toString());
            res.add(i.getValue());
        }
        return res;
    }

    public void sendLocalContent()
    {
        debug("sendLocalContent start");
        ArrayList<JSONObject> templist = getContactList();
        JSONArray contact = new JSONArray();
        JSONObject user = getLoginStatus();
        if(user == null){
            debug("sendLocalContent error : user null");
            Toast.makeText(getContext(),"You have to login First!", Toast.LENGTH_LONG ).show();
            return;
        }
        try{
            for(JSONObject i : templist){i.put("src","phone"); contact.put(i);}
            JSONObject arg = new JSONObject(); arg.put("contact", contact);
            arg.put("user", user);
            renewMyInfo();
            if(myInfo!=null) arg.put("fbinfo",myInfo);
            mSocket.emit("uploadPhoneContact", arg);
        } catch(Exception e){debug(e.toString());}
    }

    public boolean isEmailAddress(String str)
    {
        return str.contains("@");
    }

    public boolean isPhoneNumber(String str)
    {
        for(int i=0;i<str.length();i++){
            int code = str.charAt(i);
            if((code!='+'&&code!='-'&&code!=' ') && (code>'9' || code <'0')) return false;
        }
        return true;
    }

    public void renewMyInfo()
    {
        if(AccessToken.getCurrentAccessToken() == null) {myInfo = null; debug("no access token -> no info");return;}
        try {
            Thread t=
                    new Thread(){
                        public void run(){
                            try{
                                String token = AccessToken.getCurrentAccessToken().getToken().toString();
                                String url = "https://graph.facebook.com/me/?access_token=" + token;
                                URL myurl = new URL(url);
                                HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
                                InputStream ins = con.getInputStream();
                                InputStreamReader isr = new InputStreamReader(ins);
                                BufferedReader in = new BufferedReader(isr);
                                String inputLine; String res = "";
                                while ((inputLine = in.readLine()) != null){res += inputLine;}
                                myInfo = new JSONObject(res);
                                myInfo.put("fbid", myInfo.get("id"));
                                myInfo.put("token",token);
                                myInfo.remove("id");
                                debug("renew : "+myInfo.toString());
                                in.close();
                                synchronized (this) {
                                    this.notify();
                                }
                            }catch(Exception e){}
                        }
                    };
            t.start();
            synchronized (t) {
                t.wait();
            }
        }catch(Exception e){
            debug(e.toString());
        }

    }

}
