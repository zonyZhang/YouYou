package com.youyou.socketserver.tcp;

import com.youyou.socketserver.domain.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServiceTcp {
    private int port;

    public ServiceTcp() {

    }

    public ServiceTcp(int port) {
        this.port = port;
    }

    private List<User> users = new CopyOnWriteArrayList<>();// 用户列表

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("服务启动成功~");
                Socket socket = serverSocket.accept();
                Client client = new Client(socket, users);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.out.println("启动失败!!!");
            e.printStackTrace();
        }
    }
}
