package com.example.q.helloworld;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ContactsFragment extends Fragment {

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
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_contacts, container, false);

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        jsonPath = getContext().getFilesDir().getAbsolutePath() + "/Roche";
        contactList = jsonStringToList(loadJsonData());

        Adapter = new PersonAdapter(
                getContext(), R.layout.my_item_view, contactList
        );
        ListView list = (ListView)v.findViewById(R.id.listView);
        list.setAdapter(Adapter);

        ImageButton but = (ImageButton)v.findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addListContact();
            }
        });
        list.setOnItemLongClickListener(new ListViewItemLongClickListener());
        sortContact();
        return v;
    }
    protected String loadJsonDataFromInternal(String dirPath)
    {
        String res = "";
        byte[] b = new byte[BUFFER_SIZE];
        try {
            FileInputStream input = new FileInputStream(dirPath + "/mydata.json");
            input.read(b);
            res = new String(b).trim();
            input.close();
        }catch(Exception e){}
        return res;
    }

    protected String loadJsonDataFromRes()
    {
        InputStream is = getResources().openRawResource(R.raw.mydata);
        Writer writer = new StringWriter();
        char[] buffer = new char[BUFFER_SIZE];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch(Exception e){
        } finally {
            try{is.close();}
            catch(Exception e){}
        }
        return writer.toString().trim();
    }
    public void debug(String str)
    {
        Log.v("mydebug",str);
    }

    public static boolean deleteDirectory(File path) {
        if(!path.exists()) {
            return false;
        }

        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        return path.delete();
    }
    protected boolean saveToIntDir(String jsonData, String dirPath)//성공시 true
    {
        File dir = new File(dirPath);
        if(!dir.exists()) {
            debug("saveToIntDir : Roche dir not exists");
            boolean res = dir.mkdirs();
            if (res == false) {
                debug("saveToIntDir failed : dir generation rejected");
                return false;
            }
            else debug("saveToIntDir : Roche dir created");
        }
        try {
            PrintWriter pw = new PrintWriter(dirPath + "/mydata.json");
            pw.println(jsonData);
            pw.close();
        }catch(Exception e){
            debug("saveToIntDir failed : file write error : " + e.toString());
            return false;
        }
        debug("saveToIntDir succeed : " + jsonData);
        return true;
    }

    protected String loadJsonData(){
        String res="";
        File dir = new File(jsonPath);
        // debug(""+deleteDirectory(dir));
        if(!dir.exists())
        {
            debug("call loadJsonDataFromRes");
            res = loadJsonDataFromRes();
            saveToIntDir(res, dir.getAbsolutePath());
            //그 후 디렉토리 생성하고 파일 복사해서 저장
        }
        else
        {
            debug("call loadJsonDataFromInternal");
            res = loadJsonDataFromInternal(dir.getAbsolutePath());
        }
        debug("result length : " + res.length());
        debug("result : " + res);
        return res;
    }

    protected void savePersonToInternal(Person newbie)
    {
        try {
            JSONObject jsonObj = new JSONObject(loadJsonData());

            // Getting JSON Array node
            JSONArray arr = jsonObj.getJSONArray(TAG_CONTACTS);
            JSONObject jsonPerson = new JSONObject();
            JSONObject jsonPhone = new JSONObject();
            jsonPerson.put("name",newbie.name);
            jsonPerson.put("email",newbie.email);
            jsonPhone.put("mobile",newbie.mobile);
            jsonPhone.put("home","01012344321");
            jsonPerson.put("phone",jsonPhone);
            arr.put(jsonPerson);
            saveToIntDir(jsonObj.toString(), jsonPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    protected ArrayList<Person> jsonStringToList(String jsonStr)
    {
        ArrayList<Person> res = new ArrayList<Person>();
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
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
        startActivityForResult(intent, 1);
        return;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        debug("onActivity called");
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == 1)
            {
                String name = data.getStringExtra("data_name");
                String email = data.getStringExtra("data_email");
                String mobile = data.getStringExtra("data_mobile");
                Person newb = new Person(name, email, mobile);
                contactList.add(contactList.size()-1,newb);
                savePersonToInternal(newb);
                sortContact();
                Adapter.notifyDataSetChanged();
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(getContext(), "Input Canceled!", Toast.LENGTH_SHORT);
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


    public JSONArray sortJsonArrayByName(JSONArray array)
    {
        try {
            List<JSONObject> jsons = new ArrayList<JSONObject>();
            for (int i = 0; i < array.length(); i++) {
                jsons.add(array.getJSONObject(i));
            }
            Collections.sort(jsons, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    try {
                        String lid = lhs.getString("name");
                        String rid = rhs.getString("name");
                        // Here you could parse string id to integer and then compare.
                        return lid.compareTo(rid);
                    }catch(Exception e){}
                    return 1;
                }
            });
            return new JSONArray(jsons);
        }catch(Exception e){debug("sort error : " + e.toString());}
        return null;
    }

    public void sortContact()
    {
        try {
            JSONObject jsonObj = new JSONObject(loadJsonData());
            JSONArray arr = jsonObj.getJSONArray(TAG_CONTACTS);
            JSONArray newarr = sortJsonArrayByName(arr);
            JSONObject res = new JSONObject();
            res.put("contacts", newarr);
            saveToIntDir(res.toString(), jsonPath);
            debug("sortContact Result : " + res.toString());
        } catch (Exception e) {
            debug("sortContact : json error");
        }
        //contactList = jsonStringToList(loadJsonData());
        contactList.remove(contactList.size()-1);
        Collections.sort(contactList, new Comparator<Person>() {
            @Override
            public int compare(Person lhs, Person rhs) {
                try {
                    String lid = lhs.name;
                    String rid = rhs.name;
                    // Here you could parse string id to integer and then compare.
                    return lid.compareTo(rid);
                }catch(Exception e){}
                return 1;
            }
        });
        contactList.add(new Person("","",""));
        Adapter.notifyDataSetChanged();
    }

    public void removeContact(int pos)
    {
        if(pos == contactList.size() -1)
        {
            Toast.makeText(getContext(), "It's dummy!", Toast.LENGTH_SHORT).show();
            return;
        }
        contactList.remove(pos);
        Adapter.notifyDataSetChanged();
        try {
            JSONObject jsonObj = new JSONObject(loadJsonData());
            JSONArray arr = jsonObj.getJSONArray(TAG_CONTACTS);
            JSONArray newarr = new JSONArray();
            for(int i=0;i<arr.length();i++)
            {
                if(i == pos) continue;
                newarr.put(arr.getJSONObject(i));
            }
            JSONObject res = new JSONObject();
            res.put("contacts", newarr);
            saveToIntDir(res.toString(), jsonPath);
            debug("removeContact Result : " + res.toString());
        } catch (JSONException e) {
            debug("removeContact : json error");
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
                    removeContact(position);
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
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Toast.makeText(getContext(), "onButtonPressed for Tab A", Toast.LENGTH_LONG);
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
