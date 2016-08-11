package P_Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.pyojihye.airpollution.R;

import P_Data.Util_STATUS;
import P_Fragment.Fr_View_pager;
import P_Manager.Realchart_Manager;
import P_Manager.Realtime_Manager;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;


/**
 * Created by user on 2016-08-02.
 */
public class Pager_Adapter extends PagerAdapter {

    LayoutInflater inflater;
    public static Context context;
    Handler pHandler;
    View realtime_view;
    View realchart_view;
    VerticalViewPager verticalViewPager;
    public static Realtime_Manager rm;
    public static Realchart_Manager rcm;

    public Pager_Adapter(LayoutInflater inflater, Context context, VerticalViewPager verticalViewPager)
    {

        this.inflater=inflater;
        this.context=context;
        this.verticalViewPager=verticalViewPager;
        //this.pHandler=adapter_handler;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view=null;

        switch(position) {

            case 0:
            {

                view=inflater.inflate(R.layout.air_realtime,null);
                realtime_view=view;
                rm=new Realtime_Manager(view);

                FrameLayout fr=(FrameLayout)view.findViewById(R.id.CO_Frame);
                fr.setOnClickListener(listener);
                fr=(FrameLayout)view.findViewById(R.id.NO2_Frame);
                fr.setOnClickListener(listener);

                fr=(FrameLayout)view.findViewById(R.id.O3_Frame);
                fr.setOnClickListener(listener);
                fr=(FrameLayout)view.findViewById(R.id.PM_Frame);
                fr.setOnClickListener(listener);
                fr=(FrameLayout)view.findViewById(R.id.SO2_Frame);
                fr.setOnClickListener(listener);
                fr=(FrameLayout)view.findViewById(R.id.TEMP_Frame);
                fr.setOnClickListener(listener);

                container.addView(view);

                break;
            }
            case 1:
            {
                view=inflater.inflate(R.layout.air_realchart,null);
                rcm=new Realchart_Manager(view,context);
                realchart_view=view;
                Button btn=(Button)realchart_view.findViewById(R.id.realchart_co_button);
                btn.setOnClickListener(air_listener);
                btn=(Button)realchart_view.findViewById(R.id.realchart_so2_button);
                btn.setOnClickListener(air_listener);
                btn=(Button)realchart_view.findViewById(R.id.realchart_no2_button);
                btn.setOnClickListener(air_listener);
                btn=(Button)realchart_view.findViewById(R.id.realchart_o3_button);
                btn.setOnClickListener(air_listener);
                btn=(Button)realchart_view.findViewById(R.id.realchart_all_button);
                btn.setOnClickListener(air_listener);
                btn=(Button)realchart_view.findViewById(R.id.realchart_pm_button);
                btn.setOnClickListener(air_listener);

                container.addView(view);

                break;
            }
        }
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return  view==object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    View.OnClickListener air_listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button btn=(Button)view;
            Util_STATUS.Chart_Select= (String) btn.getText();
        }
    };

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.CO_Frame:
                {
                    Util_STATUS.Chart_Select="CO";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
                case R.id.NO2_Frame:
                {
                    Util_STATUS.Chart_Select="NO2";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
                case R.id.O3_Frame:
                {
                    Util_STATUS.Chart_Select="O3";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
                case R.id.PM_Frame:
                {
                    Util_STATUS.Chart_Select="PM";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
                case R.id.SO2_Frame:
                {
                    Util_STATUS.Chart_Select="SO2";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
                case R.id.TEMP_Frame:
                {
                    Util_STATUS.Chart_Select="TEMP";
                    Fr_View_pager.RT_v_viewpager.setCurrentItem(2);
                    break;
                }
            }
        }
    };
}
