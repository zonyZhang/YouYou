package com.youyou.util;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.youyou.R;
import com.youyou.domain.UserItem;

import java.util.List;

/**
 * 地图工具类
 *
 * @author zony
 * @time 18-5-7
 */
public class MapUtil {
    private static final String TAG = "MapUtil";

    /**
     * 添加地图标记
     *
     * @param userItem 用户实例
     * @param resId    自定义标记图标id
     * @return marker
     * @author zony
     * @time 18-6-14
     */
    public static Marker addMarker(Context context, AMap aMap, UserItem userItem, int resId) {
        if (null == aMap || null == userItem) {
            return null;
        }
        double shareLat = userItem.getLat();
        double shareLng = userItem.getLng();
        LogUtil.i("zony", "shareLat: " + shareLat + ",shareLng: " + shareLng);
        LatLng latLng = new LatLng(shareLat, shareLng);
//        MarkerOptions markOptiopns = new MarkerOptions().position(latLng)
//                .title(userItem.getName()).snippet(userItem.getDes()).anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory
//                        .fromView(getBitmapView(context, userItem, resId)));
        MarkerOptions markOptiopns = new MarkerOptions().position(latLng)
                .title(userItem.getName()).snippet(userItem.getDes()).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromResource(resId));
        Marker marker = aMap.addMarker(markOptiopns);

        if (null != marker) {
            marker.setObject(userItem.getTel());
            Animation startAnimation = new AlphaAnimation(0, 1);
            startAnimation.setDuration(600);
            marker.setAnimation(startAnimation);
            marker.startAnimation();
//            marker.showInfoWindow();
        }
        return marker;
    }

    public static View getBitmapView(Context context, UserItem userItem, int resId)
    {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.custom_info_window, null);
        ImageView ivBadge = (ImageView) view.findViewById(R.id.badge);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvSnippet = (TextView) view.findViewById(R.id.snippet);
        tvTitle.setText(userItem.getName());
        tvSnippet.setText(userItem.getDes());
        ivBadge.setImageResource(resId);
        return view;
    }

    /**
     * 移除地图标记
     *
     * @param showMarks     标记集合
     * @param smoothMarkers 平滑移动的标记集合
     * @author zony
     * @time 18-6-14
     */
    public static void removeMarker(List<Marker> showMarks, List<SmoothMoveMarker> smoothMarkers) {
        if (null != showMarks && showMarks.size() > 0) {
            for (Marker showMark : showMarks) {
                if (null != showMark) {
                    showMark.remove();
                }
            }
        }

        if (null != smoothMarkers && smoothMarkers.size() > 0) {
            for (SmoothMoveMarker smoothMoveMarker : smoothMarkers) {
                if (null != smoothMoveMarker) {
                    smoothMoveMarker.destroy();
                }
            }
        }
    }


    /**
     * 移除地图标记
     *
     * @param list 标记集合
     * @author zony
     * @time 18-6-14
     */
    public static void removeMarker(List<Marker> list) {
        if (list != null && list.size() > 0) {
            for (Marker marker : list) {
                if (null != marker) {
                    marker.remove();
                }
            }
        }
    }
}
