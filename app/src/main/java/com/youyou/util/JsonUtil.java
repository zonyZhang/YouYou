package com.youyou.util;

import android.content.Context;

import com.youyou.constant.YouYouConstant.UserField;
import com.youyou.domain.UserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用util工具类
 *
 * @author zony
 * @time 18-5-7
 */
public class JsonUtil {
    private static final String TAG = "CommonUtil";

    /**
     * 解析json数据
     *
     * @param jsonData json数据
     * @author zony
     * @time 18-6-14
     */
    public static List<UserItem> parseJson(Context context, String jsonData) {
        List<UserItem> userItemList = new ArrayList<>();
        if (jsonData == null) {
            LogUtil.e(TAG, "JsonUtil parseJson jsonData is null, return!");
            return null;
        }
        LogUtil.i(TAG, "JsonUtil parseJson jsonData: " + jsonData);
        try {
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            jsonObject = new JSONObject(jsonData);
            int responseCode = jsonObject.getInt("responseCode");
            if (responseCode == 200) {
                jsonArray = jsonObject.getJSONArray("data");
            } else {
                return null;
            }
            LogUtil.i(TAG, "JsonUtil parseJson jsonArray.length(): " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                UserItem userItem = new UserItem();
                JSONObject jsonObjectReal = new JSONObject(jsonArray.get(i).toString());
                double shareLat = jsonObjectReal.optDouble(UserField.LAT);
                double shareLng = jsonObjectReal.optDouble(UserField.LNG);
                String uuid = jsonObjectReal.optString(UserField.UUID);
                String name = jsonObjectReal.optString(UserField.NAME);
                String tel = jsonObjectReal.optString(UserField.TEL);
                String des = jsonObjectReal.optString(UserField.DES);
                boolean isOnline = jsonObjectReal.optBoolean(UserField.ISONLINE);
                if (null != context) {
                    if (uuid.equals(CommonUtil.getUUID(context))) {
                        continue;
                    }
                }
                userItem.setUuid(uuid);
                userItem.setName(name);
                userItem.setTel(tel);
                userItem.setDes(des);
                userItem.setLat(shareLat);
                userItem.setLng(shareLng);
                userItem.setOnline(isOnline);
                userItemList.add(userItem);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "JsonUtil parseJson exception: " + e.getMessage());
            e.printStackTrace();
        }
        return userItemList;
    }

    /**
     * 封装成json数据
     *
     * @param userItem 用户实例
     * @author zony
     * @time 18-6-14
     */
    public static String packageJson(UserItem userItem) {
        if (userItem == null) {
            LogUtil.e(TAG, "JsonUtil packageJson userItem is null, return!");
            return "";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UserField.UUID, userItem.getUuid());
            jsonObject.put(UserField.NAME, userItem.getName());
            jsonObject.put(UserField.TEL, userItem.getTel());
            jsonObject.put(UserField.DES, userItem.getDes());
            jsonObject.put(UserField.LAT, userItem.getLat());
            jsonObject.put(UserField.LNG, userItem.getLng());
        } catch (JSONException e) {
            LogUtil.e(TAG, "JsonUtil packageJson exception: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}  
