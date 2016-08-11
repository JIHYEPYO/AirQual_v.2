package P_Manager;

import android.content.Context;
import android.graphics.Color;

import com.example.pyojihye.airpollution.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import P_Fragment.Fr_H_G_Map;

/**
 * Created by user on 2016-08-08.
 */
public class HGMap_Manager {

    Context mcontext;

    public HGMap_Manager(Context mcontext) {
        this.mcontext = mcontext;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

    }
    public  void addHeatMap() {
        List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems(R.raw.police_stations);
        } catch (JSONException e) {

        }
        int[] colors = {
                Color.rgb(102, 225, 0) // green
                // red
        };
        float[] startPoints = {
                0.2f, 1f
        };
        //Gradient gradient = new Gradient(colors, startPoints);
        // Create a
        // zheat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).radius(50).build();
        // Add a tile overlay to the map, using the heat map tile provider.
        Fr_H_G_Map.hgMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = mcontext.getResources().openRawResource(resource);
        @SuppressWarnings("resource")
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }

        return list;
    }


}
