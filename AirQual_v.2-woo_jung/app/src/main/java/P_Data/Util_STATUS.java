package P_Data;

import java.util.ArrayList;

/**
 * Created by user on 2016-08-02.
 */
public class Util_STATUS {


    //public static
    public static String GMap_Realtime_set="CO"; //show google real time circle data
    public static boolean AIR_V_CONDITION=false;
    public static boolean BLUETOOTH_CONNECTION=false;
    public static boolean REAL_MAP_STATE=false;
    public static String Chart_Select="CO"; //Show data chart select
    public static boolean RECEIVE_DATA_STATUS=false;
    public static boolean HAVE_UDOODeviceID=false;   //Save Udoo mac to preference
    public static boolean HAVE_HEARTDeviceID=false; //Save heart mac to preference
    public static int SELECT_DEVICE =0; //0 UDOO 1 HEART
    public static int HTTP_CONNECT_KIND=0; //0 DEFAULT 1 SIGN IN& SIGN UP  2 DEVICE REGISTER // 3 CONNECTION REQUEST
    //4 RESPONSE HISTORY DATA 5 GET REAL TIME USER DATA 6 INPUT AIR DATA 7 INPUT HR DATA
    //7 INPUT HR DATA 8 INPUT CSV DATA
    public static int BLUETOOTH_RECEIVCE=0; //ready to receive bluetooth data
    //0 not ready 1 json 2 csv
    public static int REQ_CONNECTION_STATE=0; //0 REQUEST CONNECTION 1 REQUEST DISCONNETION
    //public static boolean
    public static int NAV_MENU_SELECT=0; //0 DEFAULT 1 MAIN 2 REALTIME DATA 3 CHART 4 REALTIME MAP 5 HISTORY MAP 6 DEVICEMANAGEMENT
    public static int UDOO_CONNECTION_ID=0; //UDOO CONNECTION ID
    public static int HEART_CONNECTION_ID=0; //HEART CONNECTION ID
    public static int syncro_heart=0; //0 면 대기 1이면 보냄  heartlate랑 공기데이터 동기화용
    public ArrayList<Integer> heartrate=new ArrayList<>();
    public static boolean HELP_VIEW=false;

    /*************
     *
     *프래그먼트 페이저 어댑터에있는 디바이스 매니지먼트에서 메인액티비티로 날리는 요청
     * (블루투스 연결,해제 폴라 연결 해제 등)
     *
     * **************/
    public static final int REQUEST_UDOO_CONNECT = 1; //UDOO 연결 요청
    public static final int REQUEST_UDOO_DISCONNECT = 2; //UDOO 연결 해제 요청
    public static final int REQUEST_UDOO_DEVICE_REGISTER=3; //기기 등록 요청
    public static final int REQUEST_HEART_DEVICE_REGISTER=4; //기기 등록 요청
    public static final int REQUEST_HEART_CONNECT=5; //HEART 연결 요청
    public static final int REQUEST_HEART_DISCONNECT=6; //HEART 연결 해제 요청

    public Util_STATUS() {

    }


}
