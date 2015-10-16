package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "main_fragment";

    private static final int STATE_PARK_NOW = 0;
    private static final int STATE_PARK_APPOINTMENT = 1;

    private int mParkState;

    // 百度地图控件
    private MapView mMapView = null;
    // 百度地图对象
    private BaiduMap mBaiduMap;
    private GeoCoder mSearch;
    private boolean mMenuClosed = true;

    // 定位相关声明
    public LocationClient mLocationClient = null;

    boolean isFirstLoc = true;

    private ViewGroup mMenuView;
    private ViewGroup mSearchView;
    private ViewGroup mParkListView;
    private MenuItem mMenuButton;
    private ImageButton mParkNowButton;
    private ImageButton mParkAppointmentButton;
    private Button mConfirmParkButton;
    private Animation mHide;

    private float mY1, mY2;
    static final int MIN_DISTANCE = 150;

    private EditText mSearchLocationEditText;
    private Button mSearchLocationButton;
    private String mCity;
    private LatLng mDestination;
    private TextView mParkListDestinationText;

    private ListView mParkList;

    private ParkInfoAdapter mParkInfoAdapter;
    private ArrayList<ParkInfo> mParkInfos;

    private void setParkState(int state) {
        mParkState = state;
        if (state == STATE_PARK_NOW) {
            mSearchLocationButton.setText(getResources().getText(R.string.park_now));
            mConfirmParkButton.setText(getResources().getText(R.string.park_now));
        }else {
            mSearchLocationButton.setText(getResources().getText(R.string.park_appointment));
            mConfirmParkButton.setText(getResources().getText(R.string.park_appointment));
        }
    }

    private class ParkInfoAdapter extends ArrayAdapter<ParkInfo> {
        public ParkInfoAdapter(ArrayList<ParkInfo> parks) {
            super(getActivity(), 0, parks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_park_info, null);

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

            ImageView checkBox = (ImageView)convertView.findViewById(R.id.list_item_select_checkbox);
            if(info.isSelected()) {
                checkBox.setImageResource(R.drawable.selected_button240x240);
            } else {
                checkBox.setImageResource(R.drawable.unselected_button240x240);
            }

            return convertView;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getActivity().getApplicationContext());
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_main, null);

        //map initialization
        mMapView = (MapView)v.findViewById(R.id.bmapview);
        mMapView.showZoomControls(false);  // disable zoom buttons
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getActivity().getApplicationContext()); // 实例化LocationClient类
        setLocationOption();
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        //mLocationClient.requestLocation();
        mLocationClient.start();


        mMenuView = (ViewGroup)v.findViewById(R.id.main_menu_layout);
        mMenuView.setVisibility(View.GONE);

        mSearchView = (ViewGroup)v.findViewById(R.id.search_layout);
        mSearchView.setVisibility(View.GONE);

        mParkListView = (ViewGroup)v.findViewById(R.id.park_list_layout);
        mParkListView.setVisibility(View.GONE);

        mSearchLocationEditText = (EditText)v.findViewById(R.id.search_locationEditText);
        mSearchLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    mSearchLocationButton.setTextColor(getResources().getColor(R.color.black));
                } else {
                    mSearchLocationButton.setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mSearchLocationButton.setTextColor(getResources().getColor(R.color.black));
                } else {
                    mSearchLocationButton.setTextColor(getResources().getColor(R.color.main_text_color));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "After Text Change");
            }
        });
        mSearchLocationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d(TAG, "Enter!!!");
                    searchLocation();
                    return true;
                }
                return false;
            }
        });


        mSearchLocationButton = (Button)v.findViewById(R.id.search_locationButton);
        mSearchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchLocationEditText.getWindowToken(), 0);
                searchLocation();
            }
        });


        mParkNowButton = (ImageButton)v.findViewById(R.id.menu_park_nowButton);
        mParkNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParkState == STATE_PARK_NOW) {
                    toggleSearchView();
                }else {
                    if(mSearchView.getVisibility() == View.GONE)
                        toggleSearchView();
                }
                setParkState(STATE_PARK_NOW);

            }
        });
        mParkAppointmentButton = (ImageButton)v.findViewById(R.id.menu_park_appointmentButton);
        mParkAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParkState == STATE_PARK_APPOINTMENT) {
                    toggleSearchView();
                }else {
                    if(mSearchView.getVisibility() == View.GONE)
                        toggleSearchView();
                }
                setParkState(STATE_PARK_APPOINTMENT);
            }
        });

        mParkList = (ListView)v.findViewById(R.id.park_list);
        mParkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParkInfo info = (ParkInfo)mParkList.getItemAtPosition(position);

                info.setSelected(!info.isSelected());
                mParkInfoAdapter.notifyDataSetChanged();

                for(int i=0; i<mParkList.getAdapter().getCount(); i++) {
                    ParkInfo tmp = (ParkInfo)mParkList.getAdapter().getItem(i);
                    if(tmp.isSelected()) {
                        mConfirmParkButton.setTextColor(getResources().getColor(R.color.black));
                        return;
                    }
                }
                mConfirmParkButton.setTextColor(getResources().getColor(R.color.main_text_color));
            }
        });

        mParkListDestinationText = (TextView)v.findViewById(R.id.search_locationTextView);

        mConfirmParkButton = (Button)v.findViewById(R.id.confirm_parkButton);
        mConfirmParkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConfirmParkButton.getCurrentTextColor() != getResources().getColor(R.color.black)) {
                    return;
                }
                toggleParkListView();
                if(mParkState == STATE_PARK_NOW) {
                    Log.d(TAG, "Send park request now");
                } else {
                    startAppointment();
                }
            }
        });

