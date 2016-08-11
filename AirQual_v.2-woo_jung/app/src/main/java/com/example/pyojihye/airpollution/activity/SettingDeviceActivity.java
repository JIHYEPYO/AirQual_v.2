package com.example.pyojihye.airpollution.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.pyojihye.airpollution.HttpConnection;
import com.example.pyojihye.airpollution.R;
import com.example.pyojihye.airpollution.bluetooth.BluetoothChatService;
import com.example.pyojihye.airpollution.bluetooth.BluetoothLeService;
import com.example.pyojihye.airpollution.bluetooth.DeviceConnector;
import com.example.pyojihye.airpollution.bluetooth.DeviceData;
import com.example.pyojihye.airpollution.bluetooth.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import P_Data.Util_STATUS;
import P_Manager.Bluetooth_Manager;
import P_Manager.Gps_Manager;

/**
 * Created by PYOJIHYE on 2016-08-03.
 */
public class SettingDeviceActivity extends AppCompatActivity {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECT =6;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    SharedPreferences pref;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> BTArrayAdapter;
    private LocationManager locationManager;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    public  static DeviceConnector connector;

    //*BLE*//
    private boolean mConnected = false;
    private BluetoothLeService mBluetoothLeService;
    private static final int REQUEST_CONNECT_DEVICE_SECURE=1;
    private static final int REQUEST_CONNECT_BLE=2;
    private static final int REQUEST_ENABLE_BT=3;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public TextView textViewUDOOName;
    public TextView textViewUDOOMac;
    public ImageView imageViewUdoo;

    public TextView textViewHEARTName;
    public TextView textViewHEARTMac;
    public ImageView imageViewHEART;

    public Activity activity;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        setContentView(R.layout.activity_setting_device);

        //GMap_Manager gmap_manager=new GMap_Manager(getApplicationContext());
        Gps_Manager gps_manager=new Gps_Manager(getApplicationContext());
        //BTArrayAdapter
        textViewUDOOName = (TextView) findViewById(R.id.TextViewUDOO);
        textViewUDOOMac = (TextView) findViewById(R.id.TextViewUDOOMac);
        imageViewUdoo=(ImageView) findViewById(R.id.imageViewUdoo);
        textViewHEARTName = (TextView) findViewById(R.id.TextViewHR);
        textViewHEARTMac = (TextView) findViewById(R.id.TextViewHRMac);
        imageViewHEART=(ImageView) findViewById(R.id.imageViewHeart);
        ToggleButton toggleButton=(ToggleButton)findViewById(R.id.toggleButtonUdoo);
        //toggleButton.setChecked(true);

        toggleButton.setOnClickListener(onClickListener);
        toggleButton=(ToggleButton)findViewById(R.id.toggleButtonHeart);

