package com.crapp.mqtttest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelperService extends Service implements MqttCallback {

    private static final String MQTT_URI1 = "tcp://test.mosquitto.org:1883";
    private static final String MQTT_URI = "tcp://broker.mqttdashboard.com:1883";
    private static final String CLIENT_ID = "1995prateekgarg95";
    private static final String MQTT_TOPIC = "prateek";
    private static final int QOS = 1;
    private MqttAndroidClient client;

    public MqttHelperService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "MQTT Helper Service Started", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }, "MqttHelperService").start();
        return START_STICKY;
    }

    public class MqttHelperBinder extends Binder {
        public MqttHelperService getService(){
            return MqttHelperService.this;
        }
    }

    public void connect() {
        client = new MqttAndroidClient(this, MQTT_URI, CLIENT_ID);
        client.setCallback(this);

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            client.connect(options,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Toast.makeText(getBaseContext(), "connected to MQTT broker", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Toast.makeText(getBaseContext(), "failed to connect: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(this, "could not connect to MQTT broker at " + MQTT_URI, Toast.LENGTH_SHORT).show();
        }
    }

    public void subscribe() {
        try {
            IMqttToken token = client.subscribe(MQTT_TOPIC, QOS, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Toast.makeText(getBaseContext(), "subscription successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Toast.makeText(getBaseContext(), "subscription failed: " + throwable, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            Toast.makeText(this, "could not subscribe", Toast.LENGTH_SHORT).show();
        }
    }

    public void send(String text) {
        if (client == null || !client.isConnected()) {
            Toast.makeText(this, "sorry, currently not connected...", Toast.LENGTH_SHORT).show();
            return;
        }
        MqttMessage message = new MqttMessage(text.getBytes());
        message.setQos(QOS);
        try {
            client.publish(MQTT_TOPIC, message, null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Toast.makeText(getBaseContext(), "message sent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Toast.makeText(getBaseContext(), "failed to send message: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(this, "could not send message to MQTT broker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Toast.makeText(this, "connection lost", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Toast.makeText(this, "message received on topic " + s, Toast.LENGTH_SHORT).show();
        SharedPreferences messagePreferences = getBaseContext().getSharedPreferences("MESSAGES",MODE_PRIVATE);
        SharedPreferences.Editor editor = messagePreferences.edit();
        editor.putString("message",mqttMessage.toString());
        editor.commit();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Toast.makeText(this, "delivery complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }


}
