package com.realm.motago;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.alibaba.sdk.android.openaccount.Environment;
import com.aliyun.alink.AlinkSDK;
import com.aliyun.alink.business.account.OALoginBusiness;
import com.aliyun.alink.business.login.IAlinkLoginCallback;
import com.realm.motago.server.ALiYunServer;
import com.realm.motago.server.AliyunBackstageServer;

/**
 * Created by Skyyao on 2018\5\21 0021.
 */

public class MyApplication extends MultiDexApplication
{

    private AliyunBackstageServer aLiYunServer;
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.e("tyty", "service bind name = " + name);
            AliyunBackstageServer.ALiyunBinder binder = (AliyunBackstageServer.ALiyunBinder) service;
            aLiYunServer = (AliyunBackstageServer) binder.getAliyunBackstageServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.e("tyty", "service bind err name = " + name);

        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();


        OALoginBusiness oaLoginBusiness = new OALoginBusiness();
        oaLoginBusiness.init(this, Environment.ONLINE, new IAlinkLoginCallback()
        {
            @Override
            public void onSuccess()
            {
                Log.i("tyty", " -------- success");
            }

            @Override
            public void onFailure(int i, String s)
            {
                Log.i("tyty", " -------- onFailure = " + s);
            }
        });

        AlinkSDK.init(this, "24898847", oaLoginBusiness);


    }

    public void bindService()
    {
        Log.i("tyty","getServiceConnection");
        Intent intent1 = new Intent(this, AliyunBackstageServer.class);
        bindService(intent1, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public  void  unBindeService()
    {
        unbindService(serviceConnection);
    }

    public AliyunBackstageServer getaLiYunServer()
    {
        return aLiYunServer;
    }
}
