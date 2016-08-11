package P_Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.pyojihye.airpollution.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import P_Data.Air_Data;
import P_Data.User_Data;
import P_Data.Util_STATUS;
import P_Fragment.Fr_R_G_Map;

/**
 * Created by user on 2016-08-03.
 */
public class GMap_Manager {

    public static ArrayList<Circle> mylist=new ArrayList<>();
    public static HashMap user_hash=new HashMap(); //유저 관리 테이블
    public static ArrayList<User_Data> user_array=new ArrayList<>();
    public Context context;
    BitmapDrawable bitmapdraw;
    Bitmap b;
    Bitmap smallAvatar;
    Bitmap smallBus;

    int height = 60;
    int width = 60;
    public GMap_Manager(Context context) {
        this.context=context;
        bitmapdraw=(BitmapDrawable)context.getResources().getDrawable(R.drawable.avatar);
        b=bitmapdraw.getBitmap();
        smallAvatar=Bitmap.createScaledBitmap(b, width, height, false);
        bitmapdraw=(BitmapDrawable)context.getResources().getDrawable(R.drawable.prisonbus);
        b=bitmapdraw.getBitmap();
        smallBus=Bitmap.createScaledBitmap(b, width, height, false);

    }

    //Bitmap 얻는거 지금은안됨


    public void Cal_AQI(User_Data user_data)
    {
        /*
        //user_air가 그사람이 모은 10개 데이터
        //이걸 aqi 구할려면 60번 포문이나와야함
        int sum=0;
        int avg=0;
         //array list가 유저가 모은 10개의 air 값  포문 10번을 5번돌려야함
        for(int i=0;i<10;i++)
        {

            //sum+=arrayList.get(i).co;
        }
        avg=sum/10;
        Hashtable<Integer,String> Hash=new Hashtable<>();
        Hash.put(avg,"CO");


        sum=0;
        avg=0;
        for(int i=0;i<10;i++)
        {
            sum+=arrayList.get(i).so2;
        }
        avg=sum/10;
        Hash=new Hashtable<>();
        Hash.put(avg,"SO2");

        sum=0;
        avg=0;
        for(int i=0;i<10;i++)
        {
            sum+=arrayList.get(i).no2;
        }
        avg=sum/10;
        Hash=new Hashtable<>();
        Hash.put(avg,"NO2");

        sum=0;
        avg=0;
        for(int i=0;i<10;i++)
        {
            sum+=arrayList.get(i).o3;
        }
        avg=sum/10;
        Hash=new Hashtable<>();
        Hash.put(avg,"O3");

        sum=0;
        avg=0;
        for(int i=0;i<10;i++)
        {
            sum+=arrayList.get(i).pm2_5;
        }
        avg=sum/10;
        Hash=new Hashtable<>();
        Hash.put(avg,"PM");

        //평균값 구함 그다음 AQI 계산
        //카운트값으로 I 값 구함


        /*
        for(int i=0;i<data.user_air.size();i++)
            {
                //해쉬안이 평균값 해쉬는 한개만쓸거
                sum+=data.user_air.get(i).co;

                Hashtable<Integer,String> Hash=new Hashtable<>();
                Hash.put(1,"dd");
                data.avg_array.add(Hash);
                //data.avg_array.add(  new Hashtable<Integer,String>().put(1,"dd"));

            }
        */
    }

    public void Set_Circle2(Air_Data ar,String user_id,LatLng latLng)
    {


        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.prisonbus);

        Circle circle;
        String color=Set_Color(ar);

        int count=0;
        if(user_array.size()==0)
        {
            //서클 생성
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(70)
                    .fillColor(Color.parseColor(color))
                    .strokeColor(Color.TRANSPARENT);
            circle = Fr_R_G_Map.gMap.addCircle(circleOptions);
            circle.setClickable(true);


            //마커 생성
            Marker marker=Fr_R_G_Map.gMap.addMarker(new MarkerOptions().position(latLng).draggable(false).position(latLng));
            marker.setTitle(user_id);
            User_Data user_data=new User_Data(user_id,ar,latLng); //id air latlng
            user_data.circle=circle;
            user_data.air=ar;
            user_data.marker=marker;
            user_data.check=true;
            user_data.user_air.add(ar); //평균값 계산을위해 삽입
            user_array.add(user_data);



            //그라운드 오버레이
            //GroundOverlay imageOverlay= Fr_R_G_Map.gMap.addGroundOverlay(newarkMap);

            return;
        }
        for(User_Data data : user_array)
        {
            if(data.id.equals(user_id))
            {
                //data 하나가 사람 하나
                //user_air가 그사람이 모은 10개 데이터
                //이걸 aqi 구할려면 60번 포문이나와야함
                animateCircle(data.circle,data.marker,latLng,data.latLng,color,data.air);

                data.circle.setStrokeColor(Color.TRANSPARENT);
                data.air=ar;
                data.circle.setFillColor(Color.parseColor(color));
                data.latLng=latLng;
                data.check=true;

                if(data.user_air.size()>10) //10개 모이면 aqi값 계산
                {
                    data.user_air.remove(0);
                    data.user_air.add(ar);
                    Cal_AQI(data);

                }
                else if(data.user_air.size()<10)
                {
                    data.user_air.add(ar);
                }

                return;
            }
            count++;
            if(count==user_array.size()) //user id 가 없으면
            {

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(70)
                        .fillColor(Color.parseColor(color))
                        .strokeColor(Color.TRANSPARENT);

                circle = Fr_R_G_Map.gMap.addCircle(circleOptions);
                circle.setClickable(true);

                User_Data user_data=new User_Data(user_id,ar,latLng); //id air latlng
                user_data.circle=circle;
                user_data.check=true;
                user_data.user_air.add(ar);
                //마커 생성
                Marker marker=Fr_R_G_Map.gMap.addMarker(new MarkerOptions().position(latLng).draggable(false).position(latLng));
                marker.setTitle(user_id);
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.avatar));

