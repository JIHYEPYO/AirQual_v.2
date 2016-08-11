package P_Manager;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pyojihye.airpollution.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Hashtable;

import P_Data.Air_Data;
import P_Data.DBHelper;
import P_Data.Util_STATUS;

/**
 * Created by user on 2016-07-25.
 */
public class Realchart_Manager {
    View realtime_view;
    private LineChart mChart;
    private LineChart hChart;
    private DBHelper helper;
    public Realchart_Manager(View view, Context context) {
        realtime_view=view;
        /* Linechart init*/

        mChart = (LineChart)view.findViewById(R.id.air_chart);
        hChart=(LineChart)view.findViewById(R.id.air_chart2);

        hChart.setTouchEnabled(true);

        hChart.setDragEnabled(true);
        hChart.setScaleEnabled(true);
        hChart.setDrawGridBackground(false);
        hChart.setPinchZoom(true);
        hChart.setVisibleXRangeMaximum(1000);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        hChart.setData(data);


        //mChart.setDescription.("");


        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setVisibleXRangeMaximum(1000);
         data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        helper=new DBHelper(context);


    }

    Hashtable<String,Integer> max_value=new Hashtable();
    Hashtable<String,Integer> min_value=new Hashtable();


    public void Set_HRchart(Integer hr_get)
    {
        LineData data=hChart.getData();
        LineDataSet set=null;
        if(data!=null)
        {
            set=data.getDataSetByIndex(0);
            if(set==null) //이곳에서 6개 만들어줌
            {
                set=create_HRSet();
                data.addDataSet(set);

            }
            set=(LineDataSet)data.getDataSetByIndex(0);
            if (data.getXValCount() > 20) {
                set.removeEntry(0);
                data.addEntry(new Entry((float)hr_get,20), 0);
                for (int z=0; z < set.getEntryCount(); z++) {
                    Entry e = set.getEntryForXIndex(z);
                    if (e==null) continue;
                    e.setXIndex(e.getXIndex() - 1);
                }

            }
            else{
                data.addEntry(new Entry((float)hr_get ,set.getEntryCount()), 0);
            }

            if (data.getXValCount() > 20)
            {
                data.getXVals().remove(0);
                data.getXVals().add(String.valueOf(hr_count));
            }
            else
            {
                data.getXVals().add(String.valueOf(hr_count));
            }
            hr_count++;
            hChart.notifyDataSetChanged();
            hChart.invalidate();

        }

    }
    public void Set_Realchart2(Air_Data air)
    {
        LineData data=mChart.getData();
        LineDataSet set=null;
        if(data!=null)
        {
            set=data.getDataSetByIndex(0);
            if(set==null) //이곳에서 6개 만들어줌
            {
                for(int i=0;i<5;i++)
                {
                    set=createSet(i);
                    data.addDataSet(set);
                }

            }

            for(int i=0;i<5;i++)
            {
                set=(LineDataSet)data.getDataSetByIndex(i);
                if (data.getXValCount() > 20) {
                    set.removeEntry(0);

                    switch (i)
                    {

                        case 0:
                        {
                            data.addEntry(new Entry((float)air.co ,20), i);
                            break;
                        }

                        case 1:
                        {
                            data.addEntry(new Entry((float)air.so2 ,20), i);
                            break;
                        }

                        case 2:
                        {
                            data.addEntry(new Entry((float)air.no2 ,20), i);
                            break;
                        }

                        case 3:
                        {
                            data.addEntry(new Entry((float)air.o3 ,20), i);
                            break;
                        }

                        case 4:
                        {
                            data.addEntry(new Entry((float)air.pm2_5,20), i);
                            break;
                        }



                    }
                    for (int z=0; z < set.getEntryCount(); z++) {
                        Entry e = set.getEntryForXIndex(z);
                        if (e==null) continue;
                        e.setXIndex(e.getXIndex() - 1);
                    }

                }
                else{
                    switch (i)
                    {
                        case 0:
                        {
                            data.addEntry(new Entry((float)air.co ,set.getEntryCount()), i);
                            break;
                        }

                        case 1:
                        {
                            data.addEntry(new Entry((float)air.so2 ,set.getEntryCount()), i);
                            break;
                        }

                        case 2:
                        {
                            data.addEntry(new Entry((float)air.no2 ,set.getEntryCount()), i);
                            break;
                        }

                        case 3:
                        {
                            data.addEntry(new Entry((float)air.o3 ,set.getEntryCount()), i);
                            break;
                        }
                        //co so2 no2 o3  pm 2_5
                        case 4:
                        {
                            data.addEntry(new Entry((float)air.pm2_5,set.getEntryCount()), i);
                            break;
                        }



                    }
                }

            }
            if (data.getXValCount() > 20)
            {
                data.getXVals().remove(0);
                data.getXVals().add(String.valueOf(count));
            }
            else
            {
                data.getXVals().add(String.valueOf(count));
            }
            count++;
            mChart.notifyDataSetChanged();
            mChart.invalidate();

        }
    }

