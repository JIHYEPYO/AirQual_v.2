package P_Data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by user on 2016-07-25.
 */
public class Air_Data implements Serializable {

    public  int time;
    public  int co2;
    public int co;
    public  int so2;
    public  int no2;
    public  int pm2_5;
    public  int o3;
    public  int temp;
    public LatLng latLng;

    public Air_Data()
    {

    }

    public Air_Data(int time, int co2, int co, int so2, int no2, int pm2_5, int o3,LatLng latLng) {
        this.time = time;
        this.co2 = co2;
        this.co = co;
        this.so2 = so2;
        this.no2 = no2;
        this.pm2_5 = pm2_5;
        this.o3 = o3;
        this.latLng=latLng;
    }
}
