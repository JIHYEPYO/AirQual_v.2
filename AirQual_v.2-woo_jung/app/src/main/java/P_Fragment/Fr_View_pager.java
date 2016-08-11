package P_Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pyojihye.airpollution.activity.MainActivity;
import com.example.pyojihye.airpollution.R;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;


/**
 * Created by user on 2016-08-02.
 */

public class Fr_View_pager extends Fragment {

    public static VerticalViewPager RT_v_viewpager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fr_viewpager_layout,container,false);

        RT_v_viewpager =new VerticalViewPager(inflater.getContext());

        RT_v_viewpager =(VerticalViewPager)view.findViewById(R.id.pager);

        RT_v_viewpager.setCurrentItem(0);
        RT_v_viewpager.setOffscreenPageLimit(5);
        RT_v_viewpager.setAdapter(MainActivity.pa);
        return view;


    }
    public VerticalViewPager getV_viewpager()
    {
        return RT_v_viewpager;
    }
}
