package com.youyou.socketserver.tcp;

import com.youyou.socketserver.domain.ResponseCode;
import com.youyou.socketserver.domain.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

public class Sender implements Runnable {
    private User user;
    private List<User> users = null;

    public Sender(User user, List<User> users) {
        this.user = user;
        this.users = users;
    }

    @Override
    public void run() {
        while (user.isOnLine()) {
            try {
                Thread.sleep(3 * 1000);// 3秒发1次
                user.getClient().send(ret().toString() + "\n");// 发送
            } catch (Exception e) {
                user.setOnLine(false);// 设置离线
                e.printStackTrace();
            }
        }
    }

    private JSONObject ret() {
        JSONObject jsonObject = null;
        JSONArray array = new JSONArray();
        for (User user : users) {
            System.out.println("Sender user: " + user.toString());
            JSONObject us = new JSONObject();
            us.put("uuid", user.getUuid());
            us.put("lat", user.getLat());
            us.put("lng", user.getLng());
            us.put("name", user.getName());
            us.put("tel", user.getTel());
            us.put("des", user.getDes());
            us.put("isonline", user.isOnLine());
            array.add(us);
        }
        jsonObject = ResponseCode.s_200.toJson(array);
        return jsonObject;
    }
}