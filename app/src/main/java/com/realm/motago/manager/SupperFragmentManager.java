package com.realm.motago.manager;

import android.content.Context;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;

import com.aliyun.alink.device.AlinkDevice;
import com.realm.motago.*;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.element.Msg;
import com.realm.motago.server.ALiYunServer;

import java.util.List;
import java.util.logging.Handler;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class SupperFragmentManager implements IXiaoZhiClick, MotaMusicFragment.IAliyunMusicHelp, MotaPlayListFragment.IMusicPlayHelp
{
    public static final int MOTA_FRAGMENT_MAIN = 0;
    public static final int MOTA_FRAGMENT_MUSIC = 1;
    public static final int MOTA_FRAGMENT_XIAOZHI = 2;
    public static final int MOTA_FRAGMENT_LIST = 3;


    private Context mContext;
    private FragmentManager motaManager;
    private ViewGroup mRootView;

    private Fragment[] kindFragment;


    // User interaction
    private Button mVoiceInput;
    private Button getmVoiceCancel;
    private ViewGroup mBottomView;
    private int mCurrentFragmentIndex;
    private ALiYunServer mainServer;

    private IMesHelper mesHelper;

    private android.os.Handler mUIHandler = new android.os.Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            if (msg.arg1 == MOTA_FRAGMENT_MUSIC)
            {
                //set current time for music .
                if (msg.what == 2)
                {
                    int time = msg.arg2;
                    ((MotaMusicFragment) kindFragment[MOTA_FRAGMENT_MUSIC]).setMusicCurrentTime("" + time, (int) msg.obj);
                    return;
                }else if(msg.what == 3)
                {

                    ((MotaPlayListFragment) kindFragment[MOTA_FRAGMENT_LIST]).setMusiclistAdapter((List<AliyunMusicInfo>)msg.obj);
                    ((MotaPlayListFragment) kindFragment[MOTA_FRAGMENT_LIST]).setHelp(SupperFragmentManager.this);
                    translateToMotagoListFragment();

                    return;
                }



                //set music info and show music fragment
                ((MotaMusicFragment) kindFragment[MOTA_FRAGMENT_MUSIC]).setMusicInfo((AliyunMusicInfo) msg.obj);
                if (mCurrentFragmentIndex != MOTA_FRAGMENT_MUSIC)
                {
                    translateToMotagoMusicFragment();
                }
            }
            if (msg.what == 1)
            {
                switchAliyunState(false);
            }
        }
    };


    public SupperFragmentManager(Context context, FragmentManager fragmentManager, ViewGroup v)
    {
        this.mContext = context;
        this.motaManager = fragmentManager;
        this.mRootView = v;
        mainServer = new ALiYunServer(mContext, this);

        initFragment();
        initEventCallback();

        motaManager.beginTransaction().show(kindFragment[MOTA_FRAGMENT_MAIN]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;

        switchAliyunState(false);
        mainServer.startALinkServer();

    }


    @Override
    public void onXiaoZhiClick()
    {
        FragmentTransaction transaction = motaManager.beginTransaction();
        transaction.hide(kindFragment[mCurrentFragmentIndex]);
        transaction.show(kindFragment[MOTA_FRAGMENT_XIAOZHI]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_XIAOZHI;
    }

    public void setMusicInfo(AliyunMusicInfo musicInfo)
    {
        Message msg = mUIHandler.obtainMessage();
        msg.arg1 = MOTA_FRAGMENT_MUSIC;
        msg.obj = musicInfo;
        msg.sendToTarget();

    }

    //need translate
    public void  setListAdepterAndAliyunMusicIfos(List<AliyunMusicInfo> infos)
    {
        Message msg = mUIHandler.obtainMessage();
        msg.arg1 = MOTA_FRAGMENT_MUSIC;
        msg.obj = infos;
        msg.what = 3;
        msg.sendToTarget();
    }
    public  void  setLovedAdepterAndAliyunMusicIfos(List<AliyunMusicInfo> infos)
    {
        ((MotaPlayListFragment) kindFragment[MOTA_FRAGMENT_LIST]).setMusiclistAdapter(infos);
    }


    public void setMusicCurrentTIme(int time, int percent)
    {
        Message msg = mUIHandler.obtainMessage();
        msg.arg1 = MOTA_FRAGMENT_MUSIC;
        msg.arg2 = time;
        msg.what = 2;
        msg.obj = percent;
        msg.sendToTarget();
    }


    public boolean backToLastFragment()
    {
        if (mCurrentFragmentIndex == MOTA_FRAGMENT_XIAOZHI && ((MotaXiaoZhiFragment) kindFragment[MOTA_FRAGMENT_XIAOZHI]).onNavigationCLick())
        {
            FragmentTransaction transaction = motaManager.beginTransaction();
            transaction.hide(kindFragment[MOTA_FRAGMENT_XIAOZHI]);
            transaction.show(kindFragment[MOTA_FRAGMENT_MAIN]).commit();
            mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;
            return true;
        } else if (mCurrentFragmentIndex == MOTA_FRAGMENT_MUSIC && ((MotaMusicFragment) kindFragment[MOTA_FRAGMENT_MUSIC]).onNavigationCLick())
        {
            FragmentTransaction transaction = motaManager.beginTransaction();
            transaction.hide(kindFragment[MOTA_FRAGMENT_MUSIC]);
            transaction.show(kindFragment[MOTA_FRAGMENT_MAIN]).commit();
            mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;
            if (mainServer.isAliyunMusicMediaPlaying())
            {
                mainServer.pauseMusic();
            }
            return true;


        }

        return false;
    }

    public void addMessage(String msg, int type)
    {
        mesHelper.addMessage(msg, type);
        //if receive ,may stop rec.
        if (type == Msg.TYPE_RECEIVE)
        {
            Message message = mUIHandler.obtainMessage();
            message.what = 1;
            message.sendToTarget();

        }
    }

    public void switchAliyunState(boolean recording)
    {
        if (recording)
        {
            mBottomView.setVisibility(View.VISIBLE);
            mVoiceInput.setVisibility(View.GONE);
        } else
        {
            mBottomView.setVisibility(View.GONE);
            mVoiceInput.setVisibility(View.VISIBLE);
        }
    }



    public void finish()
    {
        mainServer.stopALinkServer();
        if(mainServer.mIsSensorStart)
        {
            mainServer.stopSensory();
        }

    }

    private void translateToMotagoMusicFragment()
    {
        FragmentTransaction transaction = motaManager.beginTransaction();
        transaction.hide(kindFragment[mCurrentFragmentIndex]);
        transaction.show(kindFragment[MOTA_FRAGMENT_MUSIC]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_MUSIC;
        switchAliyunState(false);
    }

    private  void  translateToMotagoListFragment()
    {
        FragmentTransaction transaction = motaManager.beginTransaction();
        transaction.hide(kindFragment[mCurrentFragmentIndex]);
        transaction.show(kindFragment[MOTA_FRAGMENT_LIST]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_LIST;

    }

    private void initFragment()
    {
        kindFragment = new Fragment[4];
        MotaMainActivityFragment mainFragment = new MotaMainActivityFragment();
        kindFragment[MOTA_FRAGMENT_MAIN] = mainFragment;
        mesHelper = mainFragment;
        MotaMusicFragment musicFragment = new MotaMusicFragment();
        musicFragment.setMusicHelp(this);
        kindFragment[MOTA_FRAGMENT_MUSIC] = musicFragment;
        MotaXiaoZhiFragment xiaoZhiFragment = new MotaXiaoZhiFragment();
        kindFragment[MOTA_FRAGMENT_XIAOZHI] = xiaoZhiFragment;
        MotaPlayListFragment listFragment = new MotaPlayListFragment();
        kindFragment[MOTA_FRAGMENT_LIST] = listFragment;
        for (Fragment fragment :
                kindFragment)
        {
            motaManager.beginTransaction().add(R.id.fragment_main, fragment).hide(fragment).commit();
        }

    }

    private void initEventCallback()
    {
        mVoiceInput = mRootView.findViewById(R.id.voice_input);
        mVoiceInput.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchAliyunState(true);
                mainServer.startAlinkRec();
            }
        });
        getmVoiceCancel = mRootView.findViewById(R.id.cancel_record);
        getmVoiceCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchAliyunState(false);
                mainServer.stopAlinkRec();
            }
        });
        mBottomView = mRootView.findViewById(R.id.bottom_recording);
    }

    @Override
    public void aliyunPlayMusic()
    {
        mainServer.playMusic();
    }

    @Override
    public void aliyunPauseMusic()
    {
        mainServer.pauseMusic();
    }

    @Override
    public void aliyunPlayNext()
    {
        mainServer.nextMusic();
    }

    @Override
    public void aliyunPlayLast()
    {
        mainServer.lastMusic();
    }

    @Override
    public void aliyunSetPlayMode(int mode)
    {
        mainServer.switchPlaymode();
    }

    @Override
    public void showPlayList(String uuid, String from, String size, String direct, String channelId, String channelType, String collectionID,String itemId)
    {
      //mainServer.getPlayList("1", AlinkDevice.getInstance().getDeviceUUID(),from,size,direct,channelId,collectionID,itemId);
        mainServer.getPlayDetailList(uuid,from,size,direct,collectionID);
        mainServer.getMusicCollectionDetailList(uuid,from,size,direct,channelId,collectionID);

    }

    @Override
    public boolean isPlaying()
    {
        return mainServer.isAliyunMusicMediaPlaying();
    }

    @Override
    public void aliyunLoveMusic(int loveid)
    {
        mainServer.loveMusic(""+loveid);
    }

    @Override
    public long getRecompenseTotalTime()
    {
        return mainServer.getMusicTotalTime();
    }

    @Override
    public void cancelLovedMusic(String uuid, String itemId, String channelId)
    {
        mainServer.cancelFavorite(uuid, itemId, channelId);
    }

    @Override
    public void quickPlay(String uuid, String type, String itemId, String collectionId)
    {
        // play
        mainServer.quickPlay(uuid, type, itemId, collectionId);
    }

    @Override
    public void cancelFavorite(String uuid, String itemId, String channelId)
    {
        //cancel fover
        mainServer.cancelFavorite(uuid, itemId, channelId);
    }


    public interface IMesHelper
    {
        void addMessage(String msg, int type);
    }
}
