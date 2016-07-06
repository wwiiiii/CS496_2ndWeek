package com.example.q.helloworld;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button but = (Button)findViewById(R.id.logOrReg);
        but.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        makeAccount();
                    }
                }
        );
    }

    public void makeAccount()
    {
        try{
            String id = ((EditText)findViewById(R.id.idText)).getText().toString();
            String pw = ((EditText)findViewById(R.id.pwText)).getText().toString();
            String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/mylogin";
            File dir = new File(path);
            if(!dir.exists()) dir.mkdirs();
            JSONObject acc = new JSONObject();
            acc.put("id",id); acc.put("pw",pw);
            PrintWriter prw = new PrintWriter(path + "/account");
            prw.println(acc.toString());
            prw.close();
            finish();
        } catch(Exception e){
            Log.d("mydebug", "mkAcc Error : " + e.toString());
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
