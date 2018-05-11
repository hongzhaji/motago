package com.realm.motago.server;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.aliyun.alink.pal.business.ALinkManager;
import com.aliyun.alink.pal.business.DevInfo;
import com.aliyun.alink.pal.business.LogUtils;
import com.aliyun.alink.pal.business.PALConstant;
import com.realm.motago.element.AliyunResponseData;
import com.realm.motago.element.Msg;
import com.realm.motago.element.TestJson;
import com.realm.motago.manager.SupperFragmentManager;

/**
 * Created by Skyyao on 2018\5\8 0008.
 */

public class ALiYunServer
{

    private static final String TAG = "ALiYunServer";

    public static final String MODEL = "ALINKTEST_ENTERTAINMENT_ATALK_RTOS_TEST";

    private AliyunState mCurrentState = AliyunState.close;

    private Context mContext;

    private SupperFragmentManager mainManager;


    public ALiYunServer(Context context, SupperFragmentManager mainManager)
    {
        this.mContext = context;
        this.mainManager = mainManager;

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
                Log.d(TAG, "status : " + status + "  result: " + result);
                // Recognize
                if(status == 200)
                {
                    Log.d(TAG, "json start");
                    try
                    {
                        AliyunResponseData data = JSON.parseObject(result,AliyunResponseData.class);

                        if(data !=null)
                        {
                            Log.d(TAG, "parse value = "+data.toString());
                        }
                        else
                        {
                            Log.d(TAG, "parse value err");
                        }
                    }catch (Exception e)
                    {
                        Log.e(TAG,e.toString());
                    }


                }

            }

            @Override
            public void onAsrStreamingResult(int status, String result)
            {
                Log.d(TAG, "status : " + status + " onAsrStreamingResult :" + result);
            }

            @Override
            public void onAlinkStatus(int status)
            {
                Log.d(TAG, "onAlinkStatus :" + status);

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
                    mCurrentState = AliyunState.normal;
                }

                if (PALConstant.TYPE_PAL_STATUS_START_END == status)
                {
//                    mMsgText.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mMsgText.setText("ALink 已启动");
//                        }
//                    });

                    mCurrentState = AliyunState.normal;
                }
            }

            @Override
            public void onRecEvent(int status, int extra)
            {
                //if have words , extra big than zero
                Log.d(TAG, "onRecEvent :" + status + " extra: " + extra);
                if(status == PALConstant.TYPE_REC_NOTHING)
                {
                    Log.d(TAG, " not recognize" );
                }
            }

            @Override
            public void onMediaEvent(int status, int extra)
            {
//				Log.d("guoguo","onMediaEvent :"  + status +" extra: "+extra);
            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                Log.d(TAG, "onMusicInfo :" + resCode + " info: " + info);
            }

            @Override
            public void onALinkTTSInfo(String s)
            {
                Log.d(TAG, "onALinkTTSInfo :" + s);
               // mainManager.addMessage(s, Msg.TYPE_SEND);

            }

            @Override
            public void onServerCallback(String s)
            {

            }
        });
    }

    public AliyunState getmCurrentState()
    {
        return mCurrentState;
    }

    public void startALinkServer()
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
        Log.i("tyty","init aliyun server");
    }

    public void stopALinkServer()
    {
        if (mCurrentState == AliyunState.normal)
        {
            ALinkManager.getInstance().stopALink();
        }
        if (mCurrentState == AliyunState.searching)
        {
            stopAlinkRec();
            ALinkManager.getInstance().stopALink();
        }

    }

    public void startAlinkRec()
    {
        Log.d(TAG, "mCurrentState " +mCurrentState);
        if (mCurrentState == AliyunState.close)
        {
            return;
        }
        if (mCurrentState == AliyunState.searching)
        {
            stopAlinkRec();
        }
        ALinkManager.getInstance().startRec();
        mCurrentState = AliyunState.searching;
        Log.d(TAG, "startAlinkRec ");
    }

    public void stopAlinkRec()
    {
        if (mCurrentState == AliyunState.searching)
        {
            ALinkManager.getInstance().stopRec();
            Log.d(TAG, "stopAlinkRec ");
        }


    }

    public void playMusic()
    {
        ALinkManager.getInstance().startMusic();
    }

    public void pauseMusic()
    {
        ALinkManager.getInstance().pauseMusic();
    }

    public void nextMusic()
    {
        ALinkManager.getInstance().nextMusic();

    }

    public void lastMusic()
    {
        ALinkManager.getInstance().preMusic();
    }

    public void switchPlaymode()
    {
        ALinkManager.getInstance().switchPlayMode();
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


    public enum AliyunState
    {
        normal, searching, close
    }

}


