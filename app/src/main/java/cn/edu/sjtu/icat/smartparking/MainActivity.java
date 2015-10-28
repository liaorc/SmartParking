package cn.edu.sjtu.icat.smartparking;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;

public class MainActivity extends SingleFragmentActivity {
    private static final String TAG = "main_activity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext());
        Log.d(TAG, "clientid: " + PushManager.getInstance().getClientid(this.getApplicationContext()));
    }

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

}
