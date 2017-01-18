package com.example.hou.mobile_control.udpbroadcast;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by hou on 2017/1/12.
 */

public class MyUDPClient {
    private final String BROADCAST_IP = "224.0.0.1";
    private final int BROADCAST_PORT = 8681;
    private byte[] getData = new byte[1024];
    private boolean isStop = false;
    private MulticastSocket mSocket = null;
    private InetAddress address = null;
    private DatagramPacket dataPacket;
    private Thread mUDPThread = null;
    private UDPDataCallBack mCallBack = null;

    /**
     * 开始接收广播
     *
     * @param ip
     */
    public void startUDP(UDPDataCallBack mCallBack) {
        this.mCallBack = mCallBack;
        mUDPThread = new Thread(UDPRunning);
        Log.e("main","开启接收");
        mUDPThread.start();
    }

    /**
     * 重新启动，当接收到udp后会停掉广播，再次需要时使用reStartUDp()启动
     *
     * @param ip
     */
    public void reStartUDP() {
        Log.d("tag", "UDP is reStart!");
        mUDPThread = null;
        isStop = false;
        mUDPThread = new Thread(UDPRunning);
        mUDPThread.start();
    }

    /**
     * 停止广播
     */
    public void stopUDP() {
        isStop = true;
        mUDPThread.interrupt();
    }

    /**
     * 创建udp数据
     */
    private void CreateUDP() {
        try {
            mSocket = new MulticastSocket(BROADCAST_PORT);
            mSocket.setTimeToLive(1);// 广播生存时间0-255
            address = InetAddress.getByName(BROADCAST_IP);
            mSocket.joinGroup(address);
            dataPacket = new DatagramPacket(getData, getData.length, address,
                    BROADCAST_PORT);
            Log.e("main", "udp is create");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable UDPRunning = new Runnable() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;
                mCallBack.mCallback(result);
                Log.e("main", "handler get data:" + result);
            }
        };

        @Override
        public void run() {
            CreateUDP();
            Message msg = handler.obtainMessage();
            while (!isStop) {
                Log.e("main",""+isStop);
                if (mSocket != null) {
                    Log.e("main","mSocket不为空");
                    try {
                        Log.e("main","====");
                        mSocket.receive(dataPacket);
                        Log.e("main","----");
                        String mUDPData = new String(getData, 0,
                                dataPacket.getLength());
                        /**
                         * 确定是否是这个客户端发过来的数据
                         */
                        if (mUDPData != null
                                && "xxxx".equals(mUDPData
                                .split("-")[0])) {
                            msg.obj = mUDPData;
                            handler.sendMessage(msg);
                            Log.e("main",mUDPData+"");
                            isStop = true;
                        }
                    } catch (IOException e) {
                        Log.e("main","IOException");
                        e.printStackTrace();

                    }
                } else {
                    msg.obj = "error";
                    Log.e("main","error");
                    handler.sendMessage(msg);
                }
            }
        }
    };

    public interface UDPDataCallBack {
        public void mCallback(String str);
    }
}
