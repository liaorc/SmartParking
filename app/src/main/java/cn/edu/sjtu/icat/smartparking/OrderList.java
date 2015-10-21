package cn.edu.sjtu.icat.smartparking;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Ruochen on 2015/10/20.
 */
public class OrderList {
    private static final String TAG = "QUERY_LIST";

    private ArrayList<Order> mOrders;
    private ArrayList<Order> mConfirmedOrders;
    private ArrayList<Order> mFinishedOrders;

    private static OrderList sOrderList;
    private Context mAppContext;


    private OrderList (Context appContext) {
        mAppContext = appContext;
        mOrders = new ArrayList<Order>();
        mConfirmedOrders = new ArrayList<Order>();
        mFinishedOrders = new ArrayList<Order>();
    }

    public static OrderList get(Context c) {
        if (sOrderList == null) {
            sOrderList = new OrderList(c.getApplicationContext());
        }
        return sOrderList;
    }

    public void updateConfirmedOrder(String data) {
        mConfirmedOrders.clear();
        try {
            //task.queryOrders();
            //int status = task.getStatusCode();
            JSONArray array = (JSONArray) new JSONTokener(data).nextValue();
            //task.getOrderList();
            for ( int i=0 ; i<array.length() ; i++ ) {
                Order order = new Order(array.getJSONObject(i));
                mConfirmedOrders.add(order);
                Log.d(TAG, array.getJSONObject(i).toString());
                Log.d(TAG, "submit_time: "+order.getSubmitTime().toString() + ", " + order.getParkInfo().getName());
            }
        } catch (Exception e) {
            Log.d(TAG, "Req Error" + e);
            e.printStackTrace();
        }
        Log.d(TAG, "IDKW :" + mConfirmedOrders.size());
    }

    public void buildList(ArrayList<OrderListElement> list) {
        list.clear();

        OrderListElement o = new OrderListElement();
        list.add(new OrderListElement(OrderListElement.TYPE_TAG_CONFIRMED));
        o = new OrderListElement();
        if ( mConfirmedOrders.size() == 0 ) {
            list.add(new OrderListElement(OrderListElement.TYPE_EMPTY));
        } else {
            for (int i=0;i<mConfirmedOrders.size();i++){
                OrderListElement element = new OrderListElement(OrderListElement.TYPE_ORDER_CONFIRMED);
                element.setOrder(mConfirmedOrders.get(i));
                list.add(element);

            }
        }
        list.add(new OrderListElement(OrderListElement.TYPE_TAG_FINISHED));
        if ( mFinishedOrders.size() == 0 ) {
            list.add(new OrderListElement(OrderListElement.TYPE_EMPTY));
        } else {
            for (int i=0;i<mFinishedOrders.size();i++){
                list.add(new OrderListElement(OrderListElement.TYPE_ORDER_FINISHED));
            }
        }
    }

    public void updateOrderList(String data) {
        clearOrder();
        try {
            //task.queryOrders();
            //int status = task.getStatusCode();
            JSONArray array = (JSONArray) new JSONTokener(data).nextValue();
            //task.getOrderList();
            for ( int i=0 ; i<array.length() ; i++ ) {
                Order order = new Order(array.getJSONObject(i));
                mOrders.add(order);
                Log.d(TAG, array.getJSONObject(i).toString());
                Log.d(TAG, "submit_time: "+order.getSubmitTime().toString());
            }
        } catch (Exception e) {
            Log.d(TAG, "Req Error");
        }
        Log.d(TAG, "IDKW :" + mOrders.size());
    }

    public ArrayList<Order> getOrders() {
        return mOrders;
    }

    public Order getOrder(int id) {
        for (Order order: mOrders) {
            if ( order.getOrderId() == id ) {
                return order;
            }
        }
        return null;
    }


    public void clearOrder() {
        mOrders.clear();
    }
}
