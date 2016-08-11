package P_Manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import P_Data.Util_STATUS;

/**
 * Created by user on 2016-08-08.
 */
public class Bluetooth_Manager {
    private static Bluetooth_Manager uniqueInstance;
    static Handler mHandler;
    static Handler pHandler;
    public Bluetooth_Manager(Handler handler,Handler handler2) {
        mHandler=handler;
        pHandler=handler2;
    }
    public static Bluetooth_Manager getInstance() {


        return uniqueInstance;
    }
    public static void Set_Data(Bundle bundle) //Connector에서 메인에 달린 핸들러로 전송하는 함수
    { //3초에 한번
        // //NAV_MENU_SELECT=0; //0 DEFAULT 1 MAIN 2 REALTIME DATA 3 CHART 4 REALTIME MAP 5 HISTORY MAP 6 DEVICEMANAGEMENT
        Message msg=new Message();
        msg.setData(bundle);

        if(mHandler!=null)
        {   if(Util_STATUS.NAV_MENU_SELECT==2)
            {
            mHandler.sendMessage(msg);
                Util_STATUS.syncro_heart=1;
            }

        }

    }
    public static void Set_hr_data(String var) //1초에 한번
    {
        String s=var;
        Bundle bundle=new Bundle();
        bundle.putInt("heart",Integer.parseInt(var));
        Message msg=new Message();
       msg.setData(bundle);
        //msg.setData();

        if(pHandler!=null)
        {
            if(Util_STATUS.syncro_heart==1) //1이면 보냄
            {
                if(Util_STATUS.NAV_MENU_SELECT==2) {
                    pHandler.sendMessage(msg);
                    Util_STATUS.syncro_heart=0;
                }
                }
        }
    }
}
