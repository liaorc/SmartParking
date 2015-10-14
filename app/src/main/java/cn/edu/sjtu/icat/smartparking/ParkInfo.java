package cn.edu.sjtu.icat.smartparking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Ruochen on 2015/10/13.
 */
public class ParkInfo {
    private String mName;
    private int mDistance;
    private int mDoneOrders;
    private int mFee;
    private int mBanned;
    private int mNegOrders;
    private double mLng;
    private double mLat;
    private String mAddress;
    private String mPrice;
    private String mPhone;

    private boolean mSelected = false;

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public ParkInfo(JSONObject json) throws JSONException {
        mName = json.getString(JSONLabel.NAME);
        mDistance = json.getInt(JSONLabel.DISTANCE);
        mLng = json.getDouble(JSONLabel.LONGITUDE);
        mLat = json.getDouble(JSONLabel.LATITUDE);
        mDoneOrders = json.getInt(JSONLabel.DONE_ORDERS);
        mNegOrders = json.getInt(JSONLabel.NEG_ORDERS);
        mAddress = json.getString(JSONLabel.ADDRESS);
        mPhone = json.getString(JSONLabel.PHONE);
        mPrice = json.getString(JSONLabel.PRICE);
        mFee = json.getInt(JSONLabel.FEE);
    }

    public int getFee() {
        return mFee;
    }

    public String getName() {
        return mName;
    }

    public int getDistance() {
        return mDistance;
    }

    public int getDoneOrders() {
        return mDoneOrders;
    }

    public int getBanned() {
        return mBanned;
    }

    public int getNegOrders() {
        return mNegOrders;
    }

    public double getLng() {
        return mLng;
    }

    public double getLat() {
        return mLat;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getPhone() {
        return mPhone;
    }
}
