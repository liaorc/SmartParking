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
import android.widget.ImageButton;
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

import cz.msebera.android.httpclient.Header;

/**
 * Created by Ruochen on 2015/10/12.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "main_fragment";
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
    private ImageButton mParkNowButton;
    private Animation mHide;

    private float mY1, mY2;
    static final int MIN_DISTANCE = 150;

    private EditText mSearchLocationEditText;
    private Button mSearchLocationButton;
    private String mCity;
    private LatLng mDestination;


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

        mSearchLocationEditText = (EditText)v.findViewById(R.id.search_locationEditText);
        mSearchLocationButton = (Button)v.findViewById(R.id.search_locationButton);


        mSearch = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    if(result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
                        mSearch.geocode(new GeoCodeOption()
                                .city("上海")
                                .address("东川路800号"));
                    }
                    Log.d(TAG, "没有结果");
                    Log.d(TAG, "error code: " + result.error);
                    return;
                }
                //获取地理编码结果
                mDestination = result.getLocation();
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
                .city("上海市")
                .address("上海市闵行区东川路800号"));

        mParkNowButton = (ImageButton)v.findViewById(R.id.menu_park_nowButton);
        mParkNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "日狗日狗日", Toast.LENGTH_SHORT).show();

                try {
                    ServerRequest serverRequest = new ServerRequest();
                    serverRequest.setString(JSONLabel.SESSION,
                            CurrentUser.get(getActivity()).getSession());
                    serverRequest.setDouble(JSONLabel.RADIUS, 2000);
                    serverRequest.setDouble(JSONLabel.DEST_LNG, mDestination.longitude);
                    serverRequest.setDouble(JSONLabel.DEST_LAT, mDestination.latitude);
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                    client.get(serverRequest.queryParks(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.d(TAG, "parks: "+ new String(responseBody));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
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
