package cn.edu.sjtu.icat.smartparking;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by Ruochen on 2015/10/12.
 */
public abstract class SingleFragmentActivity extends Activity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null) {
            //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //getActionBar().setCustomView(R.layout.action_bar);
        }
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

    }
}