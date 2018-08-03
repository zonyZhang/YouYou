package com.youyou.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息
 *
 * @author zony
 * @time 18-6-13
 */
public class UserItem implements Parcelable {

    private String uuid;

    /**
     * 用户名
     */
    private String name;

    /**
     * 电话
     */
    private String tel;

    /**
     * 描述
     */
    private String des;

    /**
     * 经度
     */
    private double lng;

    /**
     * 纬度
     */
    private double lat;

    /**
     * 是否在线
     */
    private boolean isOnline;


    public String getUuid() {
        return uuid;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserItem) {
            UserItem userItem = (UserItem) obj;
            return (uuid.equals(userItem.uuid));
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "UserItem{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", des='" + des + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", isOnline=" + isOnline +
                '}';
    }

    public UserItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.name);
        dest.writeString(this.tel);
        dest.writeString(this.des);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
    }

    protected UserItem(Parcel in) {
        this.uuid = in.readString();
        this.name = in.readString();
        this.tel = in.readString();
        this.des = in.readString();
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.isOnline = in.readByte() != 0;
    }

    public static final Creator<UserItem> CREATOR = new Creator<UserItem>() {
        @Override
        public UserItem createFromParcel(Parcel source) {
            return new UserItem(source);
        }

        @Override
        public UserItem[] newArray(int size) {
            return new UserItem[size];
        }
    };
}
