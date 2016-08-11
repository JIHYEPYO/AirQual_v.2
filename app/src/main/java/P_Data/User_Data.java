package P_Data;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by user on 2016-08-03.
 */
public class User_Data {

    //유저 아이디 air data,위치 정도,마커?
    public Air_Data air;
    public LatLng latLng; //user lat lang
    public String id; //user id
    public Circle circle; //user circle
    public Marker marker; //user marker
    public boolean check=false; //user connection check
    public ArrayList<Air_Data> user_air; //user save 10 air data
    public ArrayList<Hashtable<Integer,String>> avg_array; // avg
    public ArrayList<Hashtable<Integer,String>> aqi_array; //aqi
    //user data가 왔는지 안왔는지 체크
    //Marker marker;
    public User_Data(String id,Air_Data air,LatLng latLng) {
        this.id=id;
        this.air=air;
        this.latLng=latLng;
        user_air =new ArrayList<>();
        avg_array=new ArrayList<>();

    }
    public User_Data(String id,Air_Data air,LatLng latLng,Circle circle) {
        this.id=id;
        this.air=air;
        this.latLng=latLng;
        this.circle=circle;
    }
}
