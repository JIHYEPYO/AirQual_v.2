package P_Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pyojihye.airpollution.R;
import com.example.pyojihye.airpollution.activity.MainActivity;

/**
 * Created by PYOJIHYE on 2016-08-03.
 */
public class Fr_DeviceManagement extends Fragment {




    public static ViewPager v_viewpager;
    Handler device_Handler; //device Handler
    static View view;
    private static Fr_DeviceManagement instance;

    public static Fr_DeviceManagement getInstance() {
        return instance;
    }
    public Fr_DeviceManagement() {

        instance = this;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fr_device_mange_vpager,container,false);
        v_viewpager=new ViewPager(inflater.getContext());
        v_viewpager=(ViewPager)view.findViewById(R.id.device_manage_vpager);
        //v_viewpager=(VerticalViewPager)view.findViewById(R.id.pager2);
        v_viewpager.setCurrentItem(0);
        v_viewpager.setOffscreenPageLimit(5);
        v_viewpager.setAdapter(MainActivity.ma);

        return view;
    }




}
