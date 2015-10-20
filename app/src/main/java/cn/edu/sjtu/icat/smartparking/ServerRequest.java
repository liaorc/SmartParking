package cn.edu.sjtu.icat.smartparking;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class ServerRequest {
    private static final String TAG = "PARK_HTTPS";

    private static final String HTTP_ROOT = "https://139.196.26.139";
    private static final String API_LOGIN = "/user/login";
    private static final String API_QUERY_PARKS = "/user/query_parks";
    private static final String API_BOOK = "/user/book";
    private static final String API_ORDERS = "/user/orders";
    private static final String API_GET_QRCODE = "/user/get_qrcode";
    private static final String API_INFO = "/user/info";
    private static final String API_UPDATE_INFO = "/user/update_info";

    private JSONObject jsonReq = new JSONObject();
    private JSONObject jsonRev = null;



    public void setString(String label, String str) throws JSONException {
        jsonReq.put(label, str);
    }
    public void setInt(String label, int i) throws JSONException {
        jsonReq.put(label, i);
    }
    public void setDouble(String label, double i) throws JSONException {
        jsonReq.put(label, i);
    }

    public void setJSONRev(String str) throws JSONException {
        jsonRev = new JSONObject(str);
    }

    public int getStatusCode() throws Exception {
        return jsonRev.getInt(JSONLabel.STATUS);
    }
    public String getData() throws Exception {
        return jsonRev.getString(JSONLabel.DATA);
    }

    public String queryParks() throws JSONException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_QUERY_PARKS)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.RADIUS, jsonReq.getDouble(JSONLabel.RADIUS)+"")
                .appendQueryParameter(JSONLabel.DEST_LNG, jsonReq.getDouble(JSONLabel.DEST_LNG)+"")
                .appendQueryParameter(JSONLabel.DEST_LAT, jsonReq.getDouble(JSONLabel.DEST_LAT)+"")
                .build().toString();
        return url;
    }

    public String getOrders() throws JSONException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_ORDERS)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.TYPE, jsonReq.getString(JSONLabel.TYPE))
                .appendQueryParameter(JSONLabel.PAGE, jsonReq.getString(JSONLabel.PAGE))
                .build().toString();
        return url;
    }
    public String getInfo() throws JSONException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_INFO)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .build().toString();
        return url;
    }
    public String changeAddress() throws JSONException, UnsupportedEncodingException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_UPDATE_INFO)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.ADDRESS, jsonReq.getString(JSONLabel.ADDRESS))
                .build().toString();
        //return new String(url.getBytes(), "UTF-8");
        return url;
    }
    public String changeDisplayName() throws JSONException, UnsupportedEncodingException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_UPDATE_INFO)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.NAME, jsonReq.getString(JSONLabel.NAME))
                .build().toString();
        //return new String(url.getBytes(), "UTF-8");
        return url;
    }
    public String changePhone() throws JSONException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_UPDATE_INFO)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.PHONE, jsonReq.getString(JSONLabel.PHONE))
                .build().toString();
        return url;
    }
    public String changeFee() throws JSONException {
        String url = Uri.parse(HTTP_ROOT).buildUpon()
                .appendPath(API_UPDATE_INFO)
                .appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION))
                .appendQueryParameter(JSONLabel.FEE, jsonReq.getInt(JSONLabel.FEE)+"")
                .build().toString();
        return url;
    }
    public String updateUserInfo() throws JSONException {
        Uri.Builder builder = Uri.parse(HTTP_ROOT).buildUpon();
        builder.appendPath(API_UPDATE_INFO);
        builder.appendQueryParameter(JSONLabel.SESSION, jsonReq.getString(JSONLabel.SESSION));
        if(jsonReq.has(JSONLabel.ADDRESS)) {
            builder.appendQueryParameter(JSONLabel.ADDRESS, jsonReq.getString(JSONLabel.ADDRESS));
        }
        if(jsonReq.has(JSONLabel.NAME)) {
            builder.appendQueryParameter(JSONLabel.NAME, jsonReq.getString(JSONLabel.NAME));
        }
        if(jsonReq.has(JSONLabel.PHONE)) {
            builder.appendQueryParameter(JSONLabel.PHONE, jsonReq.getString(JSONLabel.PHONE));
        }
        if(jsonReq.has(JSONLabel.FEE)) {
            builder.appendQueryParameter(JSONLabel.FEE, jsonReq.getInt(JSONLabel.FEE)+"");
        }
        if(jsonReq.has(JSONLabel.PRICE)) {
            builder.appendQueryParameter(JSONLabel.PRICE, jsonReq.getString(JSONLabel.PRICE));
        }
        return builder.build().toString();
    }


}
