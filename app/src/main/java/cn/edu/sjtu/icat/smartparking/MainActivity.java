package cn.edu.sjtu.icat.smartparking;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

}
