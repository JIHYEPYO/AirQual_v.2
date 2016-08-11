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
 * Created by user on 2016-08-07.
 */
public class Fr_Historychart_pager extends Fragment {

    private static Fr_Historychart_pager instance;

    public static Fr_Historychart_pager getInstance() {
        return instance;
    }
    public Fr_Historychart_pager() {

        instance = this;


    }
    @Nullable
    public static VerticalViewPager v_viewpager;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fr_history_chart,container,false);

        v_viewpager=new VerticalViewPager(inflater.getContext());

        v_viewpager=(VerticalViewPager)view.findViewById(R.id.pager2);

        v_viewpager.setCurrentItem(0);
        v_viewpager.setOffscreenPageLimit(5);
        v_viewpager.setAdapter(MainActivity.ha);
        return view;

    }
}