                user_data.marker=marker;
                user_array.add(user_data);

            }

        }
    }

    // 사람 들어와있는지 확인
    public void check_connection()
    {
        if(user_array.size()!=0) {
            for (User_Data data : user_array) {
                if (data.check == false) {
                    data.circle.remove();
                    data.marker.remove();
                    user_array.remove(data);
                    return;
                }
                data.check = false;
            }
        }
    }
    //서클이나 마커
    public void animateCircle(final Circle circle,final Marker marker,final LatLng toPosition,
                              final LatLng startPosition, final String color,final Air_Data air) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                circle.setCenter(new LatLng(lat,lng));
                marker.setPosition(new LatLng(lat,lng));
                if (t < 1.0) {

                    handler.postDelayed(this,500);

                    String datas="co "+String.valueOf(air.co)+" so2 "+String.valueOf(air.so2)+" o3 "+String.valueOf(air.o3)+
                            " pm2_5 "+String.valueOf(air.pm2_5)+" no2 "+String.valueOf(air.no2);

                } else {

                }
            }
        });

    }

    public static String Set_Color(Air_Data ar)
    {
        String color;
        switch (Util_STATUS.GMap_Realtime_set)
        {
            case "CO":
            {
                color= (ar.co< 51) ? "#7000e400" : (ar.co < 101) ? "#70e9e918" : (ar.co < 151) ? "#70ff7e00" : (ar.co < 200) ? "#70ff0000" :
                        (ar.co < 301) ? "#708f3f97" : (ar.co < 500) ? "#707e0023" : "#707e0023";
                break;
            }
            case "SO2":
            {
                color= (ar.so2< 51) ? "#7000e400" : (ar.so2 < 101) ? "#70e9e918" : (ar.so2 < 151) ? "#70ff7e00" : (ar.so2 < 200) ? "#70ff0000" :
                        (ar.so2 < 301) ? "#708f3f97" : (ar.so2 < 500) ? "#707e0023" : "#707e0023";
                break;
            }
            case "NO2":
            {
                color= (ar.no2< 51) ? "#7000e400" : (ar.no2 < 101) ? "#70e9e918" : (ar.no2 < 151) ? "#70ff7e00" : (ar.no2 < 200) ? "#70ff0000" :
                        (ar.no2 < 301) ? "#708f3f97" : (ar.no2 < 500) ? "#707e0023" : "#707e0023";
                break;
            }
            case "O3":
            {
                color= (ar.o3< 51) ? "#7000e400" : (ar.o3 < 101) ? "#70e9e918" : (ar.o3 < 151) ? "#70ff7e00" : (ar.o3 < 200) ? "#70ff0000" :
                        (ar.o3 < 301) ? "#708f3f97" : (ar.o3 < 500) ? "#707e0023" : "#707e0023";

                break;
            }
            case "PM":
            {
                color= (ar.pm2_5< 51) ? "#7000e400" : (ar.pm2_5 < 101) ? "#70e9e918" : (ar.pm2_5 < 151) ? "#70ff7e00" : (ar.pm2_5 < 200) ? "#70ff0000" :
                        (ar.pm2_5 < 301) ? "#708f3f97" : (ar.pm2_5 < 500) ? "#707e0023" : "#707e0023";
                break;
            }
            default:
            {
                color="#70e400";

            }

        }
        return color;
    }


    /** this is before data**/

    public static void Change_Color()
    {
        for (User_Data data : user_array) {
            data.circle.setStrokeColor(Color.TRANSPARENT);
            data.circle.setFillColor(Color.parseColor(Set_Color(data.air)));

        }
    }


}
