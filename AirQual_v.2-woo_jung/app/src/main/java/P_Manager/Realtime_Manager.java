package P_Manager;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.pyojihye.airpollution.R;

import P_Data.Air_Data;

/**
 * Created by user on 2016-07-25.
 */
public class Realtime_Manager {
    View realtime_view;

    public Realtime_Manager(View view) {
        this.realtime_view=view;
    }
    public void Set_empty()
    {
        TextView local=(TextView)realtime_view.findViewById(R.id.CO_data);
        local.setText("NA");
        local=(TextView)realtime_view.findViewById(R.id.SO2_data);
        local.setText("NA");
        local=(TextView)realtime_view.findViewById(R.id.NO2_data);
        local.setText("NA");

        local=(TextView)realtime_view.findViewById(R.id.O3_data);
        local.setText("NA");


        local=(TextView)realtime_view.findViewById(R.id.temp_data);
        local.setText("NA");

        local=(TextView)realtime_view.findViewById(R.id.PM_data);
        local.setText("NA");
        local=(TextView)realtime_view.findViewById(R.id.heart_text);
        local.setText("NA");
    }
    int count=0;
    public void Set_realtime(Air_Data air)
    {
        count++;
        if(count==2)
        {
            count++;
        }
        TextView local=(TextView)realtime_view.findViewById(R.id.CO_data);
        set_background(String.valueOf(air.co),local);
        local=(TextView)realtime_view.findViewById(R.id.SO2_data);
        set_background(String.valueOf(air.so2),local);

        local=(TextView)realtime_view.findViewById(R.id.NO2_data);
        set_background(String.valueOf(air.no2),local);


        local=(TextView)realtime_view.findViewById(R.id.O3_data);
        set_background(String.valueOf(air.o3),local);


        local=(TextView)realtime_view.findViewById(R.id.temp_data);
        set_background(String.valueOf(air.co),local);


        local=(TextView)realtime_view.findViewById(R.id.PM_data);
        set_background(String.valueOf(air.pm2_5),local);

    }
    public void Set_realtime(int heart)
    {
        TextView local=(TextView)realtime_view.findViewById(R.id.heart_text);
        local.setText(String.valueOf(heart));
    }
    public void set_background(String s, TextView local)
    {
        //80E12A          FAEB78          CD1F48
        local.setText(s);
        int i= Integer.valueOf(s);
        if(i>=0&&i<=50)
        {

            local.setTextColor(Color.parseColor("#FF02E402"));
        }
        else if (51<=i&&i<=100)
        {

            local.setTextColor(Color.parseColor("#FFFFFF02"));
        }
        else if (101<=i&&i<=150)
        {

            local.setTextColor(Color.parseColor("#FFFF7F02"));
        }
        else if (151<=i&&i<=200)
        {

            local.setTextColor(Color.parseColor("#FFFF0202"));
        }
        else if (201<=i&&i<=300)
        {

            local.setTextColor(Color.parseColor("#FF904098"));
        }

        else if (301<=i&&i<=500)
        {

            local.setTextColor(Color.parseColor("#FF7F0225"));
        }

    }

}
