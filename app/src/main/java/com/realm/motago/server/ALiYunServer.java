package com.realm.motago.server;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.aliyun.alink.pal.business.ALinkManager;
import com.aliyun.alink.pal.business.DevInfo;
import com.aliyun.alink.pal.business.LogUtils;
import com.aliyun.alink.pal.business.PALConstant;

/**
 * Created by Skyyao on 2018\5\8 0008.
 */

public class ALiYunServer
{
    public static final String MODEL = "ALINKTEST_ENTERTAINMENT_ATALK_RTOS_TEST";


    private Context mContext;

    public ALiYunServer(Context context)
    {
        this.mContext = context;

    }

    private static final int SERVER_ONLINE = 201;

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
        ALinkManager.getInstance().init(mContext, new ALinkManager.onPALEventListener()
        {


            @Override
            public void onRecognizeResult(int status, String result)
            {
                Log.d("guoguo", "status : " + status + "  result: " + result);
            }

            @Override
            public void onAsrStreamingResult(int status, String result)
            {
                Log.d("guoguo", "status : " + status + " onAsrStreamingResult :" + result);
            }

            @Override
            public void onAlinkStatus(int status)
            {
                Log.d("guoguo", "onAlinkStatus :" + status);

                if (PALConstant.TYPE_PAL_STATUS_START_ALINK == status)
                {
//                    mMsgText.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ALinkManager.getInstance().initMRecognizer();
//                            if (!mIsSensorStart) {
//                                mIsSensorStart = true;
//                                ALinkManager.getInstance().startSensory();
//                            }
//                            mMsgText.setText("远场拾音 已启动");
//                        }
//                    }, 1000);
                }

                if (PALConstant.TYPE_PAL_STATUS_START_END == status)
                {
//                    mMsgText.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mMsgText.setText("ALink 已启动");
//                        }
//                    });
                }
            }

            @Override
            public void onRecEvent(int status, int extra)
            {
                Log.d("guoguo", "onRecEvent :" + status + " extra: " + extra);
            }

            @Override
            public void onMediaEvent(int status, int extra)
            {
//				Log.d("guoguo","onMediaEvent :"  + status +" extra: "+extra);
            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                Log.d("guoguo", "onMusicInfo :" + resCode + " info: " + info);
            }

            @Override
            public void onALinkTTSInfo(String s)
            {
                Log.d("guoguo", "onALinkTTSInfo :" + s);
            }

            @Override
            public void onServerCallback(String s)
            {

            }
        });
    }



    private void  startALinkServer()
    {
        changeServer(SERVER_ONLINE);

        DevInfo devInfo = new DevInfo();
        devInfo.devName = "ALINKTEST";
        devInfo.category = "ENTERTAINMENT";
        devInfo.devType = "ATALK";
        devInfo.model = MODEL;
        devInfo.sn = "11:12:F2:F9:F9:F8";
        devInfo.mac = "11:12:F2:F9:F9:F8";
        devInfo.alinkKey = "BjwvCnPIT6M8BAoCctGH";
        devInfo.alinkSecret = "aZcAxVPG1AdKyozHL3ZSQow2Pe2u1Xmm2Jctgot8";
        devInfo.manufacture = "ALINKTEST";
        ALinkManager.getInstance().startALink(devInfo);
    }

    private  void  stopALinkServer()
    {
        ALinkManager.getInstance().stopALink();
    }

    private  void  startAlinkRec()
    {
        ALinkManager.getInstance().startRec();
    }

    private  void  stopAlinkRec()
    {
        ALinkManager.getInstance().stopRec();

    }








    private boolean needRequestPermissions()
    {

        int recordPermission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO);
        if (recordPermission != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, android.Manifest.permission.RECORD_AUDIO))
            {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WRITE_SETTINGS}, 200);
                return true;
            }
        }
        return false;
    }


}


