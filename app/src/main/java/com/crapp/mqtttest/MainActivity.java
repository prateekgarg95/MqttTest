package com.crapp.mqtttest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView  message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView)findViewById(R.id.messages);

        startService(new Intent(getBaseContext(), MqttHelperService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences messagePreferences = getBaseContext().getSharedPreferences("MESSAGES",MODE_PRIVATE);
        String text = messagePreferences.getString("message",null);
        message.setText(text);
    }
}
