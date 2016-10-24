package com.yangproject.iot.Mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.yangproject.iot.Alarm.DateTimeAlarmManager;
import com.yangproject.iot.Others.Constants;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 우민 on 2016-06-04.
 */
public class MqttManager extends DateTimeAlarmManager implements MqttCallback {
    private static final String TAG = "MqttManager";

    private Context context;

    private MqttClient client;
    private int qos = 2;
    private MemoryPersistence persistence = new MemoryPersistence();

    private int lensNum;

    public MqttManager(Context context, int lensNum) {
        super(context);
        this.context = context;
        this.lensNum = lensNum;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.TRY_MQTT_CONNECTION);
        // filter.addAction(Constants.RENEW_DATABASE_TO_SERVICE);
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(topic.equals(Constants.MQTT_TOPIC_1)) {
            try {
                Intent intent = new Intent(Constants.MQTT_TOPIC_1);
                intent.putExtra("lens_number", Integer.valueOf(message.toString()));
                context.sendBroadcast(intent);
                Log.d(TAG, Constants.MQTT_TOPIC_1);
            } catch (Exception exc) {
                Log.d(TAG, Constants.MQTT_TOPIC_1 + " failed");
            }
        }
        else if(topic.equals(Constants.MQTT_TOPIC_2)) {
            try {
                Intent intent = new Intent(Constants.MQTT_TOPIC_2);
                String lensNum = message.toString().substring(0, 1);
                String lensState = message.toString().substring(1, message.toString().length());
                intent.putExtra("lens_number", lensNum);
                intent.putExtra("lens_state", lensState);
                context.sendBroadcast(intent);
                Log.d(TAG, Constants.MQTT_TOPIC_2);
            } catch (Exception exc) {
                Log.d(TAG, Constants.MQTT_TOPIC_2 + " failed");
            }
        }
        Log.d(TAG, Constants.MQTT_TOPIC_1 + " " + Constants.MQTT_TOPIC_2);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.TRY_MQTT_CONNECTION)) {
                startAlarmService(null);
            }
            /*else if(intent.getAction().equals(Constants.RENEW_DATABASE_TO_SERVICE)) {
                int lensNum = intent.getIntExtra("lens_num", 0);
                sendLensNum(lensNum);
            }*/
        }
    };

    @Override
    public void startAlarmService(String param) {
        try {
            client = new MqttClient(Constants.MQTT_SERVER, "test", persistence);
            client.setCallback(this);
            MqttConnectOptions connOpt = new MqttConnectOptions();
            connOpt.setCleanSession(true);

            client.connect(connOpt);
            client.subscribe(Constants.MQTT_TOPIC_1, qos);
            client.subscribe(Constants.MQTT_TOPIC_2, qos);
            Toast.makeText(context, "Mqtt connection succeeded", Toast.LENGTH_SHORT).show();
            sendLensNum(lensNum);
        }
        catch (Exception exc) {
            Log.d(TAG, "tryConnection failed");
            try {
                client.close();
            } catch (MqttException me) { }
            Toast.makeText(context, "Mqtt connection failed", Toast.LENGTH_SHORT).show();
            reTryMqttConnection();
        }
    }

    public void sendLensNum(int lensNum) {
        if(lensNum == 0) {
            return;
        }
        try {
            MqttMessage mqttMessage = new MqttMessage(String.valueOf(lensNum).getBytes());
            mqttMessage.setQos(qos);
            // MqttMessage mqttMessage1 = new MqttMessage(String.valueOf("3").getBytes());
            client.publish(Constants.MQTT_TOPIC_0, mqttMessage);
            // client.publish(Constants.MQTT_TOPIC_3, mqttMessage1);
            Log.d(TAG, "send message " + lensNum);
        } catch (MqttException me) {
            Toast.makeText(context, "Client publish failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void commandWashing(int lensNum) {
        try {
            MqttMessage mqttMessage = new MqttMessage(String.valueOf(lensNum).getBytes());
            mqttMessage.setQos(qos);
            client.publish(Constants.MQTT_TOPIC_3, mqttMessage);
            Log.d(TAG, "command washing " + lensNum);

            Intent intent = new Intent(Constants.MQTT_TOPIC_1);
            intent.putExtra("lens_number", lensNum);
            context.sendBroadcast(intent);
        } catch (MqttException me) {
            Toast.makeText(context, "Publishing command failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void reTryMqttConnection() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        long current10min = (new Date()).getTime() + Constants.MQTT_CONNECTION_PERIOD;
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        sdf2.setLenient(false);
        String current10minStr = sdf2.format(current10min);
        super.startDateTimeAlarmService(Constants.TRY_MQTT_CONNECTION, current10minStr,
                "Trying mqtt connection alarm started", "Trying mqtt connection alarm failed");
    }

    public void tryMqttConnection() {
        startAlarmService(null);
    }

    public void tryMqttClose() {
        stopAlarmService();
        try {
            client.close();
        } catch (MqttException me) { }
    }

    @Override
    public void stopAlarmService() {
        try {
            super.stopDateTimeAlarmService(Constants.TRY_MQTT_CONNECTION, "Stopped to trying mqtt connection");
        } catch (Exception exc) {
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) { }

    @Override
    public void connectionLost(Throwable t) {
        reTryMqttConnection();
        Log.d(TAG, "connectionLost");
    }
}
