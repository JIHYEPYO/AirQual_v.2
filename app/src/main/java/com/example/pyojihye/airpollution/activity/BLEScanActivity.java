package com.example.pyojihye.airpollution.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pyojihye.airpollution.R;

import java.util.Set;

/**
 * Created by PYOJIHYE on 2016-08-10.
 */
public class BLEScanActivity extends Activity {

    private Button scanButton = null;
    private ListView pairedListView = null;
    private ListView scannedListView = null;

    private ArrayAdapter<String> pairedAdapter = null;
    private ArrayAdapter<String> scannedAdapter = null;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private String mDeviceName;
    public String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_ble_scan);
        setResult(RESULT_CANCELED);

        initialize();
        displayPairedDevices();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                scannedAdapter.add(device.getName()+'\n'+device.getAddress());
            }
        }
    };

    private void initialize() {
        scanButton = (Button)findViewById(R.id.button_scan);

        pairedListView = (ListView)findViewById(R.id.lv_paired);
        scannedListView = (ListView)findViewById(R.id.lv_scanned);

        pairedAdapter = new ArrayAdapter<String>(this, R.layout.adapter_item_bluetooth_device, R.id.item_text);
        scannedAdapter = new ArrayAdapter<String>(this, R.layout.adapter_item_bluetooth_device, R.id.item_text);

        pairedListView.setAdapter(pairedAdapter);
        scannedListView.setAdapter(scannedAdapter);

        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String mac = pairedAdapter.getItem(position);
                mac=mac.substring(mac.length()-17, mac.length());
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);

                mDeviceAddress = device.getAddress();
                mDeviceName = device.getName();

                Intent intent = new Intent();
                intent.putExtra("MAC",mDeviceAddress);
                intent.putExtra("DEV",mDeviceName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        scannedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String mac = scannedAdapter.getItem(position);
                mac=mac.substring(mac.length()-17, mac.length());
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);

                mDeviceAddress = device.getAddress();
                mDeviceName = device.getName();
                //
                //User_Info.HRName=mDeviceName;
                //User_Info.HRMac=mDeviceAddress;
                SharedPreferences pref=getSharedPreferences("MAC",0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("HEARTNAME", mDeviceName);
                editor.putString("HEARTMAC",mDeviceAddress);
                editor.commit();

                Intent intent = new Intent();
                intent.putExtra("MAC",mDeviceAddress);
                intent.putExtra("DEV",mDeviceName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void displayPairedDevices() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
        for (BluetoothDevice device : devices) {
            pairedAdapter.add(device.getName()+'\n'+device.getAddress());
        }

        pairedAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mBluetoothAdapter.isDiscovering()) {
            scanButton.setText("SCAN");
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        return intentFilter;
    }

    public void onClickScan(View view) {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            scanButton.setText(R.string.button_scan);
            mBluetoothAdapter.cancelDiscovery();
        }
        else {
            findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
            scannedAdapter.clear();
            scanButton.setText("STOP");
            mBluetoothAdapter.startDiscovery();
        }
    }
}
