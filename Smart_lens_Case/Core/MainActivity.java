package com.yangproject.iot.Core;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yangproject.iot.Data.LensData;
import com.yangproject.iot.Dialog.ModifySleepTimeDialog;
import com.yangproject.iot.Dialog.NewLensDialog;
import com.yangproject.iot.Others.Constants;
import com.yangproject.iot.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityManager manager;

    private LensData mData;
    private int lensNum;

    private TextView sleepTimeText;
    private TextView[] lensTitleText;
    private TextView[] expirationDateText;
    private TextView[] lastWashText;

    private Intent lensServiceIntent;

    private String sleepTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLayout();

        mData = new LensData(this);
        lensNum = 0;

        for(int i = 1; i < Constants.LENS_MAX_NUMBER + 1; i++) {
            setText(i);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.RENEW_DATABASE_TO_ACTIVITY);
        registerReceiver(mReceiver, filter);

        lensServiceIntent = new Intent(getApplicationContext(), LensAlertService.class);

        sleepTime = mData.getData(Constants.SLEEP_TIME, Constants.SLEEP_TIME);
        if(sleepTime == null) {
            showSleepTimeDialog();
            String tempSleepTime = sleepTimeText.getText().toString();
            try {
                if(tempSleepTime.length() < 1) {
                    sleepTimeText.setText("22:00");
                    mData.putData(Constants.SLEEP_TIME, Constants.SLEEP_TIME, "22:00");
                }
            } catch (Exception exc) { }
        }
        else {
            sleepTimeText.setText(sleepTime);
        }

        mData.putData(Constants.LENS + "1", Constants.LENS_NAME, "yang right");
        mData.putData(Constants.LENS + "1", Constants.LENS_EXPIRATION_DATE, "2016-6-19");
        mData.putData(Constants.LENS + "1", Constants.LENS_LAST_WASH_DATE, "2016-6-18 01:05:00");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.RENEW_DATABASE_TO_ACTIVITY)) {
                for(int i = 1; i < Constants.LENS_MAX_NUMBER + 1; i++) {
                    setText(i);
                }
            }
        }
    };

    private void setLayout() {
        lensTitleText = new TextView[Constants.LENS_MAX_NUMBER];
        expirationDateText = new TextView[Constants.LENS_MAX_NUMBER];
        lastWashText = new TextView[Constants.LENS_MAX_NUMBER];

        lensTitleText[0] = (TextView)findViewById(R.id.lensTitleText1);
        lensTitleText[1] = (TextView)findViewById(R.id.lensTitleText2);
        lensTitleText[2] = (TextView)findViewById(R.id.lensTitleText3);

        expirationDateText[0] = (TextView)findViewById(R.id.expirationDateText1);
        expirationDateText[1] = (TextView)findViewById(R.id.expirationDateText2);
        expirationDateText[2] = (TextView)findViewById(R.id.expirationDateText3);

        lastWashText[0] = (TextView)findViewById(R.id.lastWashText1);
        lastWashText[1] = (TextView)findViewById(R.id.lastWashText2);
        lastWashText[2] = (TextView)findViewById(R.id.lastWashText3);

        sleepTimeText = (TextView)findViewById(R.id.sleepTimeText);
    }

    public boolean checkLens(int index) {
        try {
            if(mData.getData(Constants.LENS + index, Constants.LENS_NAME).length() > 0) {
                return true;
            }
        }
        catch (Exception exc) {
            return false;
        }
        return true;
    }

    public void setLensNum() {
        lensNum = 0;
        for(int i = 1; i < Constants.LENS_MAX_NUMBER + 1; i++) {
            try {
                if(mData.getData(Constants.LENS + i, Constants.LENS_NAME).length() > 0) {
                    if(i == 3) {
                        lensNum += 4;
                    }
                    else {
                        lensNum += i;
                    }
                }
            }
            catch (Exception exc) { }
        }
    }

    public void onAddBtnClick(View v) {
        if(checkServiceRunning()) {
            Toast.makeText(getApplicationContext(), "Stop service and do this work", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = 0;
        switch(v.getId()) {
            case R.id.addBtn1 :
                index = 1;
                break;
            case R.id.addBtn2 :
                index = 2;
                break;
            case R.id.addBtn3 :
                index = 3;
                break;
        }
        if (checkLens(index)) {
            Toast.makeText(getApplicationContext(), "Already exist", Toast.LENGTH_SHORT).show();
            return;
        }
        final NewLensDialog newLensDialog = new NewLensDialog(this, index) {
            @Override
            public void onClick(View v) {
                String[] result = this.getResult();
                int index = getIndex();
                try {
                    if(result[0].length() < 1 || result[1].length() < 1) {
                        Toast.makeText(getApplicationContext(), "Input lens information", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                            format.setLenient(false);
                            Date inputDate = format.parse(result[1]);
                            Date currentDate = format.parse(format.format(new Date()));
                            if(currentDate.after(inputDate)) {
                                Toast.makeText(getApplicationContext(), "Input coming date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        catch (Exception exc) {
                            Toast.makeText(getApplicationContext(), "Invalid date format, ex) yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                catch (Exception exc) {
                    Toast.makeText(getApplicationContext(), "Input lens name and expiration date", Toast.LENGTH_SHORT).show();
                }

                try {
                    mData.putData(Constants.LENS + index, Constants.LENS_NAME, result[0]);
                    mData.putData(Constants.LENS + index, Constants.LENS_EXPIRATION_DATE, result[1]);
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setLenient(false);
                    mData.putData(Constants.LENS + index, Constants.LENS_LAST_WASH_DATE, sdf.format(new Date()));
                }
                catch (Exception exc) {
                    Log.d(TAG, "putData failed");
                }
                setText(index);
                setLensNum();
                Toast.makeText(getApplicationContext(), "Insertion completed", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(Constants.RENEW_DATABASE_TO_SERVICE);
                intent.putExtra("lens_num", lensNum);
                sendBroadcast(intent);*/
                dismiss();
            }
        };
        newLensDialog.show();
    }

    public void onDelBtnClick(View v) {
        if(checkServiceRunning()) {
            Toast.makeText(getApplicationContext(), "Stop service and do this work", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = 0;
        switch(v.getId()) {
            case R.id.deleteBtn1 :
                index = 1;
                break;
            case R.id.deleteBtn2 :
                index = 2;
                break;
            case R.id.deleteBtn3 :
                index = 3;
                break;
        }
        if(!checkLens(index)) {
            return;
        }
        try {
            mData.delData(Constants.LENS + index, Constants.LENS_NAME);
            mData.delData(Constants.LENS + index, Constants.LENS_EXPIRATION_DATE);
            mData.delData(Constants.LENS + index, Constants.LENS_LAST_WASH_DATE);
            Toast.makeText(getApplicationContext(), "Deletion completed", Toast.LENGTH_SHORT).show();
            setTextClear(index);
            setLensNum();
            /*Intent intent = new Intent(Constants.RENEW_DATABASE_TO_SERVICE);
            intent.putExtra("lens_num", lensNum);
            sendBroadcast(intent);*/
        }
        catch (Exception exc) {
            Log.d(TAG, "delData failed");
        }
    }

    public void onWashBtnClick(View v) {
        if(!checkServiceRunning()) {
            Toast.makeText(getApplicationContext(), "Start service and do this work", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = 0;
        switch(v.getId()) {
            case R.id.washBtn1 :
                index = 1;
                break;
            case R.id.washBtn2 :
                index = 2;
                break;
            case R.id.washBtn3 :
                index = 3;
                break;
        }
        if(!checkLens(index)) {
            return;
        }
        if(index < 1) {
            return;
        }
        Intent intent = new Intent(Constants.COMMAND_WASHING_INTENT_FILTER);
        intent.putExtra("lens_number", index);
        sendBroadcast(intent);
    }

    public String calRestDay(int index) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd");
            sdf.setLenient(false);
            long diff = sdf.parse(mData.getData(Constants.LENS + index, Constants.LENS_EXPIRATION_DATE)).getTime() - sdf.parse(sdf.format(new Date().getTime())).getTime();
            if(diff < 0) {
                diff = -(diff + 86400000);
                expirationDateText[index - 1].setTextColor(Color.RED);
            }
            else {
                expirationDateText[index - 1].setTextColor(Color.BLACK);
            }
            return sdf1.format(diff) + " days";
        }
        catch (Exception exc) { }
        return null;
    }

    public String calRestTime(int index) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            long diff = sdf.parse(mData.getData(Constants.LENS + index, Constants.LENS_LAST_WASH_DATE)).getTime() + Constants.WASH_PERIOD - sdf.parse(sdf.format(new Date().getTime())).getTime();
            Log.d(TAG, "diff : " +diff);
            if(diff < 0) {
                diff = -diff;
                lastWashText[index - 1].setTextColor(Color.RED);
            }
            else {
                lastWashText[index - 1].setTextColor(Color.BLACK);
            }

            return String.valueOf(diff/(60*60*1000)) + " hours";
        }
        catch (Exception exc) { }
        return null;
    }

    public void setText(int index) {
        try {
            if(mData.getData(Constants.LENS + index, Constants.LENS_NAME).length() > 0) {
                lensTitleText[index - 1].setText(mData.getData(Constants.LENS + index, Constants.LENS_NAME));
                expirationDateText[index - 1].setText(mData.getData(Constants.LENS + index, Constants.LENS_EXPIRATION_DATE));
                lastWashText[index - 1].setText(mData.getData(Constants.LENS + index, Constants.LENS_LAST_WASH_DATE));
                expirationDateText[index - 1].append("\nexpired day : " + calRestDay(index));
                lastWashText[index - 1].append("\nremaining time : " + calRestTime(index));
            }
        }
        catch (Exception exc) {
            Log.d(TAG, "setText failed " + index);
        }
    }

    public void setTextClear(int index) {
        try {
            lensTitleText[index - 1].setText("");
            expirationDateText[index - 1].setText("");
            lastWashText[index - 1].setText("");
        }
        catch (Exception exc) {
            Log.d(TAG, "setText failed");
        }
    }

    public void showSleepTimeDialog() {
        ModifySleepTimeDialog modifySleepTimeDialog = new ModifySleepTimeDialog(this) {
            @Override
            public void onClick(View v) {
                try {
                    String sleepTime = getResult();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    sdf.setLenient(false);
                    Date date = sdf.parse(sleepTime);
                    mData.putData(Constants.SLEEP_TIME, Constants.SLEEP_TIME, sleepTime);
                    sleepTimeText.setText(sleepTime);
                    Toast.makeText(getApplicationContext(), "Modification succeeded", Toast.LENGTH_SHORT).show();
                    /*Intent sendIntent = new Intent(Constants.RENEW_SLEEP_TIME_INTENT_FILTER);
                    sendIntent.putExtra("sleep_time", sleepTime);
                    sendIntent.putExtra("lens_num", lensNum);
                    sendBroadcast(sendIntent);*/
                    dismiss();
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(), "Invalid time", Toast.LENGTH_SHORT).show();
                }
            }
        };
        modifySleepTimeDialog.show();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.changeBtn:
                if(checkServiceRunning()) {
                    Toast.makeText(getApplicationContext(), "Stop service and do this work", Toast.LENGTH_SHORT).show();
                    return;
                }
                showSleepTimeDialog();
                return;
            case R.id.serviceStartBtn:
                if (checkServiceRunning()) {
                    Toast.makeText(getApplicationContext(), "LensAlert service was started", Toast.LENGTH_SHORT).show();
                    return;
                }
                setLensNum();
                lensServiceIntent.putExtra("lens_number", lensNum);
                startService(lensServiceIntent);
                return;
            case R.id.serviceStopBtn :
                if(!checkServiceRunning()) {
                    Toast.makeText(getApplicationContext(), "LensAlert service wasn't started", Toast.LENGTH_SHORT).show();
                    return;
                }
                stopService(lensServiceIntent);
                Toast.makeText(getApplicationContext(), "LensAlert service stopped", Toast.LENGTH_SHORT).show();
                return;
        }
    }

    public boolean checkServiceRunning() {
        manager = (ActivityManager)this.getSystemService(Activity.ACTIVITY_SERVICE); // getting services from system
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.yangproject.iot.Core.LensAlertService".equals(service.service.getClassName())) { // find LensAlert service
                return true; // recommend that user make Encounter service stop before this work
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkServiceRunning()) {
            stopService(lensServiceIntent);
            Toast.makeText(getApplicationContext(), "LensAlert service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int i = 1; i < Constants.LENS_MAX_NUMBER; i++) {
            setText(i);
        }
    }

}
