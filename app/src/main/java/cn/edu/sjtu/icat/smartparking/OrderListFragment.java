package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/10/20.
 */
public class OrderListFragment extends Fragment {
    private static final String TAG = "list_fragment";

    private PullToRefreshListView mListView;
    private ArrayList<OrderListElement> mOrderListElements;
    private OrderListAdapter mOrderListAdapter;


    private class OrderListAdapter extends ArrayAdapter<OrderListElement> {
        public OrderListAdapter(ArrayList<OrderListElement> orders) {
            super(getActivity(), 0, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_park_info, null);


            OrderListElement element = getItem(position);
            Log.d(TAG, "pos " + position + "type" + element.getElementType());
            switch (element.getElementType()) {
                case OrderListElement.TYPE_EMPTY :
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_empty, null);
                    break;
                case OrderListElement.TYPE_TAG_CONFIRMED :
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                    break;
                case OrderListElement.TYPE_TAG_FINISHED :
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_tag, null);
                    break;
                default:
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_order, null);
                    break;
            }
            return convertView;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_list, null);

        mListView = (PullToRefreshListView)v.findViewById(R.id.order_listView);
        mOrderListElements = new ArrayList<OrderListElement>();
        mOrderListAdapter = new OrderListAdapter(mOrderListElements);
        mListView.setAdapter(mOrderListAdapter);

        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION,
                    CurrentUser.get(getActivity()).getSession());
            serverRequest.setInt(JSONLabel.TYPE, 0);
            serverRequest.setInt(JSONLabel.PAGE, 0);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.getOrders(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "info: " + (new String(responseBody)));
                    ServerRequest serverRequest = new ServerRequest();
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getInt(JSONLabel.STATUS) == 0) {
                            OrderList.get(getActivity()).updateConfirmedOrder(json.getString(JSONLabel.DATA));
                            mOrderListElements = OrderList.get(getActivity()).buildList();
                            mListView.setAdapter(new OrderListAdapter(mOrderListElements));
                            Log.d(TAG, "count: " + mOrderListElements.size());
//                            mOrderListAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                    } finally {
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Req Error");
        }



        return v;
    }
}
