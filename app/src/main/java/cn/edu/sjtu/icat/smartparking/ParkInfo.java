package cn.edu.sjtu.icat.smartparking;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Ruochen on 2015/10/13.
 */
public class ParkInfo implements Parcelable {
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

    public ParkInfo() {}

    public ParkInfo(JSONObject json) throws JSONException {
        mName = json.getString(JSONLabel.NAME);
        if(json.has(JSONLabel.DISTANCE))
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mDistance);
        dest.writeInt(mDoneOrders);
        dest.writeInt(mFee);
        dest.writeInt(mBanned);
        dest.writeInt(mNegOrders);
        dest.writeDouble(mLng);
        dest.writeDouble(mLat);
        dest.writeString(mAddress);
        dest.writeString(mPrice);
        dest.writeString(mPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static public final Parcelable.Creator<ParkInfo> CREATOR = new Creator<ParkInfo>() {
        @Override
        public ParkInfo createFromParcel(Parcel source) {
            ParkInfo info = new ParkInfo();
            info.mName = source.readString();
            info.mDistance = source.readInt();
            info.mDoneOrders = source.readInt();
            info.mFee = source.readInt();
            info.mBanned = source.readInt();
            info.mNegOrders = source.readInt();
            info.mLng = source.readDouble();
            info.mLat = source.readDouble();
            info.mAddress = source.readString();
            info.mPrice = source.readString();
            info.mPhone = source.readString();
            return info;
        }

        @Override
        public ParkInfo[] newArray(int size) {
            return new ParkInfo[size];
        }
    };

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
