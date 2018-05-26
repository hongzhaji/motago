package com.realm.motago.server;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.aliyun.alink.business.alink.ALinkBusinessEx;

import com.aliyun.alink.device.AlinkDevice;
import com.aliyun.alink.pal.business.*;
import com.aliyun.alink.sdk.net.anet.api.AError;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryRequest;
import com.aliyun.alink.sdk.net.anet.api.transitorynet.TransitoryResponse;
import com.realm.motago.HelpUtil;
import com.realm.motago.MyApplication;
import com.realm.motago.element.*;
import com.realm.motago.manager.SupperFragmentManager;

import java.util.ArrayList;

import java.util.List;


/**
 * Created by Skyyao on 2018\5\8 0008.
 * http://smart.aliyun.com/business/app/download_andorid_pic.htm?spm=0.0.0.0.KqgOIy  阿里云远程登录
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
    private long musicTotalTime = -1;






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

    //暂停事件
    public static final int TYPE_PAL_STATUS_MUSIC_PAUSE = 110;
    //开始事件
    public static final int TYPE_PAL_STATUS_MUSIC_START = 111;


    //开始语音识别
    public static final int TYPE_REC_STATUS_RECOGNIZE_START = 301;
    //结束语音识别
    public static final int TYPE_REC_STATUS_RECOGNIZE_STOP = 302;
    //返回音量大小
    public static final int TYPE_REC_STATUS_VOLUME = 303;


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
                    mCurrentState = AliyunState.normal;
                }

                if (PALConstant.TYPE_PAL_STATUS_START_END == status)
                {
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

                    stopAlinkRec();
                }else if(status == TYPE_REC_STATUS_VOLUME)
                {
                    //0 -100  level [0 1 2 3]
                    int level = 0;
                    if(extra < 25)
                    {
                        level = 0;
                    }else if(extra < 50)
                    {
                        level = 1;
                    }else if (extra < 75)
                    {
                        level = 2;
                    }else
                    {
                        level = 3;
                    }
                    mainManager.changeVioceLevel(level);
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
                    isAliyunMusicMediaPlaying = true;
                } else if (status == TYPE_PAL_STATUS_MUSIC_DURATION)
                {
                    isAliyunMusicMediaPlaying = true;
                    musicTotalTime = extra;
                    //here may set total long
                } else if (status == TYPE_PAL_STATUS_MUSIC_ERROR || status == TYPE_PAL_STATUS_MUSIC_COMPLETE)
                {
                    isAliyunMusicMediaPlaying = false;
                } else if (TYPE_PAL_STATUS_MUSIC_PAUSE == status)
                {
                    isAliyunMusicMediaPlaying = false;

                } else if (TYPE_PAL_STATUS_MUSIC_START == status)
                {
                    isAliyunMusicMediaPlaying = true;
                }


            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                Log.i(TAG, "onMusicInfo :" + resCode + " info: " + info);
                isTopApp();
                if (resCode == 1000)
                {
                    try
                    {
                       AliyunMusicInfo mCurrentAliyunMusicInfo = JSON.parseObject(info, AliyunMusicInfo.class);
                        if (mCurrentAliyunMusicInfo != null)
                        {
                            Log.i(TAG, mCurrentAliyunMusicInfo.toString());
                            String musicName = mCurrentAliyunMusicInfo.getName();
                            mainManager.addMessage("正在播放歌曲：" + musicName, Msg.TYPE_RECEIVE);
                            mainManager.setMusicInfo(mCurrentAliyunMusicInfo);
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

        Log.i(TAG, HelpUtil.getAndroidOsSN() + " ---------" + HelpUtil.getMacAddress() + "  ----");


    }

    public  void startALinkServerNew()
    {

        mCurrentState = AliyunState.normal;
        AliyunBackstageServer server =  ((MyApplication)mContext.getApplicationContext()).getaLiYunServer();
        if(server == null )
        {
            Log.e("tyty","aliyun service not install err!");
            Toast.makeText(mContext,"阿里服务没有开启，请重启设备！",Toast.LENGTH_LONG).show();
            ((Activity)mContext).finish();
            return;
        }
        server .setImp(
        new AliyunBackstageServer.IGaoShiQingimp()
        {


            @Override
            public void onRecognizeResult(int status, String result)
            {
                Log.d(TAG, "status : " + status + "  result: " + result);
                // Recognize
                if (status == 200)
                {
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
                    mCurrentState = AliyunState.normal;
                }

                if (PALConstant.TYPE_PAL_STATUS_START_END == status)
                {
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

                    stopAlinkRec();
                }else if(status == TYPE_REC_STATUS_VOLUME)
                {
                    //0 -100  level [0 1 2 3]
                    int level = 0;
                    if(extra < 25)
                    {
                        level = 0;
                    }else if(extra < 50)
                    {
                        level = 1;
                    }else if (extra < 75)
                    {
                        level = 2;
                    }else
                    {
                        level = 3;
                    }
                    mainManager.changeVioceLevel(level);
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
                    isAliyunMusicMediaPlaying = true;
                } else if (status == TYPE_PAL_STATUS_MUSIC_DURATION)
                {
                    isAliyunMusicMediaPlaying = true;
                    musicTotalTime = extra;
                    //here may set total long
                } else if (status == TYPE_PAL_STATUS_MUSIC_ERROR || status == TYPE_PAL_STATUS_MUSIC_COMPLETE)
                {
                    isAliyunMusicMediaPlaying = false;
                } else if (TYPE_PAL_STATUS_MUSIC_PAUSE == status)
                {
                    isAliyunMusicMediaPlaying = false;

                } else if (TYPE_PAL_STATUS_MUSIC_START == status)
                {
                    isAliyunMusicMediaPlaying = true;
                }


            }

            @Override
            public void onMusicInfo(int resCode, String info)
            {
                Log.i(TAG, "onMusicInfo :" + resCode + " info: " + info);
                isTopApp();
                if (resCode == 1000)
                {
                    try
                    {
                        AliyunMusicInfo mCurrentAliyunMusicInfo = JSON.parseObject(info, AliyunMusicInfo.class);
                        if (mCurrentAliyunMusicInfo != null)
                        {
                            Log.i(TAG, mCurrentAliyunMusicInfo.toString());
                            String musicName = mCurrentAliyunMusicInfo.getName();
                            mainManager.addMessage("正在播放歌曲：" + musicName, Msg.TYPE_RECEIVE);
                            mainManager.setMusicInfo(mCurrentAliyunMusicInfo);
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

    public void startSensory()
    {
        ALinkManager.getInstance().startSensory();
    }

    public void stopSensory()
    {
        ALinkManager.getInstance().stopSensory();
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
        transitoryRequest.putParam("uuid", AlinkDevice.getInstance().getDeviceUUID());
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

    public long getMusicTotalTime()
    {
        return musicTotalTime;
    }


    public void quickPlay(String uuid, String type, String itemId, String collectionId)
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.quick.play");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("auid", "1");
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("auditionType", type);
        transitoryRequest.putParam("itemId", itemId);
        transitoryRequest.putParam("collectionId", collectionId);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "quickPlay onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {
                    Log.i(TAG, "quickPlay " + transitoryResponse.data.toString());
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "quickPlay onFailed");
            }
        });
    }


    public void cancelFavorite(String uuid, String itemId, String channelId)
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.itemfromfav.remove");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("auid", "1");
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("itemId", itemId);
        transitoryRequest.putParam("channelId", channelId);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "cancelFavorite onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {
                    Log.i(TAG, "cancelFavorite " + transitoryResponse.data.toString());
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "cancelFavorite onFailed");
            }
        });


    }

    public void getPlayList(String auid, String uuid, String from, String size, String direct, String channelId, String collectionID, String itemId)
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.playlist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("from", from);
        transitoryRequest.putParam("size", size);
        transitoryRequest.putParam("direct", direct);
        transitoryRequest.putParam("channelId", channelId);
        //itemId
        transitoryRequest.putParam("itemId", itemId);
        transitoryRequest.putParam("auid", auid);
        transitoryRequest.putParam("collectionId", collectionID);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "getplaylist onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {

                    Log.i(TAG, "getplaylist " + transitoryResponse.data.toString());

                    List<AliyunMusicInfo> musicList = JSON.parseArray(transitoryResponse.data.toString(), AliyunMusicInfo.class);

                    Log.i(TAG, "size = " + musicList.size());
                    //get data
                    mainManager.setListAdepterAndAliyunMusicIfos(musicList);
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "getplaylist onFailed");
            }
        });
    }


    public void getChannelList(String uuid) {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.douglaschannellist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", uuid);

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener() {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse) {
                Log.i(TAG, "getChannelList onSuccess");
                try {
                    String channelData = (String)transitoryResponse.data;
                    Log.i(TAG, "getChannelList channelData: "+channelData);
                    JSONObject channelDataObject = JSON.parseObject(channelData);
                    JSONArray channelList = channelDataObject.getJSONArray("data");
                   String mRecentChannelId = channelDataObject.getString("recentChannelId");

                    if (channelList != null && channelList.size() > 0) {

                        for (int i = 0; i < channelList.size(); i++) {
                            JSONObject channelItem = (JSONObject) channelList.get(i);

                            MusicChannel musicChannel = new MusicChannel();
                            musicChannel.auid = channelItem.getString("auid");
                            musicChannel.channelId = channelItem.getString("id");
                            musicChannel.channelLogo = channelItem.getString("logo");
                            musicChannel.channelName = channelItem.getString("name");
                            musicChannel.channelType= channelItem.getString("channelType");
                            musicChannel.detailEditable = channelItem.getString("detailEditable");
                            musicChannel.removable = channelItem.getString("removable");
                            musicChannel.seqNum = channelItem.getString("seqNum");
                            musicChannel.supportCache = channelItem.getString("supportCache");
                            Log.i("tyty",musicChannel.toString());
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError) {
                Log.i(TAG, "getChannelList onFailed");
            }
        });
    }

    public void getMusicCollectionDetailList(String uuid, String from, String size, String direct,
                                              String channelId, String collectionID)
    {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.channeldetaillist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("from", from);
        transitoryRequest.putParam("size", size);
        transitoryRequest.putParam("direct", direct);
        transitoryRequest.putParam("channelId", "629328");
        //音乐收藏
        transitoryRequest.putParam("channelType", ""+3);
        Log.i("tyty"," id = "+collectionID);
        transitoryRequest.putParam("collectionId","0" );

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener()
        {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse)
            {
                Log.i(TAG, "getChannelList ----------------   onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null)
                {
                    Log.i(TAG, "getChannelList data: " + transitoryResponse.data.toString());
                    JSONObject jsonObject = JSON.parseObject(transitoryResponse.data.toString());

                    String datas = jsonObject.getString("datas");
                    List<AliyunMusicInfo> musicList = JSON.parseArray(datas, AliyunMusicInfo.class);
                    Log.i(TAG, "size = " + musicList.size());
                    mainManager.setLovedAdepterAndAliyunMusicIfos(musicList);
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError)
            {
                Log.i(TAG, "getChannelList onFailed");
            }
        });
    }

    //may be is here
    public void getPlayDetailList(String uuid, String from, String size,
                                              String direct, String collectionId) {
        TransitoryRequest transitoryRequest = new TransitoryRequest();
        transitoryRequest.setMethod("mtop.openalink.pal.collectiondetaillist.get");
        transitoryRequest.needToken = false;
        transitoryRequest.putParam("uuid", uuid);
        transitoryRequest.putParam("from", from);
        transitoryRequest.putParam("size", size);
        transitoryRequest.putParam("direct", direct);
        transitoryRequest.putParam("collectionId", collectionId);
        transitoryRequest.putParam("version", "1.0");

        ALinkBusinessEx biz = new ALinkBusinessEx();
        biz.request(transitoryRequest, new ALinkBusinessEx.IListener() {
            @Override
            public void onSuccess(TransitoryRequest transitoryRequest, TransitoryResponse transitoryResponse) {
                Log.i(TAG, "getCollectionByTag ------------------ onSuccess");
                if (transitoryResponse != null && transitoryResponse.data != null) {
                    Log.i(TAG, "getCollectionByTag data: "+ transitoryResponse.data.toString());

                    JSONObject jsonObject = JSON.parseObject(transitoryResponse.data.toString());
                    String datas = jsonObject.getString("datas");
                    List<AliyunMusicInfo> musicList = JSON.parseArray(datas, AliyunMusicInfo.class);
                    Log.i(TAG, "size = " + musicList.size());
                    mainManager.setListAdepterAndAliyunMusicIfos(musicList);
                }
            }

            @Override
            public void onFailed(TransitoryRequest transitoryRequest, AError aError) {
                Log.i(TAG, "getCollectionByTag onFailed");
            }
        });
    }

    public   boolean isTopApp()
    {
        ActivityManager activityManager =(ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
        Log.i("tyty","pacckgename = "+cn.getPackageName());
        if(cn.getPackageName().equals(""))
        {
            return true;
        }
        return  false;

    }

    public enum AliyunState
    {
        normal, searching, close
    }

}


