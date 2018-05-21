package com.realm.motago.server;


import android.content.Context;

import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.openaccount.Environment;
import com.aliyun.alink.AlinkSDK;

import com.aliyun.alink.business.account.OALoginBusiness;

import com.aliyun.alink.business.alink.ALinkBusinessEx;

import com.aliyun.alink.business.downstream.DeviceBusiness;

import com.aliyun.alink.business.login.IAlinkLoginCallback;

import com.aliyun.alink.linksdk.tools.ALog;
import com.aliyun.alink.pal.business.*;
import com.aliyun.alink.sdk.net.anet.api.AError;
import com.aliyun.alink.sdk.net.anet.api.persistentnet.IOnPushListener;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryRequest;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryResponse;
import com.realm.motago.HelpUtil;
import com.realm.motago.element.*;
import com.realm.motago.manager.SupperFragmentManager;
import com.wsf.squareup.okhttp.internal.http.HttpEngine;

import javax.crypto.spec.OAEPParameterSpec;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Skyyao on 2018\5\8 0008.
 * http://smart.aliyun.com/business/app/download_andorid_pic.htm?spm=0.0.0.0.KqgOIy  阿里云远程登录
 *
 */

public class ALiYunServer
{

    private static final String TAG = "ALiYunServer";

    public static final String MODEL = "HUIJU_ENTERTAINMENT_ATALK_E3";

    private String mUUID = "EF6E1FECDBABBA2525DE5A93AED32173";

    private AliyunState mCurrentState = AliyunState.close;

    private Context mContext;

    private SupperFragmentManager mainManager;

    private boolean isAliyunMusicMediaPlaying;
    private long musicTotalTime;


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
        initPushTunnel();
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
                if (status == TYPE_PAL_STATUS_MUSIC_POSITION)
                {
                    double per = extra * 100 / musicTotalTime;
                    Log.i("tyty", "per = " + per);
                    mainManager.setMusicCurrentTIme(extra, (int) per);
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

        /* test
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
        */
        DevInfo devInfo = new DevInfo();
        devInfo.devName = "E3";
        devInfo.category = "ENTERTAINMENT";
        devInfo.devType = "ATALK";
        devInfo.model = MODEL;
        devInfo.sn = HelpUtil.getAndroidOsSN();
        devInfo.mac = HelpUtil.getMacAddress();
        devInfo.alinkKey = "xNPBDj1BtYtuD5zRZhFe";
        devInfo.alinkSecret = "mQcwLXFnfCZyvBV3hXLCskwtujLZW6KqwCtDXB7X";
        devInfo.manufacture = "HUIJU";
        ALinkManager.getInstance().startALink(devInfo);

        Log.i(TAG, HelpUtil.getAndroidOsSN()+" ---------"+ HelpUtil.getMacAddress()+"  ----");


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
        Log.i(TAG, "switchPlaymode");
    }


    public boolean isAliyunMusicMediaPlaying()
    {
        return isAliyunMusicMediaPlaying;
    }


    //music

    public void loveMusic(String itemId)
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
                                     String channelId, String channelType, String collectionID)
    {
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
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "getChannelList onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {
                    Log.i(TAG, "getChannelList data: " + transitoryResponse.data.toString());
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "getChannelList onFailed");
            }
        });
    }

    public void getChannelList()
    {

        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.douglaschannellist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", "");

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "getChannelList onSuccess");
                List<MusicChannel> mMusicChannelList = new ArrayList<MusicChannel>();
                try
                {
                    String channelData = (String) transitoryResponse.data;
                    Log.i(TAG, "getChannelList channelData: " + channelData);
                    JSONObject channelDataObject = JSON.parseObject(channelData);
                    JSONArray channelList = channelDataObject.getJSONArray("data");
                    String mRecentChannelId = channelDataObject.getString("recentChannelId");
                    if (channelList != null && channelList.size() > 0)
                    {
                        mMusicChannelList.clear();
                        for (int i = 0; i < channelList.size(); i++)
                        {
                            JSONObject channelItem = (JSONObject) channelList.get(i);

                            MusicChannel musicChannel = new MusicChannel();
                            musicChannel.auid = channelItem.getString("auid");
                            musicChannel.channelId = channelItem.getString("id");
                            musicChannel.channelLogo = channelItem.getString("logo");
                            musicChannel.channelName = channelItem.getString("name");
                            musicChannel.channelType = channelItem.getString("channelType");
                            musicChannel.detailEditable = channelItem.getString("detailEditable");
                            musicChannel.removable = channelItem.getString("removable");
                            musicChannel.seqNum = channelItem.getString("seqNum");
                            musicChannel.supportCache = channelItem.getString("supportCache");
                            mMusicChannelList.add(musicChannel);
                        }
                    }
                } catch (Exception e)
                {

                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "getChannelList onFailed");
            }
        });





    }

    public void bindDevice()
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.app.core.user.binddevice");

        transitoryRequest.putParam("uuid", mUUID);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "bindDevice onSuccess");
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "bindDevice onFailed");
            }
        });
    }

    public void accountLogin()
    {



        OALoginBusiness oaLoginBusiness = new OALoginBusiness();
        oaLoginBusiness.init(mContext, Environment.ONLINE, new IAlinkLoginCallback()
        {
            @Override
            public void onSuccess()
            {
                Log.i("tyty"," -------- success");
            }

            @Override
            public void onFailure(int i, String s)
            {
                Log.i("tyty"," -------- onFailure = "+s);
            }
        });

        AlinkSDK.init(mContext,"24898847",oaLoginBusiness);





    }

    private void initPushTunnel()
    {
        DeviceBusiness deviceBiz = new DeviceBusiness();
        deviceBiz.startWatching(DeviceBusiness.FEATURE_DOWNSTREAM_WATCHER);
        deviceBiz.setDownstreamListener(new IOnPushListener()
        {
            @Override
            public void onCommand(String s)
            {
                Toast.makeText(mContext, "收到下推数据", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean filter(String s)
            {
                return true;
            }
        }, true);
    }




    public enum AliyunState
    {
        normal, searching, close
    }

}


