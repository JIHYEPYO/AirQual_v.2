package P_Manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import P_Data.DBHelper;

/**
 * Created by user on 2016-08-02.
 */
public class Gps_Manager {
    public LocationManager locationManager;
    Location location;
    Context mContext;
    public static LatLng latLng=null;
    DBHelper helper;
    SQLiteDatabase db;

    public Gps_Manager(Context mContext) {
        helper=new DBHelper(mContext);
        db=helper.getWritableDatabase();
        this.mContext = mContext;
        //locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null)
        {
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        db.execSQL("INSERT INTO Gps_data (regdate,Lat,Lon) values("+System.currentTimeMillis()/1000+","+latLng.latitude+","+latLng.longitude+");");
        Cursor cursor=db.rawQuery("SELECT * FROM Gps_data",null);
        Gps_data_Thread gdt=new Gps_data_Thread();
        gdt.start();
        //db.delete("Gps_data","regdate"<,);
        Date d = new Date();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        db.delete("Gps_data","regdate"+"<"+d.getTime()/1000,null); //오늘 00시 이전의 데이터는 다날림

        d.getTime();
        //  String sql="INSERT INTO Air_data(regdate,CO) values(1234,'12','13','14','15','16');";
    }
    class Gps_data_Thread extends Thread {
        private TimerTask mTask=new TimerTask() {
            @Override
            public void run() {
                db.execSQL("INSERT INTO Gps_data (regdate,Lat,Lon) values("+System.currentTimeMillis()/1000+","+latLng.latitude+","+latLng.longitude+");");
            }
        };
        private Timer mTimer=new Timer( );
        @Override
        public void run() {
            mTimer.schedule(mTask,0,1000);
        }
    }
    public LatLng get_LatLng()
    {
        return latLng;
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // arrayPoints.add(new LatLng(Double.parseDouble(intent.getStringExtra("LAT")),Double.parseDouble(intent.getStringExtra("LANG"))));
            latLng= new LatLng(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
