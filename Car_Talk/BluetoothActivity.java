package com.yangproject.embeddedproject.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangproject.embeddedproject.Others.BluetoothService;
import com.yangproject.embeddedproject.Others.Constants;
import com.yangproject.embeddedproject.R;

import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothService mBluetoothService;
    private BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName;
    private static final int REQUEST_ENABLE_BT = 2;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth);

        mBluetoothService = BluetoothService.getInstance(mHandler);
        mBluetoothAdapter = mBluetoothService.getBluetoothAdapter();

        // for bluetooth availability
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // for paired devices list
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_item);
        setPairedDevicesList();

        // for scanning available bluetooth devices
        Button scanBtn = (Button)findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    if(mNewDevicesArrayAdapter != null) {
                        mNewDevicesArrayAdapter.clear();
                    }
                }
                catch (Exception exc) { }
                doDiscovery();
            }
        });
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_item);
        ListView newDevicesListView = (ListView)findViewById(R.id.newDevicesListView);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    public void setPairedDevicesList() {
        try {
            // for paired devices list
            mPairedDevicesArrayAdapter.clear();
            ListView pairedListView = (ListView)findViewById(R.id.devicesListView);
            pairedListView.setAdapter(mPairedDevicesArrayAdapter);
            pairedListView.setOnItemClickListener(mDeviceClickListener);
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for(BluetoothDevice device : pairedDevices) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
            else {
                mPairedDevicesArrayAdapter.add("No devices");
            }
        }
        catch (Exception exc) {
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setEnableListView(boolean flag) {
        findViewById(R.id.devicesListView).setEnabled(flag);
        findViewById(R.id.newDevicesListView).setEnabled(flag);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            if(mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            String info = ((TextView)v).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            setEnableListView(false);
            mBluetoothService.connect(device);
        }
    };

    private void doDiscovery() {
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        Toast.makeText(getApplicationContext(), "Start discovery", Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        super.onStart();
        mBluetoothService = BluetoothService.getInstance(mHandler);
        if(!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        setPairedDevicesList();
        setEnableListView(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT :
                if(resultCode != Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "This application must use bluetooth.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        if (mBluetoothService != null) {
            mBluetoothService.stop("Try to login again");
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MESSAGE_STATE_CHANGE :
                    switch(msg.arg1) {
                        case BluetoothService.STATE_CONNECTED :
                            Toast.makeText(getApplication(), "Success to connect the device", Toast.LENGTH_SHORT).show();
                            Intent messageIntent = new Intent(getApplicationContext(), MessageSendActivity.class);
                            messageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(messageIntent);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(getApplication(), "Connecting...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_NONE:
                            Toast.makeText(getApplicationContext(), "Try to connect a device", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    setEnableListView(true);
                    break;
            }
        }
    };
}
