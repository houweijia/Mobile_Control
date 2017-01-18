package com.example.hou.mobile_control;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hou.mobile_control.client.SocketClientManager;
import com.example.hou.mobile_control.udpbroadcast.MyUDPClient;
import com.example.hou.mobile_control.utils.Info;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private WifiManager.MulticastLock lock;
    private MyUDPClient myUDPClient;
    private String ip = null;
    private int port ;
    private SocketClientManager mClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("test wifi");
        mClientManager = SocketClientManager.getInstance();
        Button btn = (Button) findViewById(R.id.start_udp_btn);
        Button btn_lock = (Button) findViewById(R.id.btn_lock);
        Button btn_go = (Button) findViewById(R.id.btn_go);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        Button btn_left = (Button) findViewById(R.id.btn_left);
        Button btn_right = (Button) findViewById(R.id.btn_right);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);

        btn.setOnClickListener(this);
        btn_lock.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        myUDPClient = new MyUDPClient();
        myUDPClient.startUDP(new MyUDPClient.UDPDataCallBack() {
            @Override
            public void mCallback(String str) {
                Info.SERVER_IP = str.split("-")[1];
                Log.e("main","ip==="+Info.SERVER_IP);
                Info.SERVER_PORT = Integer.parseInt(str.split("-")[2]);
                Log.e("main","port===="+Info.SERVER_PORT);
                if(Info.SERVER_IP != null && !TextUtils.isEmpty(Info.SERVER_PORT+"")){
                    mClientManager.startClientSocket(Info.SERVER_IP,Info.SERVER_PORT);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_udp_btn:

                break;
            case R.id.btn_lock:
                lock.acquire();
                break;
            case R.id.btn_go:
                mClientManager.SendMessage("go");
                break;
            case R.id.btn_back:
                mClientManager.SendMessage("back");
                break;
            case R.id.btn_left:
                mClientManager.SendMessage("left");
                break;
            case R.id.btn_right:
                mClientManager.SendMessage("right");
                break;
            case R.id.btn_stop:
                mClientManager.SendMessage("stop");
                break;
        }
    }
}
