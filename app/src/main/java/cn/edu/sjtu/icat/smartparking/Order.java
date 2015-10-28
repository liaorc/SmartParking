package cn.edu.sjtu.icat.smartparking;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Ruochen on 2015/10/20.
 */
public class Order implements Parcelable {
    public static final int ORDER_TYPE_APPOINTMENT = 1;
    public static final int ORDER_TYPE_INSTANCE = 0;

    private int mOrderId;
    private int mOrderType;
    private Date mSubmitTime;
    private Date mAppointmentTime;
    private Date mConfirmedTime;
    private int mUserId;
    private int mFee;
    private ParkInfo mParkInfo;
    private int mDistance;
    private String mQRCode;

    public Order() {}

    public Order(JSONObject json) throws JSONException {
        mOrderId = json.getInt(JSONLabel.ORDERID);
        mOrderType = json.getInt(JSONLabel.ORDER_TYPE);
        mSubmitTime = new Date(json.getLong(JSONLabel.SUBMIT_TIME)*1000);
        mAppointmentTime = new Date(json.getLong(JSONLabel.APPOINTMENT_TIME)*1000);
        mUserId = json.getInt(JSONLabel.USERID);
        mFee = json.getInt(JSONLabel.FEE);
        if(json.has(JSONLabel.CONFIRMED_TIME))
            mConfirmedTime = new Date(json.getLong(JSONLabel.CONFIRMED_TIME)*1000);
        if(json.has(JSONLabel.PARK_INFO)) {
            //Log.d("Order_test", "user info:" + json.getString(JSONLabel.USER_INFO));
            mParkInfo = new ParkInfo(json.getJSONObject(JSONLabel.PARK_INFO));
        }
        if(json.has(JSONLabel.DISTANCE))
            mDistance = json.getInt(JSONLabel.DISTANCE);
        if(json.has(JSONLabel.QRCODE))
            mQRCode = json.getString(JSONLabel.QRCODE);
    }

    public int getFee() {
        return mFee;
    }

    public void setFee(int fee) {
        mFee = fee;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void setOrderId(int orderId) {
        mOrderId = orderId;
    }

    public int getOrderType() {
        return mOrderType;
    }

    public void setOrderType(int orderType) {
        mOrderType = orderType;
    }

    public Date getSubmitTime() {
        return mSubmitTime;
    }

    public void setSubmitTime(Date submitTime) {
        mSubmitTime = submitTime;
    }

    public Date getAppointmentTime() {
        return mAppointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        mAppointmentTime = appointmentTime;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public ParkInfo getParkInfo() {
        return mParkInfo;
    }

    public void setUserInfo(ParkInfo parkInfo) {
        mParkInfo = parkInfo;
    }

    public int getDistance() {
        return mDistance;
    }

    public String getQRCode() {
        return mQRCode;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mOrderId);
        dest.writeInt(mOrderType);
        dest.writeSerializable(mSubmitTime);
        dest.writeSerializable(mAppointmentTime);
        dest.writeSerializable(mConfirmedTime);
        dest.writeInt(mUserId);
        dest.writeInt(mFee);
        dest.writeParcelable(mParkInfo, flags);
        dest.writeString(mQRCode);


    }
    @Override
    public int describeContents() {
        return 0;
    }

    static public final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            Order order = new Order();
            order.mOrderId = source.readInt();
            order.mOrderType = source.readInt();
            order.mSubmitTime = (Date)source.readSerializable();
            order.mAppointmentTime = (Date)source.readSerializable();
            order.mConfirmedTime = (Date)source.readSerializable();
            order.mUserId = source.readInt();
            order.mFee = source.readInt();
            order.mParkInfo = source.readParcelable(ParkInfo.class.getClassLoader());
            order.mQRCode = source.readString();
            return order;
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

}
