package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ruochen on 2015/10/14.
 */
public class AppointmentFragment extends Fragment {
    private static final String TAG = "appointment_fragment";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_LIST = "list";

    private ArrayList<ParkInfo> mParkInfos;
    private String mParkAddress;
    private TextView mParkAddressTextView;

    private ListView mListView;
    private ParkInfoAdapter mAdapter;

    private TextView mAppointmentTimeButton;


    private class ParkInfoAdapter extends ArrayAdapter<ParkInfo> {
        public ParkInfoAdapter(ArrayList<ParkInfo> parks) {
            super(getActivity(), 0, parks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_park_info_appointment, null);

            ParkInfo info = getItem(position);

            TextView parkName = (TextView)convertView.findViewById(R.id.list_item_park_name);
            parkName.setText(info.getName());
            TextView parkAddress = (TextView)convertView.findViewById(R.id.list_item_park_address);
            parkAddress.setText(info.getAddress());
            TextView parkDistance = (TextView)convertView.findViewById(R.id.list_item_park_distance);
            parkDistance.setText(MiscUtils.getDistanceDescription(info.getDistance()));

            TextView fee = (TextView)convertView.findViewById(R.id.list_item_fee);
            fee.setText("议价:" + info.getFee());
            TextView price = (TextView)convertView.findViewById(R.id.list_item_price);
            price.setText("收费:" + info.getPrice());

            return convertView;
        }

    }



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
        View v = inflater.inflate(R.layout.fragment_appointment, null);
        mListView = (ListView)v.findViewById(R.id.park_info_listView);
        View header = inflater.inflate(R.layout.header_view_fragment_appointment, mListView, false);

        mListView.addHeaderView(header);
        mAdapter = new ParkInfoAdapter(mParkInfos);
        mListView.setAdapter(mAdapter);

        mAppointmentTimeButton = (TextView)header.findViewById(R.id.park_appointment_timeButton);
        mAppointmentTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                DateTimePickerFragment dialog = DateTimePickerFragment.newInstance(new Date());
                dialog.setTargetFragment(AppointmentFragment.this, 1);
                dialog.show(fm, "1");
            }
        });


        mParkAddressTextView = (TextView)header.findViewById(R.id.park_appointment_addressTextView);
        mParkAddressTextView.setText(mParkAddress);

        return v;
    }
}
