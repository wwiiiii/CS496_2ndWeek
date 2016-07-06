package com.example.q.helloworld;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class IntegratedLoginActivity extends Activity {

    private static int DO_FACEBOOK_LOG = 1;
    private static int DO_APP_LOG = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrated_login);
        ImageButton fbbut = (ImageButton)findViewById(R.id.fb_login_button);
        fbbut.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        doFbLogin();
                    }
                });

        ImageButton appbut = (ImageButton)findViewById(R.id.app_login_button);
        appbut.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        doAppLogin();
                    }
                });
        if(getLoginStatus() != null) ((TextView)findViewById(R.id.apptxtview)).setText("←앱 로그아웃");
        if(getIntent().getBooleanExtra("fbstatus",false) == true) ((TextView)findViewById(R.id.fbtxtview)).setText("←페이스북 로그아웃");
    }
    public void doFbLogin()
    {
        setResult(DO_FACEBOOK_LOG);
        finish();
    }

    public void doAppLogin()
    {
        setResult(DO_APP_LOG);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    public JSONObject getLoginStatus()
    {
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/mylogin";
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

}
