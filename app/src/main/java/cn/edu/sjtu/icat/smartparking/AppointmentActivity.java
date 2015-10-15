package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Ruochen on 2015/10/15.
 */
public class AppointmentActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AppointmentFragment.newInstance(getIntent().getExtras());
    }
}