//        mParkInfoAdapter = new P
//        mParkList.setAdapter(new ArrayAdapter<String>(getActivity(),
//                R.layout.list_item_park_info, strs));

        return v;
    }

    private void startAppointment() {
        Intent i = new Intent(getActivity(), AppointmentActivity.class);
        Bundle arg = new Bundle();
//                i.putParcelableArrayListExtra(AppointmentFragment.EXTRA_LIST, mParkInfos);
//                i.putExtra(AppointmentFragment.EXTRA_ADDRESS, mSearchLocationEditText.getText().toString());
        arg.putParcelableArrayList(AppointmentFragment.EXTRA_LIST, mParkInfos);
        arg.putString(AppointmentFragment.EXTRA_ADDRESS, mSearchLocationEditText.getText().toString());
        i.putExtras(arg);
        startActivity(i);
    }

    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(1000); // 设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
    }

    private void searchLocation() {
        Toast.makeText(getActivity(), "日狗日狗日", Toast.LENGTH_SHORT).show();
        if(mSearch!=null)
            mSearch.destroy();
        mSearch = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    if(result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
                        mSearch.geocode(new GeoCodeOption()
                                .city(mCity)
                                .address(mSearchLocationEditText.getText().toString()));
                    }
                    Toast.makeText(getActivity(), "没有地理结果", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error code: " + result.error);
                    return;
                }
                //获取地理编码结果
                mDestination = result.getLocation();
                Log.d(TAG, "得到地址: " + mDestination.latitude+ "," + mDestination.longitude);
                searchParks(mDestination);
                //addMarker(result.getLocation().latitude, result.getLocation().longitude);

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                //获取反向地理编码结果
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.geocode(new GeoCodeOption()
                .city(mCity)
                .address(mSearchLocationEditText.getText().toString()));
    }

    private void searchParks(LatLng destination) {
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setString(JSONLabel.SESSION,
                    CurrentUser.get(getActivity()).getSession());
            serverRequest.setDouble(JSONLabel.RADIUS, 5);
            serverRequest.setDouble(JSONLabel.DEST_LNG, destination.longitude);
            serverRequest.setDouble(JSONLabel.DEST_LAT, destination.latitude);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
            client.get(serverRequest.queryParks(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(TAG, "parks: " + new String(responseBody));
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        if (json.getInt(JSONLabel.STATUS) == 0) {
                            mParkInfos = ParkListBuilder.fromString(json.getString(JSONLabel.DATA));
                            for (int i = 0; i < mParkInfos.size(); i++) {
                                Log.d(TAG, "park " + i + ": " + mParkInfos.get(i).getName());
                            }
                            if(mParkInfos.size()==0) {
                                Toast.makeText(getActivity(), "日狗日狗日，根本没有停车场", Toast.LENGTH_SHORT).show();
                            } else {
                                mParkInfoAdapter = new ParkInfoAdapter(mParkInfos);
                                mParkList.setAdapter(mParkInfoAdapter);
                                mParkListDestinationText.setText(mSearchLocationEditText.getText().toString());
                                mConfirmParkButton.setTextColor(getResources().getColor(R.color.main_text_color));
                                toggleParkListView();
                            }
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "与服务器联系失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
        mMenuButton = menu.findItem(R.id.menu_item_toggle_button);
        mMenuButton.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });
    }

    private void toggleMenu() {
        Animation rotation;
        Animation menuAnime;
        if(mMenuView.getVisibility() == View.GONE ) {
            rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_180);
            menuAnime = AnimationUtils.loadAnimation(getActivity(), R.anim.show_menu);
            mMenuView.setVisibility(View.VISIBLE);
        } else {
            rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_180_2);
            menuAnime = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_menu);
            menuAnime.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMenuView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        rotation.setFillAfter(true);
        mMenuButton.getActionView().startAnimation(rotation);
        mMenuView.startAnimation(menuAnime);
    }
    private void toggleSearchView() {
        Animation anime;
        if(mSearchView.getVisibility() == View.GONE ) {
            //close park list
            if(mParkListView.getVisibility() == View.VISIBLE) {
                toggleParkListView();
            }
            anime = AnimationUtils.loadAnimation(getActivity(), R.anim.show_search_view);
            mSearchView.setVisibility(View.VISIBLE);
        } else {
            anime = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_search_view);
            anime.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mSearchView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        mSearchView.startAnimation(anime);
    }

    private void toggleParkListView() {
        Animation anime;
        if(mParkListView.getVisibility() == View.GONE ) {
            //close search view
            if(mSearchView.getVisibility() == View.VISIBLE) {
                toggleSearchView();
            }
            if(mMenuView.getVisibility() == View.VISIBLE) {
                toggleMenu();
            }
            anime = AnimationUtils.loadAnimation(getActivity(), R.anim.show_search_view);
            mParkListView.setVisibility(View.VISIBLE);
        } else {
            anime = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_search_view);
            anime.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mParkListView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        anime.setDuration(400);
        mParkListView.startAnimation(anime);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        mMapView.onDestroy();
        super.onDestroy();
    }

    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                Log.d(TAG, "return without view");
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据


            if (isFirstLoc) {
                isFirstLoc = false;
                mCity = location.getCity();
                //Log.d(TAG, "get city: " + mCity);

                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别
//              MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    };

}
