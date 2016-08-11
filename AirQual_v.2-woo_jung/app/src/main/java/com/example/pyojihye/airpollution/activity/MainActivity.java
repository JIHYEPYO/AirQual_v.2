package com.example.pyojihye.airpollution.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pyojihye.airpollution.HttpConnection;
import com.example.pyojihye.airpollution.R;
import com.example.pyojihye.airpollution.bluetooth.BluetoothChatService;
import com.example.pyojihye.airpollution.bluetooth.BluetoothLeService;
import com.example.pyojihye.airpollution.bluetooth.DeviceConnector;
import com.example.pyojihye.airpollution.bluetooth.DeviceData;
import com.example.pyojihye.airpollution.bluetooth.SampleGattAttributes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import P_Adapter.History_Adapter;
import P_Adapter.Manage_Adapter;
import P_Adapter.Pager_Adapter;
import P_Data.AQI_Data;
import P_Data.Air_Data;
import P_Data.DBHelper;
import P_Data.Preference_Data;
import P_Data.Util_STATUS;
import P_Fragment.Fr_DeviceManagement;
import P_Fragment.Fr_H_G_Map;
import P_Fragment.Fr_Historychart_pager;
import P_Fragment.Fr_R_G_Map;
import P_Fragment.Fr_View_pager;
import P_Manager.Bluetooth_Manager;
import P_Manager.GMap_Manager;
import P_Manager.Gps_Manager;
import P_Manager.HGMap_Manager;
import P_Service.Air_Fake_Service;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GoogleMap gmap;
    SharedPreferences setting;
    //class
    public Gps_Manager gps_manager;
    public GMap_Manager gmap_manager;
    public HGMap_Manager hgmap_manager;
    private LocationManager locationManager;
    //Fragment 관련
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public static Pager_Adapter pa; //STATIC PAGER ADAPTER USING Viewpager add
    public static History_Adapter ha;
    public static Manage_Adapter ma;
    private static DeviceConnector connector;


    Preference_Data preference_data;


    Air_Fake_Service air_fake_service;
    SQLiteDatabase db;
    DBHelper helper;
    /*blue tooth*/


    /* 브루투스 */
    private static BluetoothAdapter myBluetoothAdapter;
    /*connector 에서 되돌아오는 리턴값으로 connector 됬는지 안됬는지등 확인*/
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECT =6;


    private static final int REQUEST_CONNECT_DEVICE_SECURE=1;
    static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT=3;
    Bluetooth_Manager bluetooth_manager;


    /**ble **/
    private boolean mConnected = false;
    private BluetoothLeService mBluetoothLeService;

    private static final int REQUEST_CONNECT_BLE=2;


    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    //SINGLE TON
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gps_manager = new Gps_Manager(getApplicationContext());
        setting=getSharedPreferences("setting",0);
        preference_data=new Preference_Data(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bluetooth_manager=new Bluetooth_Manager(bluetooth_Handler, polar_Handler);
        init();
        blue_tooth_init();
        instance = this;

        Fragment fr=new Fr_DeviceManagement();//수정 필요
        ma=new Manage_Adapter(getLayoutInflater(),getApplicationContext(),device_manage_handler);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.list_container, fr);
        fragmentTransaction.commit();

        // LinearLayout header=(LinearLayout)findViewById(R.id.nav_header);
        //((TextView)header.findViewById(R.id.emailtextView)).setText();
        //((TextView)header.findViewById(R.id.userid)).setText(this.getSharedPreferences("MAC",0).getString("useremail",""));

        //activity.getSharedPreferences("MAC",0).getString("userID","")
    }

    public static MainActivity getInstance() {
        return instance;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void blue_tooth_init()
    {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /* BLE */
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
    }

    public void init() {
        System.currentTimeMillis();
        //gps_manager = new Gps_Manager(getApplicationContext());

        //
        air_fake_service=new Air_Fake_Service(fake_handler);

        gmap_manager=new GMap_Manager(getApplicationContext());
        hgmap_manager=new HGMap_Manager(getApplicationContext());
        //Intent Service = new Intent(this, Air_Fake_Service.class);
        //startService(Service);

        //Intent Service = new Intent(this, MainService.class);
        //stopService(Service);
        //db_init();
        helper=new DBHelper(getApplicationContext());
        try
        {
            db=helper.getWritableDatabase();

        }catch (SQLiteException e)
        {
            db=helper.getReadableDatabase();
        }
        helper.Today_max_val();
        Set_AQI();


        //myBluetoothAdapter.getDefaultAdapter();


        //유닉스타임 시작값 - 84000
    }
    public static ArrayList<AQI_Data> AQI_ARRAY=new ArrayList<>();
    public void Set_AQI()
    {
        AQI_ARRAY.add(new AQI_Data("O3",0,54,0));
        AQI_ARRAY.add(new AQI_Data("O3",55,70,1));
        AQI_ARRAY.add(new AQI_Data("O3",71,85,2));
        AQI_ARRAY.add(new AQI_Data("O3",86,105,3));
        AQI_ARRAY.add(new AQI_Data("O3",106,200,4));
        AQI_ARRAY.add(new AQI_Data("PM",0,12,0));
        AQI_ARRAY.add(new AQI_Data("PM",12,35,1));
        AQI_ARRAY.add(new AQI_Data("PM",36,55,2));
        AQI_ARRAY.add(new AQI_Data("PM",56,150,3));
        AQI_ARRAY.add(new AQI_Data("PM",150,250,4));
        AQI_ARRAY.add(new AQI_Data("PM",250,350,5));
        AQI_ARRAY.add(new AQI_Data("PM",350,500,6));
        AQI_ARRAY.add(new AQI_Data("CO",0,4,0));
        AQI_ARRAY.add(new AQI_Data("CO",4,9,1));
        AQI_ARRAY.add(new AQI_Data("CO",9,12,2));
        AQI_ARRAY.add(new AQI_Data("CO",12,15,3));
        AQI_ARRAY.add(new AQI_Data("CO",15,30,4));
        AQI_ARRAY.add(new AQI_Data("CO",30,40,5));
        AQI_ARRAY.add(new AQI_Data("CO",40,50,6));
        AQI_ARRAY.add(new AQI_Data("SO2",0,35,0));
        AQI_ARRAY.add(new AQI_Data("SO2",36,75,1));
        AQI_ARRAY.add(new AQI_Data("SO2",76,185,2));
        AQI_ARRAY.add(new AQI_Data("SO2",186,304,3));
        AQI_ARRAY.add(new AQI_Data("SO2",305,604,4));
        AQI_ARRAY.add(new AQI_Data("SO2",605,804,5));
        AQI_ARRAY.add(new AQI_Data("SO2",805,1004,6));
        AQI_ARRAY.add(new AQI_Data("NO2",0,53,0));
        AQI_ARRAY.add(new AQI_Data("NO2",54,100,1));
        AQI_ARRAY.add(new AQI_Data("NO2",101,360,2));
        AQI_ARRAY.add(new AQI_Data("NO2",361,649,3));
        AQI_ARRAY.add(new AQI_Data("NO2",650,1249,4));
        AQI_ARRAY.add(new AQI_Data("NO2",1250,1649,5));
        AQI_ARRAY.add(new AQI_Data("NO2",1650,2049,6));



    }

    public void db_init(){
        //SQLiteDatabase db = this.openOrCreateDatabase("pims", MODE_PRIVATE, null);
        SQLiteDatabase db = this.openOrCreateDatabase("Air_data", MODE_PRIVATE, null);
        //db=this.openOrCreateDatabase("location",MODE_PRIVATE,null);
        String SQL_CREATE_TABLE_CONTACT =
                "CREATE TABLE IF NOT EXISTS Air_data ( "
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "regdate INTEGER, "
                        + "CO TEXT,"
                        + "SO2 TEXT,"
                        + "NO2 TEXT,"
                        + "O3 TEXT,"
                        + "PM TEXT"
                        + " )";
        db.execSQL(SQL_CREATE_TABLE_CONTACT);
        //String sql="INSERT INTO Air_data (regdate,CO,SO2,NO2,O3,PM) values (1234,'12','13','14','15','16');";
        String sql="INSERT INTO Air_data(regdate,CO) values(1234,'12','13','14','15','16');";
        db.execSQL(sql);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    int count=0;
    int count2=0;

    /* it's fake data so i can delete after next time*/
    Air_Data [] arr=new Air_Data[5];

    static int h_count=0;

    @SuppressWarnings("StatementWithEmptyBody")




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //클릭시마다 상태확인


        if (id == R.id.nav_main) {
            Util_STATUS.NAV_MENU_SELECT=1;
        } else if (id == R.id.nav_realtime_data) {
            Util_STATUS.NAV_MENU_SELECT=2;
            Fragment fr = new Fr_View_pager();

            pa = new Pager_Adapter(getLayoutInflater(), getApplicationContext(),((Fr_View_pager)fr).getV_viewpager());

            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.list_container, fr);
            fragmentTransaction.commit();
            //NAV_MENU_SELECT=0; //0 DEFAULT 1 MAIN 2 REALTIME DATA 3 CHART 4 REALTIME MAP 5 HISTORY MAP 6 DEVICEMANAGEMENT
            Air_Fake_Service.RECEIVE_DATA_STATUS=true;

        } else if (id == R.id.nav_chart) {
            Util_STATUS.NAV_MENU_SELECT=3;
            Fragment fr=new Fr_Historychart_pager();
            ha=new History_Adapter(getLayoutInflater(), getApplicationContext());

            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.list_container, fr);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_realtime_map) {
            Util_STATUS.NAV_MENU_SELECT=4;
            // MapFragment map=(MapFragment)getFragmentManager().findFragmentById(R.id.map);
            Fragment fr = new Fr_R_G_Map(gps_manager.get_LatLng());

            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.list_container, fr);
            fragmentTransaction.commit();
            P_Data.Util_STATUS.REAL_MAP_STATE=true;
        } else if (id == R.id.nav_history_map) {
            Util_STATUS.NAV_MENU_SELECT=5;
            Fragment fr=new Fr_H_G_Map(gps_manager.get_LatLng());

            fragmentManager=getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.list_container, fr);
            fragmentTransaction.commit();

            Air_Fake_Service.RECEIVE_DATA_STATUS=true;
            P_Data.Util_STATUS.REAL_MAP_STATE=true;


        } else if (id == R.id.nav_management) {
            Util_STATUS.NAV_MENU_SELECT=6;
            Fragment fr=new Fr_DeviceManagement();//수정 필요
            ma=new Manage_Adapter(getLayoutInflater(),getApplicationContext(),device_manage_handler);
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.list_container, fr);
            fragmentTransaction.commit();

            //Intent SettingDevcieActivity=new Intent(getApplicationContext(),SettingDeviceActivity.class);

            /* 기존에있던 ACTIVITY 재호출 */
            //SettingDevcieActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //startActivity(SettingDevcieActivity);

            //  Intent title = new Intent(getApplicationContext(),JnJTitle.class);

        }

        GMap_Manager.user_array.clear();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;

    }

    /**blue tooth**/
    //adapter status check
    boolean isAdapterReady() {
        return (myBluetoothAdapter != null) && (myBluetoothAdapter.isEnabled());
    }
    //bluetooth connect
    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(false);
        //((ToggleButton)findViewById(R.id.toggleButtonHeart)).setEnabled(false);
        if(Manage_Adapter.getInstance()!=null)
        {
            Manage_Adapter.getInstance().UDOOButton.setEnabled(false);
            Manage_Adapter.getInstance().HEARTButton.setEnabled(false);
        }
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
    //stop 요청
    private void stopConnection() {
        //stopconnection 요청

        if (connector != null) {
            connector.stop();
            connector = null;

        }
        else if(connector==null)
        {

        }
    }
    //우도보드 텍스트 네임 변경
    private final void setStatus(int resId) {
        if(Manage_Adapter.getInstance()!=null)
        {
            switch (Util_STATUS.SELECT_DEVICE)
            {
                case 0: //udoo
                {
                    Manage_Adapter.getInstance().UDOONAMETextview.setText(resId);
                    break;
                }

            }
        }
        //textViewUDOOName.setText(resId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);


                    //디바이스 등록 요청
                    Util_STATUS.HTTP_CONNECT_KIND=2;
                    HttpConnection httpConnectionRegDevice = new HttpConnection(this,getApplicationContext());
                    httpConnectionRegDevice.execute(preference_data.get_data("userID"),address);
                    DeviceData datas=new DeviceData(myBluetoothAdapter.getRemoteDevice((address)),"New Device"); //connected device
                    String devicename=(datas.getName() == null) ? datas.getAddress() :datas.getName();
                    switch (Util_STATUS.SELECT_DEVICE) //0 UDOO 1 HEART
                    {
                        //UDOO 선택 했을떄
                        case 0: {
                            preference_data.save_data("UDOONAME",devicename);
                            preference_data.save_data("UDOOMAC",address);
                            //text 수정
                            if(Manage_Adapter.getInstance()!=null)
                            {
                                Manage_Adapter.getInstance().UDOONAMETextview.setText(devicename);
                                Manage_Adapter.getInstance().UDOOMACTextview.setText(address);
                            }
                            break;
                        }
                        //HEART 선택 했을때

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
            case REQUEST_CONNECT_BLE:
                if(resultCode==RESULT_OK){


                    preference_data.save_data("HEARTNAME",data.getStringExtra("DEV"));
                    preference_data.save_data("HEARTMAC",data.getStringExtra("MAC"));
                        //text 수정
                    //1번으로 찍혀있지만 한번더
                    Util_STATUS.SELECT_DEVICE=1;
                    Util_STATUS.HTTP_CONNECT_KIND=2;
                    HttpConnection httpConnectionRegDevice = new HttpConnection(this,getApplicationContext());
                    httpConnectionRegDevice.execute(preference_data.get_data("userID"),data.getStringExtra("MAC"));
                    if(Manage_Adapter.getInstance()!=null)
                    {
                        Manage_Adapter.getInstance().HEARTNAMETextview.setText(data.getStringExtra("DEV"));
                        Manage_Adapter.getInstance().HEARTMACTEXTview.setText(data.getStringExtra("MAC"));

                    }
                    //http connection 요청
                }
                break;
        }
    }




    /*** handler **/
    //블루투스 connector 객체와 연결된 핸들러 연결이 됬는지 안됬는지 알아줌
    private final Handler mHandler = new Handler() {
        HttpConnection httpConnectionreqconn;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(mConnectedDeviceName);
                           // BTArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:

                            setStatus(R.string.title_connecting);
                            Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
                            //imageViewUdoo.setImageResource(R.drawable.udoo);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("BLUETOOTH READ MESSAGE",readMessage);
                    //BTArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
                case MESSAGE_TOAST:
                    //connect 객체에서 연결이 끊어졌다고 알려옴
                    //끊어지는건 udoo보드만
                    //disconnect
                    stopConnection();
                    if(Manage_Adapter.getInstance()!=null)
                    {
                        Manage_Adapter.getInstance().UDOOButton.setEnabled(true);
                        Manage_Adapter.getInstance().HEARTButton.setEnabled(true);

                    }
                    //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(true);
                    //((ToggleButton)findViewById(R.id.toggleButtonHeart)).setEnabled(true);
                    //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setChecked(true);
                    Toast.makeText(getApplicationContext(),"Socket connection failed",Toast.LENGTH_SHORT).show();

                    //HTTP 프로토콜 요청번호 3 커넥션 연결 or 해제
                    //1 해제
                    Util_STATUS.HTTP_CONNECT_KIND=3;  //http connection
                    Util_STATUS.REQ_CONNECTION_STATE=1; //0 connect 1 disconnect

                    /* request disconnet*/
                    httpConnectionreqconn =new HttpConnection(MainActivity.getInstance(),getApplicationContext());
                    //선택된 기기가 우도인지 하트인지
                    if(Util_STATUS.SELECT_DEVICE ==0)
                    {
                        if(Util_STATUS.UDOO_CONNECTION_ID!=0)
                        {
                            httpConnectionreqconn.execute(preference_data.get_data("UDOOdeviceID"));
                            //상수에있는 connection id 해제 요청
                           // httpConnectionreqconn.execute(String.valueOf(Util_STATUS.UDOO_CONNECTION_ID));
                        }

                        else
                        {
                            //Toast.makeText()
                        }
                        //선택된 기기가 하트이면
                    }
                    break;
                case MESSAGE_CONNECT: //Connection ok

                    if(Manage_Adapter.getInstance()!=null)
                    {
                        Manage_Adapter.getInstance().UDOOButton.setEnabled(true);
                        Manage_Adapter.getInstance().HEARTButton.setEnabled(true);
                    }
                    Manage_Adapter.getInstance().UDOONAMETextview.setText(preference_data.get_data("UDOONAME"));
                    Manage_Adapter.getInstance().UDOOImageview.setImageResource(R.drawable.udoo);
                    //connect 객체에서 연결됬다고 알려줌
                    //connection
                    //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setEnabled(true); //커넥션 연결 성공시 true
                    //((ToggleButton)findViewById(R.id.toggleButtonUdoo)).setChecked(false);

                    //prefrence 에서 맥을 꺼내와서 저장
                    //textViewUDOOName.setText(getSharedPreferences("MAC",0).getString("UDOONAME",""));

                    /* request to connection*/
                    //연결됬으니 커넥션 요청
                    //http 번호 3번 커넥트 상태 0번
                    Util_STATUS.HTTP_CONNECT_KIND=3;  //http connection (request)
                    Util_STATUS.REQ_CONNECTION_STATE=0; //request connection(connection)
                    httpConnectionreqconn = new HttpConnection(MainActivity.getInstance(),MainActivity.getInstance().getApplicationContext());
                    //httpConnectionreqconn =new HttpConnection(activity,getApplicationContext());
                    //연결된 기기가 0번이면 우도보드 커넥션 요청청
                    switch (Util_STATUS.SELECT_DEVICE)
                    {
                        case 0:
                        {
                            //UDOO DEVICEID OR HEART DIVCE ID
                            httpConnectionreqconn.execute(preference_data.get_data("UDOOdeviceID"));
                            break;
                        }
                        case 1:
                        {

                            httpConnectionreqconn.execute(preference_data.get_data("HEARTdeviceID"));
                            break;
                        }
                    }

                    break;
            }
        }
    };


    /*BLE*/
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
                if(Bluetooth_Manager.getInstance()!=null)
                {
                    Util_STATUS.HTTP_CONNECT_KIND=7;
                    //Bluetooth_Manager.getInstance().Set_hr_data(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }
                Util_STATUS.SELECT_DEVICE=1;
                Util_STATUS.HTTP_CONNECT_KIND=7;
                HttpConnection inputhrconnection=new HttpConnection(MainActivity.getInstance(),getApplicationContext());
                inputhrconnection.execute(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.d("dalgo","hr execute");
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

    //폴라 센서에서 들어오는 데이터
    private static final Handler polar_Handler =new Handler() //heartlate에서 오는 데이터
    {

        @Override
        public void handleMessage(Message msg) {
            //msg.getData().getString("polar");

            pa.rcm.Set_HRchart(msg.getData().getInt("heart"));
            pa.rm.Set_realtime(msg.getData().getInt("heart"));
            Log.d("heart in", "send heart chart");
            h_count=0;

            h_count++;

        }
    };
    //블루투스 매니저에서 들어오는 핸들러
    private static final Handler bluetooth_Handler =new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            //블루투스에서 들어온 데이터
            //NAV_MENU_SELECT=0; //0 DEFAULT 1 MAIN 2 REALTIME DATA 3 CHART 4 REALTIME MAP 5 HISTORY MAP 6 DEVICEMANAGEMENT
            //블루투스 매니저에서 여기로옴



            if(Util_STATUS.BLUETOOTH_RECEIVCE==0) //JSON
            {
                Air_Data ar=new Air_Data();
                try {
                    JSONObject jsonObject=new JSONObject(msg.getData().getString("data").trim());
                    //json을 Air_data로 파싱
                    //int time, int co2, int co, int so2, int no2, int pm2_5, int o3,LatLng latLng
                    //CO , NO2, O3 ,PM25,SO2,TEMP,TIME
                    ar.co=jsonObject.getInt("CO");
                    ar.no2=jsonObject.getInt("NO2");
                    ar.o3=jsonObject.getInt("O3");
                    ar.pm2_5=jsonObject.getInt("PM25");
                    ar.so2=jsonObject.getInt("SO2");
                    ar.temp=jsonObject.getInt("TEMP");
                    ar.time=jsonObject.getInt("TIME");
                    //Air_Data ar=new Air_Data(jsonObject.getInt("time"),jsonObject.getInt());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch(Util_STATUS.NAV_MENU_SELECT)
                {
                    case 0: //default fragment
                    {

                        break;
                    }

                    case 1: //main
                    {
                        break;
                    }
                    case 2: //real time data
                    {
                        //pa.rm.Set_realtime(ar);
                        //pa.rcm.Set_Realchart2(ar);
                        //pa.rcm.Set_Data(ar);
                        //pa.rcm.Set_Data_Color(ar);
                        break;
                    }
                    case 3: //history chart
                    {
                        break;
                    }
                    case 4: //realtime map
                    {
                        break;
                    }
                    case 5: //history map
                    {
                        break;
                    }
                    case 6://device management
                    {
                        break;
                    }
                }
                //string


            }
            else if(Util_STATUS.BLUETOOTH_RECEIVCE==2) //CSV
            {

            }

        }
    };

    private final Handler fake_handler=new Handler(){

        @Override
        public void handleMessage(Message msg) { //faek data

            if(count==0)
            {
                //hgmap_manager.addHeatMap();
            }
            //Bluetooth_Manager.getInstance().Set_Data(msg.getData());
            Air_Data ar=(Air_Data)msg.getData().getSerializable("data");
            if(P_Data.Util_STATUS.REAL_MAP_STATE) {
                if (ar != null) {
                    if (count == 4) {
                        if (count2 < 60) {
                            for (int i = 0; i < 4; i++) {
                                gmap_manager.Set_Circle2(arr[i], "im." + String.valueOf(i), arr[i].latLng);
                            }
                        } else {
                            for (int i = 0; i < 3; i++) {
                                gmap_manager.Set_Circle2(arr[i], "im." + String.valueOf(i), arr[i].latLng);
                            }
                        }
                        count = 0;

                        if (count2 > 44) {

                            //gmap_manager.Set_Circle2(arr[i],"cm"+String.valueOf(i),arr[i].latLng);
                            gmap_manager.Set_Circle2(arr[1], "cm." + String.valueOf(1), new LatLng(arr[1].latLng.latitude + 0.001, arr[1].latLng.longitude - 0.001));

                        }

                        gmap_manager.check_connection();
                    }
                    arr[count] = ar;
                    count++;
                    count2++;

                }
            }

            //pa.rm.Set_realtime(ar);
            //pa.rcm.Set_Realchart2(ar);
            //pa.rcm.Set_Data(ar);
            //pa.rcm.Set_Data_Color(ar);
            Util_STATUS.syncro_heart=1;
            // helper.onCreate(db);
            helper.Get_Gps_data();
            //helper.startOfDay();
            String sql=
                    "INSERT INTO Air_data(regdate,CO,SO2,NO2,O3,PM,Lat,Lon) values("+String.valueOf(System.currentTimeMillis()/1000)+","+String.valueOf(ar.co)+","
                            +String.valueOf(ar.so2)+","+String.valueOf(ar.no2)+","+String.valueOf(ar.o3)+","+String.valueOf(ar.pm2_5)+","
                            +String.valueOf(gps_manager.get_LatLng().latitude)+","+String.valueOf(gps_manager.get_LatLng().longitude)+
                            ");";

            db.execSQL(sql);






        }
    };
    private final Handler device_manage_handler =new Handler() //device management에서 들어오는곳
    {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
              /*
                    public static final int REQUEST_UDOO_CONNECT = 1; //UDOO 연결 요청
                    public static final int REQEUST_UDOO_DISCONNECT = 2; //UDOO 연결 해제 요청
                    public static final int REQUEST_UDOO_DEVICE_REGISTER=3; //기기 등록 요청
                    public static final int REQUEST_HEART_DEVICE_REGISTER=4; //기기 등록 요청
                    public static final int REQUST_HEART_CONNECT=5; //HEART 연결 요청
                    public static final int REQUEST_HEART_DISCONNECT=6; //HEART 연결 해제 요청

               */
                case Util_STATUS.REQUEST_UDOO_CONNECT: //우도 연결 요청 프리퍼런스에서 뒤져서 연결요청 ㄱㄱ
                {

                    //BluetoothDevice device = myBluetoothAdapter.getRemoteDevice((pref.getString("UDOOMAC", "")));

                    //Util_STATUS.SELECT_DEVICE=3;
                    Util_STATUS.SELECT_DEVICE =0;
                    Util_STATUS.HEART_CONNECTION_ID=3;
                    Util_STATUS.REQ_CONNECTION_STATE=0;
                    HttpConnection httpConnectionreqconn = new HttpConnection(MainActivity.getInstance(),MainActivity.getInstance().getApplicationContext());
                    httpConnectionreqconn.execute(preference_data.get_data("UDOOdeviceID"));

                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice( preference_data.get_data("UDOOMAC"));

                    setupConnector(device);


                    break;
                }
                case Util_STATUS.REQUEST_UDOO_DISCONNECT: //우도 연결 해제 요청 프리퍼런스에서 검색 ㄱ
                {
                    stopConnection();
                    break;
                }
                case Util_STATUS.REQUEST_UDOO_DEVICE_REGISTER: //우도 기기 등록 요청
                {
                    Intent serverIntent = new Intent(MainActivity.getInstance(),DeviceListActivity.class);
                    MainActivity.getInstance().startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE_SECURE);

                    break;
                }
                case Util_STATUS.REQUEST_HEART_DEVICE_REGISTER: //하트 기기 등록 요청
                {
                    //mconnected default false
                    //so true startactivityforesult
                    Util_STATUS.SELECT_DEVICE=1;
                    if(mConnected){

                    }else{
                        Intent intent = new Intent(MainActivity.getInstance(),BLEScanActivity.class);
                        startActivityForResult(intent,REQUEST_CONNECT_BLE);
                    }
                    break;
                }
                case Util_STATUS.REQUEST_HEART_CONNECT: //하트  커넥트 연결 요청 프리퍼런스 검색 ㄱㄱ
                {

                    mBluetoothLeService.connect(preference_data.get_data("HEARTMAC"));

                    if(Manage_Adapter.getInstance()!=null)
                    {
                        Manage_Adapter.getInstance().HEARTImageview.setImageResource(R.drawable.heartrate);

                    }
                    Util_STATUS.SELECT_DEVICE =1; //HEART
                    Util_STATUS.HTTP_CONNECT_KIND=3; //CONNECTION
                    Util_STATUS.REQ_CONNECTION_STATE=0; //CONNECTION 1 is DISCONNECTION
                    HttpConnection httpConnectionreqconn =new HttpConnection(MainActivity.getInstance(),getApplicationContext());
                    httpConnectionreqconn.execute(preference_data.get_data("HEARTdeviceID"));


                    break;
                }
                case Util_STATUS.REQUEST_HEART_DISCONNECT: //하트 연결 해제 요청
                {

                    mBluetoothLeService.disconnect();
                    if(Manage_Adapter.getInstance()!=null)
                    {
                        Manage_Adapter.getInstance().HEARTImageview.setImageResource(R.drawable.heartrate0);

                    }
                    if(Util_STATUS.HEART_CONNECTION_ID!=0)
                    {
                        Util_STATUS.SELECT_DEVICE =1; //HEART
                        Util_STATUS.HTTP_CONNECT_KIND=3;
                        ; //CONNECTION
                        Util_STATUS.REQ_CONNECTION_STATE=1; //CONNECTION 1 is DISCONNECTION
                        HttpConnection httpConnectionreqconn =new HttpConnection(MainActivity.getInstance(),getApplicationContext());
                        httpConnectionreqconn.execute(preference_data.get_data("HEARTdeviceID"));

                    }

                    break;
                }
            }
            super.handleMessage(msg);
        }
    };
}
