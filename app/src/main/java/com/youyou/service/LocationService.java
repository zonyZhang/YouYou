package com.youyou.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.youyou.MainActivity;
import com.youyou.constant.YouYouConstant;
import com.youyou.constant.YouYouConstant.SendConst;
import com.youyou.util.Executor;
import com.youyou.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 发送或收取位置信息服务
 *
 * @author zony
 * @time 18-6-13
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";

    private Socket socket = null;

    private BufferedWriter writer = null;

    private BufferedReader reader = null;

    private ICallBack callBack = null;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public class Binder extends android.os.Binder {
        public void sendDataToServer(final String localLocateData) {
            Executor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //向服务端发送定位数据
                        if (null != writer) {
                            LogUtil.i(TAG, "LocationService Binder sendData data: " + localLocateData);
                            writer.write(localLocateData + "\n");
                            writer.flush();
                        } else {
                            LogUtil.w(TAG, "LocationService Binder writer is null!");
                        }
                    } catch (Exception e) {
                        LogUtil.i(TAG, "LocationService Binder sendData Exception: " + e.getMessage());
                        startSocket(false);
                        e.printStackTrace();
                    }
                }
            });
        }

        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startSocket(true);
    }

    /**
     * 启动socket连接
     *
     * @param isFirstStart 是否第一次启动,第一次启动不用等待3秒,非第一次则是3秒后自动重连
     * @author zony
     * @time 18-6-15
     */
    private void startSocket(boolean isFirstStart) {
        closeSocket();
        if (!isFirstStart) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                LogUtil.e(TAG, "LocationService startSocket Socket Exception: " + e1.getMessage());
                e1.printStackTrace();
            }
        }
        Executor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(SendConst.IP, SendConst.PORT);
                    LogUtil.i(TAG, "LocationService startSocket socket is connected, ip: " + SendConst.IP + ":" + SendConst.PORT);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (Exception e) {
                    LogUtil.e(TAG, "LocationService startSocket Socket Exception: " + e.getMessage());
                    startSocket(false);
                    e.printStackTrace();
                }

                try {
                    if (null != reader) {
                        String otherLocateData = "";
                        while ((otherLocateData = reader.readLine()) != null) {
                            LogUtil.i(TAG, "LocationService startSocket readLine receive data: " + otherLocateData);
                            if (callBack != null) {
                                callBack.onDateChange(otherLocateData);
                            }
                        }
                    } else {
                        LogUtil.w(TAG, "LocationService startSocket reader is null!");
                    }
                } catch (Exception e) {
                    LogUtil.i(TAG, "LocationService startSocket readLine Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 关闭socket连接
     *
     * @author zony
     * @time 18-6-15
     */
    private void closeSocket() {
        try {
            if (null != writer) {
                writer.close();
                writer = null;
            }

            if (null != reader) {
                reader.close();
                reader = null;
            }

            if (null != socket) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            LogUtil.i(TAG, "LocationService closeSocket readLine Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置数据变化回调
     *
     * @param callBack 数据变化回调
     * @author zony
     * @time 18-6-13
     */
    public void setCallBack(ICallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 数据变化回调
     *
     * @author zony
     * @time 18-6-13
     */
    public interface ICallBack {

        /**
         * 数据变化监听
         *
         * @param data
         * @author zony
         * @time 18-6-13
         */
        void onDateChange(String data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "Service on Destroy");
        closeSocket();
    }
}
