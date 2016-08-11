package P_Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by user on 2016-08-08.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="Air data";
    private static final int DATABASE_VERSION=2;

    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Air_data");
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Gps_data");

        sqLiteDatabase.execSQL( "CREATE TABLE  Air_data ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "regdate INTEGER, "
                + "CO INTEGER,"
                + "SO2 INTEGER,"
                + "NO2 INTEGER,"
                + "O3 INTEGER,"
                + "PM INTEGER,"
                + "Lat Double,"
                + "Lon Double"
                + " );");
        sqLiteDatabase.execSQL("CREATE TABLE Gps_data("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"regdate long,"
                +"Lat Double,"
                +"Lon Double"+");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Air_data");
        onCreate(sqLiteDatabase);
    }
    String data;
    Cursor cursor;
    public String Today_max_val()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.rawQuery("SELECT MAX(CO) FROM Air_data",null);

        Date date=new Date(System.currentTimeMillis());
        cursor.moveToNext();
        int data=cursor.getInt(0);
        /*while(cursor.moveToNext())
        {
            int data=cursor.getInt(0);

        }*/


        return String.valueOf(data);
    }
    public static int startOfDay() {

        Date d = new Date();
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        //d.setTime(0);
        return 5;
    }

    public String Today_min_val()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.rawQuery("SELECT MIN(CO) FROM Air_data",null);
        cursor.moveToNext();
        int data=cursor.getInt(0);
        /*while(cursor.moveToNext())
        {

            int data=cursor.getInt(0);

        }*/
        return String.valueOf(data);
    }
    public String Today_avg_val()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.rawQuery("SELECT AVG(CO) FROM Air_data",null);
        cursor.moveToNext();
        int data=cursor.getInt(0);
        /*while(cursor.moveToNext())
        {

            int data=cursor.getInt(0);

        }*/
        return String.valueOf(data);
    }
    public void Get_Gps_data()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        cursor=db.rawQuery("SELECT * FROM Gps_data",null);
        while(cursor.moveToNext())
        {
           /* long unixTime = 932545204L * 1000;

            Date date = new Date(unixTime);

            System.out.println(date.toString());
               */
            int data=cursor.getInt(0);
        }

    }

}
