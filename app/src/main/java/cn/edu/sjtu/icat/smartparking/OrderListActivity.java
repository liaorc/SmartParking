package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;

/**
 * Created by Ruochen on 2015/10/20.
 */
public class OrderListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new OrderListFragment();
    }
}
