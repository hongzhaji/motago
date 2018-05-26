package com.realm.motago.server;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.aliyun.alink.pal.business.ALinkManager;
import com.aliyun.alink.pal.business.DevInfo;
import com.aliyun.alink.pal.business.LogUtils;
import com.aliyun.alink.pal.business.PALConstant;
import com.realm.motago.HelpUtil;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.element.AliyunResponseData;
import com.realm.motago.element.Msg;

import java.util.List;

/**
 * Created by Skyyao on 2018\5\26 0026.
 */

public class AliyunBackstageServer extends Service
{

    private static final int SERVER_ONLINE = 201;
    private ALiyunBinder binder;
    private IGaoShiQingimp imp;
    private Handler delHandler;
    public boolean mIsSensorStart = false;
    @Override
    public void onCreate()
    {
        super.onCreate();
        binder = new ALiyunBinder();
        delHandler = new Handler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        startALinkServer();
        return binder;
    }

    private void startALinkServer()
    {
        changeServer(SERVER_ONLINE);
        DevInfo devInfo = new DevInfo();
        devInfo.devName = "E3";
        devInfo.category = "ENTERTAINMENT";
        devInfo.devType = "ATALK";
        devInfo.model = "HUIJU_ENTERTAINMENT_ATALK_E3";
        devInfo.sn = HelpUtil.getAndroidOsSN();
        devInfo.mac = HelpUtil.getMacAddress();
        devInfo.alinkKey = "xNPBDj1BtYtuD5zRZhFe";
        devInfo.alinkSecret = "mQcwLXFnfCZyvBV3hXLCskwtujLZW6KqwCtDXB7X";
        devInfo.manufacture = "HUIJU";
        ALinkManager.getInstance().startALink(devInfo);


    }


    private void changeServer(final int type)
    {

        switch (type)
        {
            case SERVER_ONLINE:

                LogUtils.openLog(true);
                break;
            default:
                break;
        }
        ALinkManager.getInstance().init(this, new ALinkManager.onPALEventListener()
        {
            @Override
            public void onRecognizeResult(int status, String result)
            {
                if (imp != null)
                {
                    imp.onRecognizeResult(status, result);
                }
            }

            @Override
            public void onAsrStreamingResult(int status, String result)
            {
                if (imp != null)
                {
                    imp.onAsrStreamingResult(status, result);
                }
            }

            @Override
            public void onAlinkStatus(int status)
            {

                if (PALConstant.TYPE_PAL_STATUS_START_ALINK == status)
                {
                    delHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ALinkManager.getInstance().initMRecognizer();
                            if (!mIsSensorStart)
                            {
                                ALinkManager.getInstance().startSensory();
                                mIsSensorStart = true;
                            }
                            Log.i("tyty", "小智 已经启动 ");
                        }
                    }, 1000);
                }
                if (imp != null)
                {
                    imp.onAlinkStatus(status);
                }
            }

            @Override
            public void onRecEvent(int status, int extra)
            {
                if (imp != null)
                {
                    imp.onRecEvent(status, extra);
                }
            }

            @Override
            public void onMediaEvent(int status, int extra)
            {
                if (imp != null)
                {
                    imp.onMediaEvent(status, extra);
                }
            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                if (imp != null)
                {
                    imp.onMusicInfo(resCode, info);
                }
            }

            @Override
            public void onALinkTTSInfo(String s)
            {
                if (imp != null)
                {
                    imp.onALinkTTSInfo(s);
                }
            }

            @Override
            public void onServerCallback(String s)
            {
                if (imp != null)
                {
                    imp.onServerCallback(s);
                }
            }

        });


    }

    @Override
    public void unbindService(ServiceConnection conn)
    {
        super.unbindService(conn);
    }

    public void setImp(IGaoShiQingimp imp)
    {
        this.imp = imp;
    }

    public class ALiyunBinder extends Binder
    {
        public AliyunBackstageServer getAliyunBackstageServer()
        {
            return AliyunBackstageServer.this;
        }
    }

    public interface IGaoShiQingimp
    {
        public void onRecognizeResult(int status, String result);

        public void onAsrStreamingResult(int status, String result);

        public void onAlinkStatus(int status);

        public void onRecEvent(int status, int extra);

        public void onMediaEvent(int status, int extra);

        public void onMusicInfo(int resCode, String info);

        public void onALinkTTSInfo(String s);

        public void onServerCallback(String s);

    }

}
