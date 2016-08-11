package P_Data;

/**
 * Created by user on 2016-08-08.
 */
public class AQI_Data {

        public String name;
        public int min;
        public int max;
        int count;
    public AQI_Data(String name, int min, int max,int count) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.count=count;
    }
}
