package com.example.q.helloworld;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

}
