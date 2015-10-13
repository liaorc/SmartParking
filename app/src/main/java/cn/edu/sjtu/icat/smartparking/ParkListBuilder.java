package cn.edu.sjtu.icat.smartparking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Ruochen on 2015/10/13.
 */
public class ParkListBuilder {
    static public ArrayList<ParkInfo> fromString(String data) throws JSONException {
        ArrayList<ParkInfo> list = new ArrayList<ParkInfo>();

        JSONArray array = (JSONArray)new JSONTokener(data).nextValue();
        for(int i=0; i<array.length(); i++) {
            ParkInfo info = new ParkInfo(array.getJSONObject(i));
            list.add(info);
        }
        return list;
    }
}
