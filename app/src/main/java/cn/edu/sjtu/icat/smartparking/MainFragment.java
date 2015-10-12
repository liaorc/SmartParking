package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

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
import com.baidu.mapapi.search.geocode.GeoCoder;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "main_activity";
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
    private MenuItem mMenuButton;
    private Animation mHide;

    private float mY1, mY2;
    static final int MIN_DISTANCE = 150;

    private EditText mSearchLocationEditText;
    private Button mSearchLocationButton;
    private String mCity;


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

        return v;
    }
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(1000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
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
        if(mMenuClosed) {
            rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_180);
            menuAnime = AnimationUtils.loadAnimation(getActivity(), R.anim.show_menu);
            mMenuView.setVisibility(View.VISIBLE);
        } else {
            rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_180_2);
            menuAnime = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_menu);
            menuAnime.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMenuView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //
                }
            });
        }
        mMenuClosed = !mMenuClosed;
        rotation.setFillAfter(true);
        mMenuButton.getActionView().startAnimation(rotation);
        mMenuView.startAnimation(menuAnime);
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
