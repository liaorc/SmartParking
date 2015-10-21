package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;

/**
 * Created by Ruochen on 2015/10/21.
 */
public class OrderDetailActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new OrderDetailFragment();
    }
}
