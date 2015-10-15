package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Ruochen on 2015/10/14.
 */
public class AppointmentFragment extends Fragment {
    private static final String TAG = "appointment_fragment";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_LIST = "list";

    private ArrayList<ParkInfo> mParkInfos;
    private String mParkAddress;


    public static AppointmentFragment newInstance(Bundle arg) {
        AppointmentFragment fragment = new AppointmentFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParkAddress = getArguments().getString(EXTRA_ADDRESS);
        mParkInfos = getArguments().getParcelableArrayList(EXTRA_LIST);
        setRetainInstance(true);

        for(int i=0; i<mParkInfos.size(); i++) {
            Log.d(TAG, "得到:" + mParkInfos.get(i).getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test_layout, null);
        return v;
    }
}
