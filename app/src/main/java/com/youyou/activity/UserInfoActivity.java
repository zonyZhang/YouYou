package com.youyou.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.youyou.R;
import com.youyou.domain.UserItem;
import com.youyou.service.LocationService;
import com.youyou.util.CommonUtil;
import com.youyou.util.JsonUtil;
import com.youyou.util.LogUtil;
import com.youyou.util.MapUtil;

import java.util.ArrayList;
import java.util.List;

import static com.youyou.constant.YouYouConstant.UserField;

/**
 * 用户信息界面
 *
 * @author zony
 * @time 18-6-19
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "UserInfoActivity";

    /**
     * 用户名/电话
     */
    private TextView userNameTv, telTv;

    private UserItem mUserItem;

    private String tel;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        userNameTv = findViewById(R.id.user_name);
        telTv = findViewById(R.id.user_tel);
        mUserItem = CommonUtil.getBundleUserItem(getIntent());
        userNameTv.setText(String.format(getString(R.string.userinfo_text_name), mUserItem.getName()));
        tel = mUserItem.getTel();
        telTv.setText(tel);
        telTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_tel:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
