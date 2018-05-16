package com.realm.motago.server;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.alink.ALinkBusinessEx;
import com.aliyun.alink.device.AlinkDevice;
import com.aliyun.alink.pal.business.*;
import com.aliyun.alink.sdk.net.anet.api.AError;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryRequest;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryResponse;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.element.AliyunResponseData;
import com.realm.motago.element.Msg;
import com.realm.motago.element.TestJson;
import com.realm.motago.manager.SupperFragmentManager;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;

/**
 * Created by Skyyao on 2018\5\8 0008.
 */

public class ALiYunServer
{

    private static final String TAG = "ALiYunServer";

    public static final String MODEL = "ALINKTEST_ENTERTAINMENT_ATALK_RTOS_TEST";

    private String mUUID = "EF6E1FECDBABBA2525DE5A93AED32173";

    private AliyunState mCurrentState = AliyunState.close;

    private Context mContext;

    private SupperFragmentManager mainManager;

    private boolean isAliyunMusicMediaPlaying;
    private  long musicTotalTime;


    //music player state
    //播放器准备就绪
    public static final int TYPE_PAL_STATUS_MUSIC_PREPARE = 101;
    //播放器播放完毕
    public static final int TYPE_PAL_STATUS_MUSIC_COMPLETE = 102;
    //播放器出错
    public static final int TYPE_PAL_STATUS_MUSIC_ERROR = 103;
    //上报音乐总时长
    public static final int TYPE_PAL_STATUS_MUSIC_DURATION = 104;
    //上报音乐当前播放时间
    public static final int TYPE_PAL_STATUS_MUSIC_POSITION = 105;


    public ALiYunServer(Context context, SupperFragmentManager mainManager)
    {
        this.mContext = context;
        this.mainManager = mainManager;
        isAliyunMusicMediaPlaying = false;
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
                if (status == 200)
                {
                    Log.d(TAG, "json start");


                    try
                    {
                        AliyunResponseData data = JSON.parseObject(result, AliyunResponseData.class);

                        if (data != null)
                        {
                            Log.i(TAG, "parse value = " + data.toString());

                            // ask msg
                            mainManager.addMessage(data.getAskRet(), Msg.TYPE_SEND);

                            // receive msg
                            List<AliyunResponseData.AliyunServiceData.AliyunTTsData> tTsData = data.getData().getTtsUrl();
                            if (tTsData.size() > 0)
                            {
                                String ttsText = tTsData.get(0).getTts_text();
                                if (ttsText != null && !ttsText.trim().equals("null"))
                                {
                                    mainManager.addMessage(ttsText, Msg.TYPE_RECEIVE);
                                }
                            }


                        } else
                        {
                            Log.d(TAG, "parse value err");
                        }
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }


                }

            }

            @Override
            public void onAsrStreamingResult(int status, String result)
            {
                Log.i(TAG, "status : " + status + " onAsrStreamingResult :" + result);
            }

            @Override
            public void onAlinkStatus(int status)
            {
                Log.i(TAG, "onAlinkStatus :" + status);


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
                Log.i(TAG, "onRecEvent :" + status + " extra: " + extra);
                if (status == PALConstant.TYPE_REC_NOTHING)
                {
                    Log.d(TAG, " not recognize");
                    mainManager.addMessage("声音太小，听不清楚", Msg.TYPE_RECEIVE);
                    mainManager.switchAliyunState(false);
                    stopAlinkRec();
                }
            }

            @Override
            public void onMediaEvent(int status, int extra)
            {
                //extra  = time
                //state play = 105?
                Log.i(TAG, "onMediaEvent :" + status + " extra: " + extra);
                if (status == 105)
                {
                    double per = extra*100/musicTotalTime;
                    Log.i("tyty","per = "+per);
                    mainManager.setMusicCurrentTIme(extra,(int)per);
                } else if (status == TYPE_PAL_STATUS_MUSIC_DURATION)
                {
                    isAliyunMusicMediaPlaying = true;
                    musicTotalTime = extra;
                    //here may set total long
                } else if (status == TYPE_PAL_STATUS_MUSIC_ERROR || status == TYPE_PAL_STATUS_MUSIC_COMPLETE)
                {
                    isAliyunMusicMediaPlaying = false;
                }


            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                Log.i(TAG, "onMusicInfo :" + resCode + " info: " + info);
                if (resCode == 1000)
                {
                    try
                    {
                        AliyunMusicInfo musicInfo = JSON.parseObject(info, AliyunMusicInfo.class);
                        if (musicInfo != null)
                        {
                            Log.i(TAG, musicInfo.toString());
                            String musicName = musicInfo.getName();
                            mainManager.addMessage("正在播放歌曲：" + musicName, Msg.TYPE_RECEIVE);
                            mainManager.setMusicInfo(musicInfo);
                        }
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }

                }

            }

            @Override
            public void onALinkTTSInfo(String s)
            {
                Log.i(TAG, "onALinkTTSInfo :" + s);
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
        Log.i("tyty", "init aliyun server");
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
        Log.d(TAG, "mCurrentState " + mCurrentState);
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
        isAliyunMusicMediaPlaying = true;
    }

    public void pauseMusic()
    {
        ALinkManager.getInstance().pauseMusic();
        isAliyunMusicMediaPlaying = false;
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
        Log.i(TAG,"switchPlaymode");
    }


    public boolean isAliyunMusicMediaPlaying()
    {
        return isAliyunMusicMediaPlaying;
    }


    //music

    public void loveMusic( String itemId)
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.itemtofav.add");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("auid", "1");
        transitoryRequest.putParam("uuid", mUUID);
        transitoryRequest.putParam("itemId", itemId);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "loveMusic onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {
                    Log.i(TAG, "loveMusic " + transitoryResponse.data.toString());
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "loveMusic onFailed");
            }
        });
    }


    public void getChannelDetailList(String uuid, String from, String size, String direct,
                                      String channelId, String channelType, String collectionID) {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.channeldetaillist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("from", from);
        transitoryRequest.putParam("size", size);
        transitoryRequest.putParam("direct", direct);
        transitoryRequest.putParam("channelId", channelId);
        transitoryRequest.putParam("channelType", channelType);
        transitoryRequest.putParam("collectionId", collectionID);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener() {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse) {
                Log.i(TAG, "getChannelList onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null) {
                    Log.i(TAG, "getChannelList data: "+ transitoryResponse.data.toString());
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError) {
                Log.i(TAG, "getChannelList onFailed");
            }
        });
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


