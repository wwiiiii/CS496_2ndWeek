package com.example.q.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class addActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);

        Button but = (Button) findViewById(R.id.confirm);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                // startActivity(intent);
                onConfirm(view);
            }
        });

        Button but1 = (Button) findViewById(R.id.cancel);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                // startActivity(intent);
                onCancel(view);
            }
        });

    }
    public void debug(String str)
    {
        Log.v("mydebug",str);
    }

    protected void onConfirm(View view)
    {
        debug("onConfirm");
        Intent intent = getIntent();
        String name = ((TextView)findViewById(R.id.inputName)).getText().toString();
        String email = ((TextView)findViewById(R.id.inputEmail)).getText().toString();
        String mobile = ((TextView)findViewById(R.id.inputMobile)).getText().toString();
        intent.putExtra("data_name",name);
        intent.putExtra("data_email",email);
        intent.putExtra("data_mobile",mobile);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void onCancel(View view)
    {
        debug("onCancel");
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
