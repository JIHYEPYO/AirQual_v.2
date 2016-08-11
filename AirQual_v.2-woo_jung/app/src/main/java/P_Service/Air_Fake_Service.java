package P_Service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

import P_Data.Air_Data;

/**
 *
 * Receive Airdata From Udooboard and send Data Service
 * Created by user on 2016-07-24.
 */
public class Air_Fake_Service extends Service {

    Handler a_handler; //핸들러에서 메인액티비티로 보내기위한 객체
    Message msg;// 핸들러로 보낼 메세지
    public static boolean RECEIVE_DATA_STATUS=false;
    public Air_Fake_Service(Handler a_handler) {
        super();
        this.a_handler=a_handler;
        Air_data_Thread ait=new Air_data_Thread();
        ait.start();
        //버스데이터 나중에 삭제해야함
        Bus_location();
        Date date=new Date();

    }
    class Air_data_Thread extends Thread {
        @Override
        public void run() {
            super.run();
            while(true) {
                Air_Data aa=Get_Air_Data();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(!RECEIVE_DATA_STATUS) { //데이터 리시브 상태가 false인 경우
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                msg=new Message();
                msg.setData(Get_Data());
                a_handler.sendMessage(msg);

                //Bus Data

            }
        }
    }
    private Bundle Get_Data() //Json에서 받아올 데이터 일단은 랜덤으로 할당
    {
        //JSON 파싱
        Bundle bundle=new Bundle();

        bundle.putSerializable("data",Get_Air_Data2());
        //Bluetooth_Manager.getInstance().Set_Data(bundle);
        //Bluetooth_Manager.getInstance().Set_Data();
        /*bundle = new Bundle();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CO",0.3);
            jsonObject.put("NO2",0.0);
            jsonObject.put("O3",0.0);
            jsonObject.put("PM25",94);
            jsonObject.put("SO2",0.0);
            jsonObject.put("TEMP",55);
            jsonObject.put("TIME",1470825992);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putString("data",jsonObject.toString());*/
        //bundle.putString("data", readed);

        //Bluetooth_Manager.getInstance().Set_Data(bundle);
        return bundle;

    }
    private Bundle Get_Bus_Data()
    {
        Bundle bundle=new Bundle();
        bundle.putSerializable("data2",Get_Air_Data3());
        return bundle;
    }
    LatLng latLng=new LatLng(32.88179410794101,-117.23402337703594);
    LatLng [] latLngs=new LatLng[]{new LatLng(32.88179410794101,-117.23402337703594),
            new LatLng(32.88989410794101,-117.23007327703594),new LatLng(32.88650410794101,-117.23202507703594)
            ,new LatLng(32.881443410794101,-117.23772997703594),new LatLng(32.88550010794101,-117.23302907703594)};
    int count=0;
    ArrayList<LatLng> arrayList=new ArrayList<>();
    public void Bus_location()
    {
        arrayList.add(new LatLng(32.88832074270773,-117.24207311868669));
        arrayList.add(new LatLng(32.88863240872476,-117.24192392081022));
        arrayList.add(new LatLng(32.88884750549916,-117.2418635711074));
        arrayList.add(new LatLng(32.88884750549916,-117.2418635711074));
        arrayList.add(new LatLng(32.8888931149575,-117.24106125533581));
        arrayList.add(new LatLng(32.88881653610045,-117.24042557179926));
        arrayList.add(new LatLng(32.88874418028133,-117.23918337374926));
        arrayList.add(new LatLng(32.88872419092011,-117.2389118000865));
        arrayList.add(new LatLng(32.88862030247719,-117.23885849118233));
        arrayList.add(new LatLng(32.88820531057134,-117.23881356418133));
        arrayList.add(new LatLng(32.88696144937567,-117.2387220337987));
        arrayList.add(new LatLng(32.88601488689768,-117.23864726722239));
        arrayList.add(new LatLng(32.88463669412301,-117.23858289420606));
        arrayList.add(new LatLng(32.88342488035679,-117.23859664052726));
        arrayList.add(new LatLng(32.88338884103194,-117.23793882876635));
        arrayList.add(new LatLng(32.88308278773728,-117.23691657185555));
        arrayList.add(new LatLng(32.882818122582435,-117.23602809011936));
        arrayList.add(new LatLng(32.88280629709962,-117.23579373210669));

        arrayList.add(new LatLng(32.88284796784157,-117.23547689616682));
        arrayList.add(new LatLng(32.882963125398156,-117.23488949239254));
        arrayList.add(new LatLng(32.882971572156784,-117.23469804972409));
        arrayList.add(new LatLng(32.88292539653301,-117.23443754017353));
        arrayList.add(new LatLng(32.88284430757449,-117.2342350333929));
        arrayList.add(new LatLng(32.882591467220486,-117.23391652107239));
        arrayList.add(new LatLng(32.88233130556876,-117.23360236734152));
        arrayList.add(new LatLng(32.88212407669367,-117.23335895687342));

        arrayList.add(new LatLng(32.88188812800957,-117.233142033219322));
        arrayList.add(new LatLng(32.881610225830165,-117.23290532827377));
        arrayList.add(new LatLng(32.8813359831089,-117.23256334662436));
        arrayList.add(new LatLng(32.88113184911559,-117.23243493586777));
        arrayList.add(new LatLng(32.88087421726668,-117.23247416317464));
        arrayList.add(new LatLng(32.88069401530978,-117.23264615982771));
        arrayList.add(new LatLng(32.880372748099624,-117.23299216479063));
        arrayList.add(new LatLng(32.88021788618885,-117.23268337547779));

        arrayList.add(new LatLng(32.880164669942495,-117.23245672881605));
        arrayList.add(new LatLng(32.88016269896981,-117.23218213766813));
        arrayList.add(new LatLng(32.880166640915164,-117.2321519628167));
        arrayList.add(new LatLng(32.88026181068558,-117.23169464617966));
        arrayList.add(new LatLng(32.88034430984175,-117.23140362650156));
        arrayList.add(new LatLng(32.88050367659491,-117.23100565373899));
        arrayList.add(new LatLng(32.88075427037989,-117.23041858524084));
        arrayList.add(new LatLng(32.881242222312416,-117.22930010408163));

        arrayList.add(new LatLng(32.88128023345809,-117.22923807799818));
        arrayList.add(new LatLng(32.88082353550334,-117.22898460924625));
        arrayList.add(new LatLng(32.88067374256668,-117.22891587764025));
        arrayList.add(new LatLng(32.88027560747814,-117.22892023622988));
        arrayList.add(new LatLng(32.87982425415042,-117.2289775684476));
        arrayList.add(new LatLng(32.87942639681589,-117.22904127091171));

        arrayList.add(new LatLng(32.87874752925758,-117.22918946295977));
        arrayList.add(new LatLng(32.87802979925396,-117.22937352955343));
        arrayList.add(new LatLng(32.87727151640095,-117.2295817360282));
        arrayList.add(new LatLng(32.876749471632124,-117.2298925369978));
        arrayList.add(new LatLng(32.876516887589794,-117.23023552447559));
        arrayList.add(new LatLng(32.87637159264832,-117.2306465730071));
        arrayList.add(new LatLng(32.876358639986954,-117.2311756387353));
        arrayList.add(new LatLng(32.876608682333135,-117.23233301192522));
        arrayList.add(new LatLng(32.87666077439982,-117.23261363804339));
        arrayList.add(new LatLng(32.87684548980576,-117.23316952586174));

        arrayList.add(new LatLng(32.87694038156435,-117.23337270319462));
        arrayList.add(new LatLng(32.87706934408826,-117.23409723490475));
        arrayList.add(new LatLng(32.87711439645413,-117.23476108163594));
        arrayList.add(new LatLng(32.87710623071451,-117.23577696830033));
        arrayList.add(new LatLng(32.87711327014527,-117.2369122132659));
        arrayList.add(new LatLng(32.87709158869677,-117.23752140998842));
        arrayList.add(new LatLng(32.87705667310624,-117.23809003829957));

        arrayList.add(new LatLng(32.87694657627503,-117.23844174295664));
        arrayList.add(new LatLng(32.87661741127617,-117.23888799548149));
        arrayList.add(new LatLng(32.876336958353875,-117.23908245563506));
        arrayList.add(new LatLng(32.875780555150776,-117.23920214921235));
        arrayList.add(new LatLng(32.87469448883734,-117.23862010985613));
        arrayList.add(new LatLng(32.87409527414125,-117.23825432360171));
        arrayList.add(new LatLng(32.87379284939679,-117.23863419145346));

        arrayList.add(new LatLng(32.87333357856141,-117.23892319947483));
        arrayList.add(new LatLng(32.872993136535484,-117.23947942256927));
        arrayList.add(new LatLng(32.87270000054266,-117.23970875144005));
        arrayList.add(new LatLng(32.872565963012654,-117.23989583551884));
        arrayList.add(new LatLng(32.87251246058065,-117.24016271531582));
        arrayList.add(new LatLng(32.87252935608897,-117.24064987152815));
        arrayList.add(new LatLng(32.87257272121228,-117.24126007407904));

        arrayList.add(new LatLng(32.87260031718882,-117.24171236157417));
        arrayList.add(new LatLng(32.87261467835687,-117.24210530519484));
        arrayList.add(new LatLng(32.872616367905906,-117.2426176071167));
        arrayList.add(new LatLng(32.87263044747991,-117.24292203783989));
        arrayList.add(new LatLng(32.8727326651201,-117.24316846579313));
        arrayList.add(new LatLng(32.872905561907174,-117.24333308637144));
        arrayList.add(new LatLng(32.873019887610496,-117.24338304251432));

        arrayList.add(new LatLng(32.87340510219554,-117.24340416491032));
        arrayList.add(new LatLng(32.87373258959445,-117.24340684711933));
        arrayList.add(new LatLng(32.87414877561806,-117.24340919405222));
        arrayList.add(new LatLng(32.874520187611786,-117.24340382963419));
        arrayList.add(new LatLng(32.87526131743252,-117.24323686212301));
        arrayList.add(new LatLng(32.875529947304216,-117.24307727068661));
        arrayList.add(new LatLng(32.87577069979939,-117.24304910749196));

        arrayList.add(new LatLng(32.87601680167424,-117.24313963204622));
        arrayList.add(new LatLng(32.876313024077454,-117.24340684711933));
        arrayList.add(new LatLng(32.87657630076276,-117.24349502474071));
        arrayList.add(new LatLng(32.877077509831274,-117.24356710910799));
        arrayList.add(new LatLng(32.87765558620961,-117.24361538887024));
        arrayList.add(new LatLng(32.878285468356324,-117.24362444132566));
        arrayList.add(new LatLng(32.87882383524416,-117.24363181740046));

        arrayList.add(new LatLng(32.87927998036211,-117.24362041801213));
        arrayList.add(new LatLng(32.879543248235265,-117.24361404776572));
        arrayList.add(new LatLng(32.87964067122858,-117.24350240081547));
        arrayList.add(new LatLng(32.879650526149675,-117.24307727068661));
        arrayList.add(new LatLng(32.879657002240116,-117.24259044975044));
        arrayList.add(new LatLng(32.879667701866715,-117.24195040762426));
        arrayList.add(new LatLng(32.879673614817705,-117.24134154617786));

        arrayList.add(new LatLng(32.87975526981489,-117.24100425839426));
        arrayList.add(new LatLng(32.87975414353956,-117.24100425839426));
        arrayList.add(new LatLng(32.879756677658996,-117.24076520651579));
        arrayList.add(new LatLng(32.879759493347166,-117.24075213074684));
        arrayList.add(new LatLng(32.88023562493053,-117.24073302000761));
        arrayList.add(new LatLng(32.880826351157616,-117.24072799086572));
        arrayList.add(new LatLng(32.88098036731045,-117.2407363727689));

        arrayList.add(new LatLng(32.881021475781296,-117.24097341299057));
        arrayList.add(new LatLng(32.881021475781296,-117.24129360169172));
        arrayList.add(new LatLng(32.8810228836053,-117.24167950451373));
        arrayList.add(new LatLng(32.88103358306702,-117.24231451749802));
        arrayList.add(new LatLng(32.881031612113645,-117.24284593015908));
        arrayList.add(new LatLng(32.881042593139064,-117.24379207938911));
        arrayList.add(new LatLng(32.88206523036227,-117.24377028644084));

        arrayList.add(new LatLng(32.88352342531082,-117.24375352263452));
        arrayList.add(new LatLng(32.88381793329183,-117.24376156926155));
        arrayList.add(new LatLng(32.88381568084446,-117.2423741966486));
        arrayList.add(new LatLng(32.884573344603794,-117.24238123744726));
        arrayList.add(new LatLng(32.885980819469474,-117.24239230155945));
        arrayList.add(new LatLng(32.887105882320924,-117.24237821996212));
        arrayList.add(new LatLng(32.88796768882208,-117.24224746227264));


    }
    int bus_count=0;
    private Air_Data Get_Air_Data3() //버스 데이터
    {
        if(bus_count==arrayList.size()-1)
        {
            bus_count=0;
        }
        bus_count++;
        return new Air_Data((int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),arrayList.get(bus_count));
    }
    private Air_Data Get_Air_Data2()
    {

        count ++;
        if(count==4) count=0;
        switch((int) (Math.random()*4))
        {
            case 0:
            {
                latLngs[count]=new LatLng(latLngs[count].latitude+0.0001,latLngs[count].longitude+0.0001);
                break;
            }
            case 1:
            {
                latLngs[count]=new LatLng(latLngs[count].latitude+0.0001,latLngs[count].longitude-0.0001);
                break;
            }
            case 2:
            {
                latLngs[count]=new LatLng(latLngs[count].latitude+0.0001,latLngs[count].longitude-0.0001);
                break;
            }
            case 3:
            {
                latLngs[count]=new LatLng(latLngs[count].latitude-0.0001,latLngs[count].longitude+0.0001);
                break;
            }
            case 4:
            {
                latLngs[count]=new LatLng(latLngs[count].latitude-0.0001,latLngs[count].longitude-0.0001);
                break;
            }

        }
        latLngs[count]=new LatLng(latLngs[count].latitude+0.00001,latLngs[count].longitude+0.00001);
        return new Air_Data((int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),latLngs[count]);
    }
    private Air_Data Get_Air_Data() //지금은 랜덤변수로 할당 나중에 JSON파싱과정을 이곳에다가가
    {
       /* return new Air_Data((int)(Math.random()*500),(int)(Math.random()*500),
            (int)(Math.random()*500),(int)(Math.random()*500),(int)(Math.random()*500),
            (int)(Math.random()*500),(int)(Math.random()*500),new LatLng(32.88179410794101+Math.random(),-117.23402337703594+Math.random()));
        */

        latLng=new LatLng(latLng.latitude+0.0001,latLng.longitude+0.0001);
        return new Air_Data((int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),(int)(Math.random()*500),
                (int)(Math.random()*500),(int)(Math.random()*500),latLng);
        //32,-117
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