    public void Set_Data(final Air_Data air) //Top Value , MinValue 수정
    {
        final TextView max_text=(TextView)realtime_view.findViewById(R.id.chart_max_val);
        if(max_value.size()==0)
        {
            max_value.put("CO",0);
            max_value.put("SO2",0);
            max_value.put("NO2",0);
            max_value.put("O3",0);
            max_value.put("PM",0);
        }
        switch (Util_STATUS.Chart_Select)
        {

            case "CO":
            {
                if(max_value.get("CO")<air.co)
                {
                    max_value.put("CO",air.co);

                    max_text.post(new Runnable() {
                        @Override
                        public void run() {
                            max_text.setText(String.valueOf(air.co));
                        }
                    });
                    // realtime_view.findViewById()
                }
                break;
            }
            case "SO2":
            {
                if(max_value.get("SO2")<air.so2)
                {
                    max_value.put("SO2",air.so2);
                    max_text.setText(String.valueOf(air.so2));
                }
                break;
            }
            case "NO2":
            {
                if(max_value.get("NO2")<air.no2)
                {
                    max_value.put("NO2",air.no2);
                    max_text.setText(String.valueOf(air.no2));
                }
                break;
            }
            case "O3":
            {
                if(max_value.get("O3")<air.o3)
                {
                    max_value.put("O3",air.o3);
                    max_text.setText(String.valueOf(air.o3));
                }
                break;
            }
            case "PM":
            {
                if(max_value.get("PM")<air.pm2_5)
                {
                    max_value.put("PM",air.pm2_5);
                    max_text.setText(String.valueOf(air.pm2_5));
                }
                break;
            }
        }
        final TextView min_text=(TextView)realtime_view.findViewById(R.id.chart_min_val);
        if(min_value.size()==0)
        {
            min_value.put("CO",500);
            min_value.put("SO2",500);
            min_value.put("NO2",500);
            min_value.put("O3",500);
            min_value.put("PM",500);

        }

        switch (Util_STATUS.Chart_Select)
        {

            case "CO":
            {
                if(min_value.get("CO")>air.co)
                {
                    min_value.put("CO",air.co);

                    min_text.post(new Runnable() {
                        @Override
                        public void run() {
                            min_text.setText(String.valueOf(air.co));
                        }
                    });


                    // realtime_view.findViewById()
                }

                mChart.getData().getDataSets().get(1).setVisible(false);
                mChart.getData().getDataSets().get(1).setValueTextSize(0);
                mChart.getData().getDataSets().get(2).setVisible(false);
                mChart.getData().getDataSets().get(2).setValueTextSize(0);
                mChart.getData().getDataSets().get(3).setVisible(false);
                mChart.getData().getDataSets().get(3).setValueTextSize(0);
                mChart.getData().getDataSets().get(4).setVisible(false);
                mChart.getData().getDataSets().get(4).setValueTextSize(0);

                mChart.getData().getDataSets().get(0).setVisible(true);
                mChart.getData().getDataSets().get(0).setValueTextSize(10);

                break;
            }
            case "SO2":
            {
                if(min_value.get("SO2")<air.so2)
                {
                    min_value.put("SO2",air.so2);
                    min_text.setText(String.valueOf(air.so2));
                }

                mChart.getData().getDataSets().get(0).setVisible(false);
                mChart.getData().getDataSets().get(0).setValueTextSize(0);
                mChart.getData().getDataSets().get(2).setVisible(false);
                mChart.getData().getDataSets().get(2).setValueTextSize(0);
                mChart.getData().getDataSets().get(3).setVisible(false);
                mChart.getData().getDataSets().get(3).setValueTextSize(0);
                mChart.getData().getDataSets().get(4).setVisible(false);
                mChart.getData().getDataSets().get(4).setValueTextSize(0);

                mChart.getData().getDataSets().get(1).setVisible(true);
                mChart.getData().getDataSets().get(1).setValueTextSize(10);
                break;
            }
            case "NO2":
            {
                if(min_value.get("NO2")<air.no2)
                {
                    min_value.put("NO2",air.no2);
                    min_text.setText(String.valueOf(air.no2));
                }
                mChart.getData().getDataSets().get(0).setVisible(false);
                mChart.getData().getDataSets().get(0).setValueTextSize(0);
                mChart.getData().getDataSets().get(1).setVisible(false);
                mChart.getData().getDataSets().get(1).setValueTextSize(0);
                mChart.getData().getDataSets().get(3).setVisible(false);
                mChart.getData().getDataSets().get(3).setValueTextSize(0);
                mChart.getData().getDataSets().get(4).setVisible(false);
                mChart.getData().getDataSets().get(4).setValueTextSize(0);

                mChart.getData().getDataSets().get(2).setVisible(true);
                mChart.getData().getDataSets().get(2).setValueTextSize(10);


                break;
            }
            case "O3":
            {
                if( min_value.get("O3")<air.o3)
                {
                    min_value.put("O3",air.o3);
                    min_text.setText(String.valueOf(air.o3));
                }
                mChart.getData().getDataSets().get(0).setVisible(false);
                mChart.getData().getDataSets().get(0).setValueTextSize(0);
                mChart.getData().getDataSets().get(1).setVisible(false);
                mChart.getData().getDataSets().get(1).setValueTextSize(0);
                mChart.getData().getDataSets().get(2).setVisible(false);
                mChart.getData().getDataSets().get(2).setValueTextSize(0);
                mChart.getData().getDataSets().get(4).setVisible(false);
                mChart.getData().getDataSets().get(4).setValueTextSize(0);

                mChart.getData().getDataSets().get(3).setVisible(true);
                mChart.getData().getDataSets().get(3).setValueTextSize(10);

                break;
            }
            case "PM":
            {
                if( min_value.get("PM")<air.pm2_5)
                {
                    min_value.put("PM",air.pm2_5);
                    min_text.setText(String.valueOf(air.pm2_5));
                }
                mChart.getData().getDataSets().get(0).setVisible(false);
                mChart.getData().getDataSets().get(0).setValueTextSize(0);
                mChart.getData().getDataSets().get(1).setVisible(false);
                mChart.getData().getDataSets().get(1).setValueTextSize(0);
                mChart.getData().getDataSets().get(2).setVisible(false);
                mChart.getData().getDataSets().get(2).setValueTextSize(0);
                mChart.getData().getDataSets().get(3).setVisible(false);
                mChart.getData().getDataSets().get(3).setValueTextSize(0);

                mChart.getData().getDataSets().get(4).setVisible(true);
                mChart.getData().getDataSets().get(4).setValueTextSize(10);
                break;
            }
            case "ALL":
            {
                mChart.getData().getDataSets().get(0).setVisible(true);
                mChart.getData().getDataSets().get(0).setValueTextSize(10);
                mChart.getData().getDataSets().get(1).setVisible(true);
                mChart.getData().getDataSets().get(1).setValueTextSize(10);
                mChart.getData().getDataSets().get(2).setVisible(true);
                mChart.getData().getDataSets().get(2).setValueTextSize(10);
                mChart.getData().getDataSets().get(3).setVisible(true);
                mChart.getData().getDataSets().get(3).setValueTextSize(10);

                mChart.getData().getDataSets().get(4).setVisible(true);
                mChart.getData().getDataSets().get(4).setValueTextSize(10);
            }
        }
        final TextView today_max_text=(TextView)realtime_view.findViewById(R.id.chart_max_val);
        final TextView today_min_text=(TextView)realtime_view.findViewById(R.id.chart_min_val);
        final TextView today_avg_text=(TextView)realtime_view.findViewById(R.id.chart_avg);
        today_max_text.setText(helper.Today_max_val());
        today_min_text.setText(helper.Today_min_val());
        today_avg_text.setText(helper.Today_avg_val());
    }
    public void Set_Data_Color(Air_Data air) //Avg 30 sec 수정
    {
        //LinearLayout linear=(LinearLayout)realtime_view.findViewById(R.id.chart_color1);
        //LinearLayout linear2=(LinearLayout)realtime_view.findViewById(R.id.chart_color2);
        //LinearLayout linear3=(LinearLayout)realtime_view.findViewById(R.id.chart_color3);
        LinearLayout linear4=(LinearLayout)realtime_view.findViewById(R.id.chart_color4);
        LinearLayout linear5=(LinearLayout)realtime_view.findViewById(R.id.chart_color5);
        final TextView max_text=(TextView)realtime_view.findViewById(R.id.chart_max_val);
        final TextView min_text=(TextView)realtime_view.findViewById(R.id.chart_min_val);
        final TextView avg_text=(TextView)realtime_view.findViewById(R.id.chart_avg);


        switch (Util_STATUS.Chart_Select)
        {
            case "CO":
            {
                //linear.setBackgroundColor(Color.parseColor("#5EC75E"));
                //linear2.setBackgroundColor(Color.parseColor("#5EC75E"));
                //linear3.setBackgroundColor(Color.parseColor("#5EC75E"));
                linear4.setBackgroundColor(Color.parseColor("#5EC75E"));
                linear5.setBackgroundColor(Color.parseColor("#5EC75E"));
                max_text.setTextColor(Color.parseColor("#5EC75E"));
                min_text.setTextColor(Color.parseColor("#5EC75E"));
                avg_text.setTextColor(Color.parseColor("#5EC75E"));
                break;
            }
            case "SO2":
            {
                //linear.setBackgroundColor(Color.parseColor("#FFAF0A"));
                //linear2.setBackgroundColor(Color.parseColor("#FFAF0A"));
                //linear3.setBackgroundColor(Color.parseColor("#FFAF0A"));
                linear4.setBackgroundColor(Color.parseColor("#FFAF0A"));
                linear5.setBackgroundColor(Color.parseColor("#FFAF0A"));
                max_text.setTextColor(Color.parseColor("#FFAF0A"));
                min_text.setTextColor(Color.parseColor("#FFAF0A"));
                avg_text.setTextColor(Color.parseColor("#FFAF0A"));
                break;
            }
            case "NO2":
            {
                //linear.setBackgroundColor(Color.parseColor("#CD3C3C"));
                //linear2.setBackgroundColor(Color.parseColor("#CD3C3C"));
                //linear3.setBackgroundColor(Color.parseColor("#CD3C3C"));
                linear4.setBackgroundColor(Color.parseColor("#CD3C3C"));
                linear5.setBackgroundColor(Color.parseColor("#CD3C3C"));
                max_text.setTextColor(Color.parseColor("#CD3C3C"));
                min_text.setTextColor(Color.parseColor("#CD3C3C"));
                avg_text.setTextColor(Color.parseColor("#CD3C3C"));
                break;
            }
            case "O3":
            {
                //linear.setBackgroundColor(Color.parseColor("#FF02E402"));
                //linear2.setBackgroundColor(Color.parseColor("#FF02E402"));
                //linear3.setBackgroundColor(Color.parseColor("#FF02E402"));
                linear4.setBackgroundColor(Color.parseColor("#FF02E402"));
                linear5.setBackgroundColor(Color.parseColor("#FF02E402"));
                max_text.setTextColor(Color.parseColor("#FF02E402"));
                min_text.setTextColor(Color.parseColor("#FF02E402"));
                avg_text.setTextColor(Color.parseColor("#FF02E402"));
                break;
            }
            case "PM":
            {
                //linear.setBackgroundColor(Color.parseColor( "#FF904098"));
                //linear2.setBackgroundColor(Color.parseColor( "#FF904098"));
                //linear3.setBackgroundColor(Color.parseColor( "#FF904098"));
                linear4.setBackgroundColor(Color.parseColor( "#FF904098"));
                linear5.setBackgroundColor(Color.parseColor( "#FF904098"));
                max_text.setTextColor(Color.parseColor( "#FF904098"));
                min_text.setTextColor(Color.parseColor( "#FF904098"));
                avg_text.setTextColor(Color.parseColor( "#FF904098"));
                break;
            }
        }
    }
    int count=1;
    int hr_count=1;
    /*HR DATA*/