        //toggleButton.setChecked(true);
        toggleButton.setOnClickListener(onClickListener);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());

        if(myBluetoothAdapter==null){
            Toast.makeText(this,"Bluetooth is not available",Toast.LENGTH_LONG).show();
        }else{
            gpsOn();
            blueToothOn();
        }
        /* 프리퍼런스 검색후 heart mac이나 udoomac이 있으면 등록*/
        pref=getSharedPreferences("MAC",0);
        if(!pref.getString("HEARTMAC","").equals(""))
        {
            textViewHEARTMac.setText(pref.getString("HEARTMAC",""));
            textViewHEARTName.setText(pref.getString("HEARTNAME",""));
        }
        else if(pref.getString("HEARTMAC","").equals(""))
        {
            textViewHEARTName.setText("Heart Rate");
            textViewHEARTMac.setText("NOT CONNECTED");
        }
        if(!pref.getString("UDOOMAC","").equals(""))
        {
            textViewUDOOName.setText(pref.getString("UDOONAME",""));
            textViewUDOOMac.setText(pref.getString("UDOOMAC",""));
        }
        else if(pref.getString("UDOOMAC","").equals(""))
        {
            textViewUDOOName.setText("UDOO BOARD");
            textViewUDOOMac.setText("NOT CONNECTED");
        }
        Button buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent = new Intent(SettingDeviceActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MANAGEMENT 메뉴 클릭시 다시 액티비티를불러서 기존 커넥션확인후
        //여러가지 옵션확인후 그대로 붙여넣을 예정
        if(SettingDeviceActivity.connector!=null) //연결이 있을시
        {
            textViewHEARTMac.setText(pref.getString("HEARTMAC",""));

        }

    }

    public void blueToothOn() {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent IntentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(IntentEnable, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    public void gpsOn() {
        if(android.os.Build.VERSION.SDK_INT < 23){
            if(locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS is already on", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Please check the GPS use.", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                } else {
                    Toast.makeText(this, "GPS is already on", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }
        }
    }

    public void onImageViewUDOOClick(View view){

        //선택된 기기가 우도보드
        Util_STATUS.SELECT_DEVICE =0; //UDOO

        if (isConnected()) {
            stopConnection();
        }
        else{
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        }
    }

    public void onImageViewHRClick(View view){

        //선택된 기기가 하트레이트
        Util_STATUS.SELECT_DEVICE =1; //HEART

        if(mConnected){
            textViewHEARTName.setText("Heart Rate");
            textViewHEARTMac.setText("00:00:00:00:00");
            imageViewHEART.setImageResource(R.drawable.heartrate0);

        }else{
            Intent intent = new Intent(this,BLEScanActivity.class);
            startActivityForResult(intent,REQUEST_CONNECT_BLE);
        }

    }

    private final void setStatus(int resId) {
        textViewUDOOName.setText(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        textViewUDOOMac.setText(subTitle);
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            setStatus(mConnectedDeviceName);
                            BTArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
                            imageViewUdoo.setImageResource(R.drawable.udoo);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //BTArrayAdapter.add("Me:  " + writeMessage);  Adatper null 오류 ?
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    BTArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to " + getSharedPreferences("MAC",0).getString("UDOONAME") Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    //connect 객체에서 연결이 끊어졌다고 알려옴
                    //disconnect
                    stopConnection();
                    ((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(true);
                    ((ToggleButton)findViewById(R.id.toggleButtonHeart)).setEnabled(true);
                    ((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setChecked(true);
                    Toast.makeText(getApplicationContext(),"Socket connection failed",Toast.LENGTH_SHORT).show();

                    //HTTP 프로토콜 요청번호 3 커넥션 연결 or 해제
                    //1 해제
                    Util_STATUS.HTTP_CONNECT_KIND=3;  //http connection
                    Util_STATUS.REQ_CONNECTION_STATE=1; //0 connect 1 disconnect

                    /* request disconnet*/
                    HttpConnection httpConnectionreqconn =new HttpConnection(activity,getApplicationContext());
                    //선택된 기기가 우도인지 하트인지
                    if(Util_STATUS.SELECT_DEVICE ==0)
                    {
                        if(Util_STATUS.UDOO_CONNECTION_ID!=0)
                        {
                            //httpConnectionreqconn.execute(activity.getSharedPreferences("MAC",0).getString("UDOOdeviceID",""));
                            //상수에있는 connection id 해제 요청
                            httpConnectionreqconn.execute(String.valueOf(Util_STATUS.UDOO_CONNECTION_ID));
                        }

                        else
                        {
                            //Toast.makeText()
                        }
                        //선택된 기기가 하트이면
                    }else if(Util_STATUS.SELECT_DEVICE ==1)
                    {

                        if(Util_STATUS.HEART_CONNECTION_ID!=0)
                        {
                            //httpConnectionreqconn.execute(activity.getSharedPreferences("MAC",0).getString("UDOOdeviceID",""));
                            httpConnectionreqconn.execute(String.valueOf(Util_STATUS.HEART_CONNECTION_ID));
                        }
                        else
                        {

                        };
                    }

                    break;
                case MESSAGE_CONNECT: //Connection ok

                    //connect 객체에서 연결됬다고 알려줌
                    //connection
                    ((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(true); //커넥션 연결 성공시 true
                    ((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setChecked(false);

                    //prefrence 에서 맥을 꺼내와서 저장
                    textViewUDOOName.setText(getSharedPreferences("MAC",0).getString("UDOONAME",""));

                    /* request to connection*/
                    //연결됬으니 커넥션 요청
                    //http 번호 3번 커넥트 상태 0번
                    Util_STATUS.HTTP_CONNECT_KIND=3;  //http connection (request)
                    Util_STATUS.REQ_CONNECTION_STATE=0; //request connection(connection)
                    httpConnectionreqconn =new HttpConnection(activity,getApplicationContext());
                    //연결된 기기가 0번이면 우도보드 커넥션 요청청
                    if(Util_STATUS.SELECT_DEVICE ==0)
                    {
                        httpConnectionreqconn.execute(activity.getSharedPreferences("MAC",0).getString("UDOOdeviceID",""));
                        //연결된 기기가 1번이면 하트레이트 커넥션 요청
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                //기기선택 성공 디바이스 레지스터 요청
                if (resultCode == Activity.RESULT_OK)
                {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    Util_STATUS.HTTP_CONNECT_KIND=2; //2 = REGISTER DEVICE

                    HttpConnection httpConnectionRegDevice = new HttpConnection(this,getApplicationContext());
                    httpConnectionRegDevice.execute(  this.getSharedPreferences("MAC",0).getString("userID",""),address);// user id , address
                    //디바이스 등록 요청하기전 prefernce에 저장후 setText
                    switch (Util_STATUS.SELECT_DEVICE) //0 UDOO 1 HEART
                    {
                        case 0:
                        {
                            pref = getSharedPreferences("MAC", 0);
                            //save mac address
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("UDOOMAC", address);
                            editor.commit();
                            //save name
                            DeviceData datas=new DeviceData(myBluetoothAdapter.getRemoteDevice((pref.getString("UDOOMAC",""))),"New Device"); //connected device
                            String devicename=(datas.getName() == null) ? datas.getAddress() :datas.getName();
                            editor.putString("UDOONAME",devicename);

                            editor.commit();
                            textViewUDOOName.setText(datas.getName());
                            textViewUDOOMac.setText(address);
                            break;
                        }
                        case 1:
                        {
                            //Preferences에 저장

                        }
                    }

                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                //BLE 커넥션을 웹에 보내야할듯?
            case REQUEST_CONNECT_BLE:
                if(resultCode==RESULT_OK){

                    pref=getSharedPreferences("MAC",0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("HEARTNAME", data.getStringExtra("DEV"));
                    editor.putString("HEARTMAC",data.getStringExtra("MAC"));

                    editor.commit();
                    textViewHEARTName.setText(data.getStringExtra("DEV"));
                    textViewHEARTMac.setText(data.getStringExtra("MAC"));
                    imageViewHEART.setImageResource(R.drawable.heartrate);
                }
                break;


        }
    }

    boolean isAdapterReady() {
        return (myBluetoothAdapter != null) && (myBluetoothAdapter.isEnabled());

    }

    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        ((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(false);
        ((ToggleButton)findViewById(R.id.toggleButtonHeart)).setEnabled(false);
        try {
            String emptyName = getString(R.string.empty_device_name);
            // deviceName = (deviceData.getName() == null) ? deviceData.getAddress() : deviceData.getName();
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);

            connector.connect();
            //connection 요청
        } catch (IllegalArgumentException e) {
            Toast.makeText(this,"Bluetooth connection failed",Toast.LENGTH_LONG).show();

        }
    }

    private void stopConnection() {
        //stopconnection 요청

        if (connector != null) {
            connector.stop();
            connector = null;
            textViewUDOOName.setText(getSharedPreferences("MAC",0).getString("UDOONAME",""));

            //DeviceListActivity.macaddress="NOT CONNECTED";
            imageViewUdoo.setImageResource(R.drawable.udoo0);


        }
        else if(connector==null)
        {
            //textViewUDOOName.setText("UDOO Board");
            //Toast.makeText(this,"have not connection",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    ToggleButton.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.toggleButtonUdoo:
                {

                    //udoo 연결

                    Util_STATUS.SELECT_DEVICE =0; //UDOO
                    ToggleButton toggleButton=(ToggleButton)findViewById(R.id.toggleButtonUdoo);
                    //이름이 해제일떄 연결
                    //이름이 연결일떄 해제
                    // toggleButton.get
                    if(toggleButton.getText().equals("해제")) //눈으로 보이는건 사용인데 누를시에 해제로 바껴서 해제로 인식해야함
                    {
                        BluetoothDevice device = myBluetoothAdapter.getRemoteDevice((pref.getString("UDOOMAC", "")));
                        setupConnector(device);
                        //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setChecked(true);
                        //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(false);
                        // stopConnection();
                    }
                    else if(toggleButton.getText().equals("연결")) //눈으로보이는건 해제인데 누를시에 사용으로 바껴서 사용으로 인식해야함
                    {
                        stopConnection();
                        Toast.makeText(activity.getApplicationContext(),"Disconnect success",Toast.LENGTH_SHORT);

                    }

                    break;
                }
                case R.id.toggleButtonHeart:
                {


                    mBluetoothLeService.connect(getSharedPreferences("MAC",0).getString("HEARTMAC",""));
                    //  pref=getSharedPreferences("MAC",0);
                    break;
                }
            }
        }
    };

    /**BLE**/
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mac.getText().toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //val 이 데이터받는곳
                Bluetooth_Manager.getInstance().Set_hr_data(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "<UNKNOWN SERVICE>";
        String unknownCharaString = "<UNKNOWN CHARACTERISTIC>";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        if (mGattCharacteristics != null) {
            final BluetoothGattCharacteristic characteristic =
                    mGattCharacteristics.get(3).get(0);
            final int charaProp = characteristic.getProperties();

            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }

            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit the application?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //process kill
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}