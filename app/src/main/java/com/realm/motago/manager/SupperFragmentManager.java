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
import com.realm.motago.MotaMainActivityFragment;
import com.realm.motago.MotaMusicFragment;
import com.realm.motago.MotaXiaoZhiFragment;
import com.realm.motago.R;
import com.realm.motago.element.AliyunMusicInfo;
import com.realm.motago.server.ALiYunServer;

import java.util.logging.Handler;

/**
 * Created by Skyyao on 2018\5\4 0004.
 */

public class SupperFragmentManager implements IXiaoZhiClick, MotaMusicFragment.IAliyunMusicHelp
{
    public static final int MOTA_FRAGMENT_MAIN = 0;
    public static final int MOTA_FRAGMENT_MUSIC = 1;
    public static final int MOTA_FRAGMENT_XIAOZHI = 2;


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
                ((MotaMusicFragment) kindFragment[MOTA_FRAGMENT_MUSIC]).setMusicInfo((AliyunMusicInfo) msg.obj);
                if (mCurrentFragmentIndex != MOTA_FRAGMENT_MUSIC)
                {
                    translateToMotagoMusicFragment();
                }
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


    public boolean backToLastFragment()
    {
        if (mCurrentFragmentIndex == MOTA_FRAGMENT_XIAOZHI && ((MotaXiaoZhiFragment) kindFragment[MOTA_FRAGMENT_XIAOZHI]).onNavigationCLick())
        {
            FragmentTransaction transaction = motaManager.beginTransaction();
            transaction.hide(kindFragment[MOTA_FRAGMENT_XIAOZHI]);
            transaction.show(kindFragment[MOTA_FRAGMENT_MAIN]).commit();
            mCurrentFragmentIndex = MOTA_FRAGMENT_MAIN;
            return true;
        }


        return false;
    }

    public void addMessage(String msg, int type)
    {
        mesHelper.addMessage(msg, type);
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
    }

    private void translateToMotagoMusicFragment()
    {
        FragmentTransaction transaction = motaManager.beginTransaction();
        transaction.hide(kindFragment[mCurrentFragmentIndex]);
        transaction.show(kindFragment[MOTA_FRAGMENT_MUSIC]).commit();
        mCurrentFragmentIndex = MOTA_FRAGMENT_MUSIC;
        switchAliyunState(false);
    }

    private void initFragment()
    {
        kindFragment = new Fragment[3];
        MotaMainActivityFragment mainFragment = new MotaMainActivityFragment();
        kindFragment[MOTA_FRAGMENT_MAIN] = mainFragment;
        mesHelper = mainFragment;
        MotaMusicFragment musicFragment = new MotaMusicFragment();
        musicFragment.setMusicHelp(this);
        kindFragment[MOTA_FRAGMENT_MUSIC] = musicFragment;
        MotaXiaoZhiFragment xiaoZhiFragment = new MotaXiaoZhiFragment();
        kindFragment[MOTA_FRAGMENT_XIAOZHI] = xiaoZhiFragment;
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
    public void showPlayList()
    {

    }

    @Override
    public boolean isPlaying()
    {
        return mainServer.isAliyunMusicMediaPlaying();
    }


    public interface IMesHelper
    {
        void addMessage(String msg, int type);
    }
}
