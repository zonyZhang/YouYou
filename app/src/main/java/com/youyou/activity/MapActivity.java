package com.youyou.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.youyou.R;
import com.youyou.constant.YouYouConstant.UserField;
import com.youyou.domain.UserItem;
import com.youyou.service.LocationService;
import com.youyou.util.CommonUtil;
import com.youyou.util.JsonUtil;
import com.youyou.util.LogUtil;
import com.youyou.util.MapUtil;
import com.youyou.util.YouYouPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图界面
 *
 * @author zony
 * @time 18-6-19
 */
public class MapActivity extends BaseActivity implements View.OnClickListener,
        LocationSource, AMapLocationListener, ServiceConnection, AMap.OnInfoWindowClickListener,
        AMap.OnMarkerClickListener, AMap.OnMapTouchListener {
    private static final String TAG = "MapActivity";

    /**
     * 地图ｖｉｅｗ
     */
    private MapView mapView;

    /**
     * 设置定位的默认状态
     */
    private MyLocationStyle myLocationStyle;

    /**
     * 清除用户名
     */
    private Button clearUserNameBtn;

    /**
     * 存放共享位置的list
     */
    private List<Marker> showMarks;

    private AMap aMap;

    /**
     * 地图中UI相关设置
     */
    private UiSettings mUiSettings;

    private OnLocationChangedListener mListener;

    private AMapLocationClient mlocationClient;

    /**
     * 高德相关设置
     */
    private AMapLocationClientOption mLocationOption;

    private Intent mIntent;

    /**
     * 实时位置信息用户实例
     */
    private UserItem realUserItem;

    /**
     * 用户名/电话/描述/用户唯一标识
     */
    private String name, tel, des;

    /**
     * 接收的marker
     */
    private Marker markerTourist;

    private LocationService.Binder mBinder;

    /**
     * 两点间距离
     */
    private long distance;

    /**
     * 实时位置
     */
    private LatLng realLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init(savedInstanceState);
        initMap();
        bindSocketServer();
    }

    /**
     * 初始化view及其他初始化
     *
     * @param savedInstanceState 保存实例状态
     * @author zony
     * @time 18-6-13
     */
    private void init(Bundle savedInstanceState) {
        clearUserNameBtn = findViewById(R.id.clear_username);
        mapView = findViewById(R.id.map_view);

        // 此方法必须重写
        mapView.onCreate(savedInstanceState);

        clearUserNameBtn.setOnClickListener(this);
        showMarks = new ArrayList<>();
        initBundleData();
    }

    /**
     * 初始化Bundle数据
     *
     * @author zony
     * @time 18-6-19
     */
    private void initBundleData() {
        mIntent = getIntent();
        realUserItem = CommonUtil.getBundleUserItem(mIntent);
        if (realUserItem != null) {
            name = realUserItem.getName();
            tel = realUserItem.getTel();
            des = realUserItem.getDes();
        }
    }

    /**
     * 初始化map
     *
     * @author zony
     * @time 18-6-13
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();

            // 如果要设置定位的默认状态，可以在此处进行设置
            myLocationStyle = new MyLocationStyle();
        }

        // 设置小蓝点的图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.icon_myself));
//        aMap.setMyLocationStyle(myLocationStyle);

        //设置定位监听
        aMap.setLocationSource(this);

        aMap.setOnMapTouchListener(this);

        //实时交通
        aMap.setTrafficEnabled(false);

        //有普通，卫星，夜间模式
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        //设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        //设置定位的类型为 跟随模式
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));

        //设置地图默认的比例尺是否显示
        mUiSettings.setScaleControlsEnabled(true);

        //设置地图默认的指南针是否显示
        mUiSettings.setCompassEnabled(true);

        //是否显示默认的定位按钮
        mUiSettings.setMyLocationButtonEnabled(true);

        //marker 点击事件
        aMap.setOnMarkerClickListener(this);

        //InfoWindow 点击事件
        aMap.setOnInfoWindowClickListener(this);
    }

    /**
     * 开启实时共享位置信息服务
     *
     * @author zony
     * @time 18-6-13
     */
    private void bindSocketServer() {
        Intent serverIntent = new Intent(this, LocationService.class);
        bindService(serverIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_username:
                YouYouPreference.getInstance(this).putData(UserField.NAME, "");
                break;
            default:
                break;
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        // 设置定位的类型为 跟随模式
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));

        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                // 显示系统小蓝点
                mListener.onLocationChanged(aMapLocation);
                double realLat = aMapLocation.getLatitude();
                double realLng = aMapLocation.getLongitude();
                realLatLng = new LatLng(realLat, realLng);
                sendDataToServer(realLat, realLng);
            } else {
                if (aMapLocation != null) {
                    LogUtil.i(TAG, "errorCode: " + aMapLocation.getErrorCode()
                            + ",errorInfo: " + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker == null || mIntent == null) {
            return;
        }
        mIntent.setClass(this, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        UserItem userItem = new UserItem();
        userItem.setName(marker.getTitle());
        userItem.setTel((String) marker.getObject());
        userItem.setDes(marker.getSnippet());
        bundle.putParcelable(UserField.USERITEM, userItem);
        mIntent.putExtras(bundle);
        CommonUtil.startActivity(this, mIntent, false);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != marker && null == marker.getTitle()) {
            marker.setTitle(name);
            marker.setSnippet(des);
            marker.setObject(tel);
        } else {
            distance = (long) AMapUtils.calculateLineDistance(realLatLng, marker.getPosition());
            String distanceUnit = "M";
            if (distance >= 1000) {
                distanceUnit = "KM";
                distance = distance / 1000;
            }
            marker.setSnippet(marker.getSnippet() + "\n" +
                    String.format(getString(R.string.map_activity_distance), distance) + distanceUnit);
        }

        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mBinder = (LocationService.Binder) iBinder;
        receiveDataFromServer();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    /**
     * 显示共享用户标记
     *
     * @param data 用户经纬度信息
     * @author zony
     * @time 18-6-13
     */
    public void showTouristMarker(String data) {
        MapUtil.removeMarker(showMarks);
        List<UserItem> userItemList = JsonUtil.parseJson(this, data);
        if (null == userItemList || userItemList.size() <= 0) {
            LogUtil.w(TAG, "showTouristMarker userItemList is null, return!");
            return;
        }
        int resId = R.mipmap.icon_tourist;
        for (UserItem userItem : userItemList) {
            if (!userItem.isOnline()) {
                resId = R.mipmap.ic_launcher_round;
            }
            markerTourist = MapUtil.addMarker(this, aMap, userItem, resId);
            showMarks.add(markerTourist);
        }
    }

    /**
     * 发送数据到服务器
     *
     * @param realLat 实时经度
     * @param realLng 实时纬度
     * @author zony
     * @time 18-6-13
     */
    private void sendDataToServer(double realLat, double realLng) {
        if (realUserItem == null) {
            LogUtil.w(TAG, "sendDataToServer userItem is null, return!");
            return;
        }
        realUserItem.setLat(realLat);
        realUserItem.setLng(realLng);
        if (mBinder != null) {
            mBinder.sendDataToServer(JsonUtil.packageJson(realUserItem));
        }
    }

    /**
     * 接收来自服务器数据
     *
     * @author zony
     * @time 18-6-13
     */
    private void receiveDataFromServer() {
        if (mBinder != null) {
            if (mBinder.getService() == null) {
                return;
            }
            mBinder.getService().setCallBack(new LocationService.ICallBack() {
                @Override
                public void onDateChange(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showTouristMarker(data);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        // 只定位，不进行其他操作
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
            mlocationClient = null;
        }

        if (null != showMarks) {
            showMarks.clear();
            showMarks = null;
        }
        unbindService(this);
        if (null != mBinder) {
            mBinder = null;
        }
        realUserItem = null;
    }
}
