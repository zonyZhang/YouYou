package com.youyou.socketserver.domain;

import com.youyou.socketserver.tcp.Client;

import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private static final long serialVersionUID = 7359389165310027183L;

    private String uuid;
    private double lng;
    private double lat;

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

    private Socket socket = null;
    private Client client = null;
    private boolean onLine = true;// 是否在线

    public User(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        // uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            return (uuid.equals(user.uuid) && name.equals(user.name) && tel.equals(user.tel));
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "User{" + "uuid='" + uuid + '\'' + ", lng=" + lng + ", lat=" + lat + ", name='" + name + '\'' + ", tel='"
                + tel + '\'' + ", des='" + des + '\'' + ", socket=" + socket + ", client=" + client + ", onLine="
                + onLine + '}';
    }
}
