package P_Data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 2016-08-11.
 */
public class Preference_Data extends Activity {

    //UDOONAME
    //UDOOMAC
    //HEARTNAME
    //HEARTMAC

    //UDOOdeviceID
    //HEARTdeviceID

    //UDOONAME
    //UDOOMAC
    //HEARTNAME
    //HEARTMAC
    Context mContext;

    SharedPreferences pref;
    public  Preference_Data(Context mContext) {
        pref = mContext.getSharedPreferences("MAC", 0);
        this.mContext=mContext;
    }
    public  void save_data(String key,String save_data)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,save_data);
        editor.commit();

    }
    public String get_data(String key)
    {
        //getSharedPreferences("MAC",0).getString("userID","")

        return mContext.getSharedPreferences("MAC",0).getString(key,"");
    }
}
