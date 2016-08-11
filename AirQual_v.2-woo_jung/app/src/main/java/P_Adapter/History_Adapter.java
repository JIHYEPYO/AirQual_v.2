package P_Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.pyojihye.airpollution.R;

import P_Fragment.Fr_Historychart_pager;
import P_Manager.Realchart_Manager;
import P_Manager.Realtime_Manager;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by user on 2016-08-07.
 */
public class History_Adapter extends PagerAdapter {

    LayoutInflater inflater;
    public static Context context;
    Handler pHandler;
    View realtime_view;
    View realchart_view;
    VerticalViewPager verticalViewPager;
    public static Realtime_Manager rm;
    public static Realchart_Manager rcm;

    public History_Adapter(LayoutInflater inflater, Context context) {
        this.inflater=inflater;
        this.context=context;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=null;

        switch(position) {
            case 0: {
                view=inflater.inflate(R.layout.history_calander,null);
                CalendarView calendarView=(CalendarView)view.findViewById(R.id.calendarView);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                        Toast.makeText(context,""+i+""+i1+""+i2,Toast.LENGTH_SHORT).show();
                        if(Fr_Historychart_pager.getInstance()!=null)
                        {
                            Fr_Historychart_pager.v_viewpager.setCurrentItem(1);
                        }
                    }
                });


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
}
