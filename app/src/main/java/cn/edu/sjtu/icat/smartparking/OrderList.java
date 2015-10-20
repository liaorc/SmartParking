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

    private static OrderList sOrderList;
    private Context mAppContext;

    private OrderList (Context appContext) {
        mAppContext = appContext;
        mOrders = new ArrayList<Order>();
    }

    public static OrderList get(Context c) {
        if (sOrderList == null) {
            sOrderList = new OrderList(c.getApplicationContext());
        }
        return sOrderList;
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

    public void addOrder(Order o) {
        mOrders.add(o);
    }

    public void deleteOrder(Order o) {
        mOrders.remove(o);
    }

    public void clearOrder() {
        mOrders.clear();
    }
}