    private LineDataSet create_HRSet()
    {
        LineDataSet set=new LineDataSet(null,null);
        set= new LineDataSet(null, "HR");

        set.setColor(Color.parseColor("#FFBBC6"));
        set.setCircleColor(Color.parseColor("#FFBBC6"));
        set.setLineWidth(3);
        set.setDrawCircles(true);
        set.setDrawCubic(true);
        set.setDrawCubic(true);

        return set;
    }
    private LineDataSet createSet(int count) {

        LineDataSet set=new LineDataSet(null,null);

        //LineDataSet set=new L
        switch (count)
        {
            case 0:
            {
                set= new LineDataSet(null, "CO2");
                set.setColor(Color.parseColor("#5EC75E"));
                set.setCircleColor(Color.parseColor("#5EC75E"));
                set.setLineWidth(3);
                set.setDrawCircles(true);
                set.setDrawCubic(true);

                //set.setDrawFilled(true);
                break;
            }
            case 1:
            {
                set= new LineDataSet(null, "SO2");
                set.setColor(Color.parseColor("#FFAF0A"));
                set.setCircleColor(Color.parseColor("#FFAF0A"));
                set.setLineWidth(3);
                set.setDrawCircles(true);

                break;
            }
            case 2:
            {
                set= new LineDataSet(null, "NO2");

                set.setColor(Color.parseColor("#CD3C3C"));
                set.setCircleColor(Color.parseColor("#CD3C3C"));
                set.setLineWidth(3);
                break;
            }
            case 3:
            {
                set= new LineDataSet(null, "O3");
                set.setColor(Color.parseColor("#FF02E402"));
                set.setCircleColor(Color.parseColor("#FF02E402"));
                set.setLineWidth(3);
                break;
            }
            case 4:
            {
                set= new LineDataSet(null, "PM");
                set.setColor(Color.parseColor("#FF904098"));
                set.setCircleColor(Color.parseColor("#FF904098"));
                set.setLineWidth(3);
                break;
            }
        }
        set.setDrawCubic(true);
        return set;
    }

}
