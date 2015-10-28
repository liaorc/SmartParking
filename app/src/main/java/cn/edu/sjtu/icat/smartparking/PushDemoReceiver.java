package cn.edu.sjtu.icat.smartparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import org.json.JSONObject;

/**
 * Created by Ruochen on 2015/10/28.
 */
public class PushDemoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null)
                {
                    String data = new String(payload);
                    Log.d("GetuiSdkDemo", "Got Payload:" + data);
                    try {
                        Order order = new Order(new JSONObject(data).getJSONObject(JSONLabel.INFO));
                        Intent i = new Intent(context, OrderDetailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle arg = new Bundle();
                        arg.putParcelable(OrderDetailFragment.EXTRA_ORDER, order);
                        i.putExtras(arg);
                        context.startActivity(i);
                        Log.d("GetuiSdkDemo", "Name:" + order.getParkInfo().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            //添加其他case
            //.........
            default:
                break;
        }
    }
}