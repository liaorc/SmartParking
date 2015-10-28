package cn.edu.sjtu.icat.smartparking;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class CurrentUser {
    private static final String TAG = "PARK_CUR_USER";
    private static final String JSON_USERNAME = "username";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_SESSION = "session";
    private static final String FILENAME = "user.json";

    private static CurrentUser sCurrentUser;
    private Context mAppContext;

    private String mUsername;
    private String mPassword;
    private String mSession="3c20fb2717622b1f725f174efb6b9e27fbdc02d1";
    private double mLongitude;
    private double mLatitude;
    private String mDisplayName;
    private int mFee;
    private String mAddress;
    private String mPrice;
    private String mPhone;
    private int mDoneOrders;


    private CurrentUserInfoSaver mUserInfoSaver;

    private CurrentUser(Context appContext) {
        mAppContext = appContext;
        mUserInfoSaver = new CurrentUserInfoSaver(mAppContext, FILENAME);

//        try{
//            restoreUser(mUserInfoSaver.loadUserInfo());
//            Log.d(TAG, "loaded");
//            Log.d(TAG, "user: "+ mUsername + "\t Session: " + mSession);
//        } catch (Exception e) {
//            Log.d(TAG, "error load ", e);
//            //ignore?
//        }
    }

    public static CurrentUser get(Context c) {
        if(sCurrentUser == null) {
            sCurrentUser = new CurrentUser(c.getApplicationContext());
        }
        return sCurrentUser;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setSession(String session) {
        mSession = session;
    }

    public String getSession() {
        return mSession;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public int getFee() {
        return mFee;
    }

    public void setFee(int fee) {
        mFee = fee;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public int getDoneOrders() {
        return mDoneOrders;
    }

    public void setDoneOrders(int doneOrders) {
        mDoneOrders = doneOrders;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        if (mSession != null) {
            json.put(JSONLabel.USERNAME, mUsername);
            json.put(JSONLabel.PASSWORD, mPassword);
            json.put(JSONLabel.SESSION, mSession);
        }
        return json;
    }

    public void restoreUser(JSONObject json) throws JSONException {
        if ( json.has(JSON_SESSION) ) {
            mUsername = json.getString(JSONLabel.USERNAME);
            mPassword = json.getString(JSONLabel.PASSWORD);
            mSession = json.getString(JSONLabel.SESSION);
        }
    }

    public boolean saveUser() {
        try {
            mUserInfoSaver.saveUserInfo(this);
            Log.d(TAG, "saved file " );
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.d(TAG, "error saving ", e );
            return false;
        }
    }

    public void update(JSONObject json) throws JSONException, UnsupportedEncodingException {
        setLatitude(json.getDouble(JSONLabel.LATITUDE));
        setLongitude(json.getDouble(JSONLabel.LONGITUDE));
        //String addr = json.getString(JSONLabel.ADDRESS);
        //setAddress(new String(addr.getBytes(), "UTF-8"));
        setAddress(json.getString(JSONLabel.ADDRESS));
        setFee(json.getInt(JSONLabel.FEE));
        setPrice(json.getString(JSONLabel.PRICE));
        setDisplayName(json.getString(JSONLabel.NAME));
        setPhone(json.getString(JSONLabel.PHONE));
        setDoneOrders(json.getInt(JSONLabel.DONE_ORDERS));
    }
}
