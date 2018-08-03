package com.youyou.socketserver.tcp;

import com.youyou.socketserver.domain.ResponseCode;
import com.youyou.socketserver.domain.User;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;

public class Client implements Runnable {
    private final String encoder = "UTF-8";

    private User user;
    private Socket socket;
    private List<User> users = null;

    public Client(Socket socket, List<User> users) {
        this.socket = socket;
        this.users = users;
    }

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    private final String end = "end";// 结束标识

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            User user = new User(socket, this);
            // 启动发送消息类
            System.out.println("Sender Sender >>>>>>>>>>>>>>. Size: " + users.size());
            new Thread(new Sender(user, users)).start();

            byte[] bytes = new byte[1024];
            Integer length = null;
            String inString = null;
            while ((length = inputStream.read(bytes)) != -1) {
                inString = new String(bytes, 0, length, encoder);
                try {
                    // if (user.isOnLine() == false) {
                    // this.close();
                    // break;
                    // }
                    if (end.equals(inString)) {
                        this.close();
                        break;
                    }
                    JSONObject reJson = JSONObject.fromObject(inString);
                    Double lat = reJson.getDouble("lat");
                    Double lng = reJson.getDouble("lng");
                    String uuid = reJson.getString("uuid");
                    String name = reJson.getString("name");
                    String tel = reJson.getString("tel");
                    String des = reJson.getString("des");
                    user.setLat(lat);
                    user.setLng(lng);
                    user.setUuid(uuid);
                    user.setName(name);
                    user.setTel(tel);
                    user.setDes(des);
                    user.setOnLine(true);

                    // if (users.contains(user)) {
                    // for(int i = 0; i < users.size(); i++) {
                    // User userTemp = (User) users.get(i);
                    // if (userTemp.equals(user)) {
                    // users.remove(userTemp);
                    // i--;
                    // }
                    // }
                    // }
                    if (!users.contains(user)) {
                        // System.out.println("Client run add user: " + user.getUuid());
                        users.add(user);
                    }
                    System.out.println("Client receive user: " + user.toString() + ",Size: " + users.size());
                } catch (JSONException e) {
                    JSONObject jso = (ResponseCode.e_201).toJson();
                    this.send(jso.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    user.setOnLine(false);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            System.out.println("Client socket close");
            if (null != users) {
                users.clear();
                users = null;
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送数据
    public void send(String date) throws UnsupportedEncodingException, IOException {
        outputStream.write(date.getBytes(encoder));
        outputStream.flush();
    }

}
