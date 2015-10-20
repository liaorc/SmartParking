package cn.edu.sjtu.icat.smartparking;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wefika.horizontalpicker.HorizontalPicker;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ruochen on 2015/10/14.
 */
public class AppointmentFragment extends Fragment {
    private static final String TAG = "appointment_fragment";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_LIST = "list";
    private static final int REQUEST_DATE_TIME = 0;
    private static final int sPrice[]={0,5,10,15,25,50};

    private ArrayList<ParkInfo> mParkInfos;
    private String mParkAddress;
    private TextView mParkAddressTextView;

    private ListView mListView;
    private ParkInfoAdapter mAdapter;

    private TextView mAppointmentTimeButton;

    private Date mDate;
    private int mPrice;


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
        setHasOptionsMenu(true);

        mDate = new Date();

        for(int i=0; i<mParkInfos.size(); i++) {
            Log.d(TAG, "得到:" + mParkInfos.get(i).getName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_appointment_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sendResult(Activity.RESULT_CANCELED);
                getActivity().finish();
                return true;
            case R.id.menu_confirm_appointment_button:
                sendResult(Activity.RESULT_OK);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAppointmentInfo() {
        mParkAddressTextView.setText(mParkAddress);
        mAppointmentTimeButton.setText(DateFormat.format("MM月dd日 HH:mm", mDate));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointment, null);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

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
                dialog.setTargetFragment(AppointmentFragment.this, REQUEST_DATE_TIME);
                dialog.show(fm, "1");
            }
        });


        mParkAddressTextView = (TextView)header.findViewById(R.id.park_appointment_addressTextView);

        HorizontalPicker pricePicker = (HorizontalPicker)v.findViewById(R.id.price_picker);
        pricePicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                mPrice = sPrice[index];
                Toast.makeText(getActivity(), "Item selected is: " + mPrice, Toast.LENGTH_LONG).show();
            }
        });

        setAppointmentInfo();
        return v;
    }

    private void sendResult(int resultCode) {

        Intent i = new Intent();
        //i.putExtra(EXTRA_DATE, mDate);

        getActivity().setResult(resultCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DATE_TIME) {
            if (resultCode == Activity.RESULT_OK) {
                mDate =(Date)data.getSerializableExtra(DateTimePickerFragment.EXTRA_DATE);
                setAppointmentInfo();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
